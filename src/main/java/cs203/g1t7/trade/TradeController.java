package cs203.g1t7.trade;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Iterator;
import javax.validation.Valid;

import java.time.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import cs203.g1t7.transaction.*;
import cs203.g1t7.account.*;
import cs203.g1t7.users.*;
import cs203.g1t7.asset.*;

import org.springframework.dao.EmptyResultDataAccessException;

@RestController
public class TradeController {
    private TradeRepository trade;
    private TransactionRepository transactions;
    private AccountRepository accounts;
    private PortfolioRepository portfolio;
    private AccountService accountService;
    private QuoteRepository quote;
    private QuoteController quoteCtrl;
    private AssetController assetCtrl;
    private AssetRepository assets;

    public TradeController(AssetController assetCtrl, AccountService accountService, QuoteRepository quote, QuoteController quoteCtrl, PortfolioRepository portfolio, TransactionRepository transactions, AccountRepository accounts, TradeRepository trade){
        this.transactions = transactions;
        this.accounts = accounts;
        this.portfolio = portfolio;
        this.trade = trade;
        this.accountService = accountService;
        this.quote = quote;
        this.quoteCtrl = quoteCtrl;
        this.assetCtrl = assetCtrl;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/trades")
    public Trade addTrade(@Valid @RequestBody Trade newTrade) {
        newTrade.setDate(Instant.now().toEpochMilli());
        newTrade.setStatus("open");
        newTrade.setFilled_quantity(0);

        Integer account_id = newTrade.getAccount_id();

        Account buyer = accountService.getAccount(account_id);
        
        if (buyer == null) throw new AccountNotFoundException(account_id);

        int quantity = newTrade.getQuantity();
        
        if (quantity % 100 != 0) throw new InvalidTradeException("Buy or sell have to be in multiples of 100");

        String action = newTrade.getAction();

        if (!action.equals("buy") && !action.equals("sell")) {
            throw new InvalidTradeException("Invalid action parameter");
        }
        
        updateTrade();
        processTrade(newTrade);
        trade.save(newTrade);
        return newTrade;
    }

    @GetMapping("/api/trades/{t_id}")
    public Trade getTrade(@PathVariable (value = "t_id") Integer t_id) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Integer userId = user.getId();

        if(userId == null) throw new TradeForbiddenException();

        Optional<Trade> temp = trade.findByIdAndCustomerId(t_id, userId);
        Trade tempTrade;

        try {
            tempTrade = temp.get();
        } catch (NoSuchElementException e) {
            throw new TradeNotFoundException(t_id);
        }

        return tempTrade;
    }

    @DeleteMapping("/api/trades/{id}")
    public void deleteTrade(@PathVariable Integer id){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try{
            Trade temp = trade.findById(id).get();
            if(user.getId() != temp.getCustomer_id()) {
                throw new TradeForbiddenException(id);
            }
            if (temp.getStatus().equals("open")) temp.setStatus("cancelled");
            else throw new InvalidTradeException("Cannot cancel filled trade with id: " + id);
            trade.save(temp);
         }catch(NoSuchElementException e) {
            throw new TradeNotFoundException(id);
         }
    }

    public void processTrade(Trade newTrade) {
        Instant nowUtc = Instant.now();
        ZoneId timeZone = ZoneId.of("Asia/Singapore");
        ZonedDateTime nowAsiaSingapore = ZonedDateTime.ofInstant(nowUtc, timeZone);

        int hour = nowAsiaSingapore.getHour();
        if (hour < 9 || hour >= 22) {
            ZonedDateTime tradeSubmit = ZonedDateTime.ofInstant(Instant.ofEpochMilli(newTrade.getDate()), timeZone);
            if (tradeSubmit.getDayOfYear() <= nowAsiaSingapore.getDayOfYear() && tradeSubmit.getYear() <= nowAsiaSingapore.getYear()) {
                if (tradeSubmit.getHour() < 17) newTrade.setStatus("expired");
            }
            return;
        }

        double price;
        double cost;
        boolean marketTrade = false;
        int quantity = newTrade.getQuantity() - newTrade.getFilled_quantity();
        int account_id = newTrade.getCustomer_id();
        Quote tempQuote;
        if (quote.findBySymbol(newTrade.getSymbol()) == null) tempQuote = quoteCtrl.getQuote(newTrade.getSymbol());
        else tempQuote = quote.findBySymbol(newTrade.getSymbol());
        Integer buyerId = newTrade.getAccount_id();
        Account buyer = accountService.getAccount(buyerId);
        
        Portfolio userPortfolio = portfolio.findByCustomerId(account_id);  
        if (userPortfolio == null) userPortfolio = assetCtrl.makePortfolio(account_id);      
        Optional<List<Asset>> buyerPortfolio = userPortfolio.getAssets();
        Iterator<Asset> portfolioIter = null;
        if (buyerPortfolio.isPresent()) portfolioIter = buyerPortfolio.get().iterator();

        String action = newTrade.getAction();

        if (action.equals("buy")) {
            price = newTrade.getBid();
            if (price == 0) {
                price = tempQuote.getAsk();
                marketTrade = true;
            }
            cost = price * quantity;
            double tempCost = 0;
            if (marketTrade) {
                if (tempQuote.getAsk_volume() >= quantity && cost < buyer.getBalance()) {
                    newTrade.setStatus("filled");
                    newTrade.setFilled_quantity(newTrade.getFilled_quantity() + quantity); 
                    updateQuote(tempQuote, "ask", tempQuote.getAsk_volume() - newTrade.getQuantity());
                    tempCost = cost;
                } else {
                    newTrade.setStatus("partial-filled");
                    if (cost < buyer.getBalance()) {
                        newTrade.setFilled_quantity(tempQuote.getAsk_volume());
                        tempCost = price * tempQuote.getAsk_volume();
                    } else {
                        newTrade.setFilled_quantity(newTrade.getFilled_quantity() + ((int)((int)(buyer.getBalance() / tempQuote.getAsk()/100)) * 100));
                        tempCost = price * ((int)((int)(buyer.getBalance() / tempQuote.getAsk()/100)) * 100);
                    }
                    updateQuote(tempQuote, "ask", tempQuote.getAsk_volume() - newTrade.getFilled_quantity()); 
                }
            } else {
                List<Trade> listCheck = trade.findBySymbol(newTrade.getSymbol());
                Collections.sort(listCheck, Comparator.comparing(Trade::getDate).thenComparing(Trade::getAsk));
                Iterator<Trade> listIter = listCheck.iterator();
                while (newTrade.getFilled_quantity() <= newTrade.getQuantity() && listIter.hasNext()) {
                    Trade tempAsk = listIter.next();
                    if (tempAsk.getStatus().equals("partial-filled") || tempAsk.getStatus().equals("open")) {
                        if (tempAsk.getAsk() <= newTrade.getBid()) {
                            int temp = Math.min(tempAsk.getQuantity() - tempAsk.getFilled_quantity(), newTrade.getQuantity());
                            double temp_tempCost = temp * tempAsk.getAsk();
                            while (tempCost >= buyer.getBalance()) {
                                temp = temp - 100;
                                temp_tempCost = temp * tempAsk.getAsk();
                            }
                            tempAsk.setFilled_quantity(tempAsk.getFilled_quantity() + temp);
                            newTrade.setFilled_quantity(temp);
                            if (tempAsk.getFilled_quantity() > 0) tempAsk.setStatus("partial-filled");
                            if (tempAsk.getFilled_quantity() == tempAsk.getQuantity()) tempAsk.setStatus("filled");
                            updateQuote(tempQuote, "ask", tempQuote.getAsk_volume() - temp);
                            Transaction newTransactions = new Transaction(-1, tempAsk.getAccount_id(), cost);
                            updateBalanceReceiver(tempAsk.getAccount_id(), newTransactions);
                            tempCost = tempCost + temp_tempCost;
                            
                            Portfolio sellerPortfolio = portfolio.findByCustomerId(tempAsk.getCustomer_id());
                            Iterator<Asset> sellerIter = sellerPortfolio.getAssets().get().iterator();
                            while (sellerIter.hasNext()) {
                                Asset tempSeller = sellerIter.next();
                                if (tempSeller.getCode().equals(newTrade.getSymbol())) {
                                    double temp_value = tempSeller.getQuantity() * tempAsk.getAsk();
                                    if (!tempAsk.getStatus().equals("open")) sellerPortfolio.setTotal_gain_loss(sellerPortfolio.getTotal_gain_loss() + (newTrade.getFilled_quantity() / tempAsk.getQuantity() * tempSeller.getGain_loss()));
                                    if (tempSeller.getQuantity() > 0) tempSeller.setGain_loss(tempSeller.getValue() - temp_value);
                                    tempSeller.setQuantity(tempSeller.getQuantity() - newTrade.getFilled_quantity());
                                    tempSeller.setAvg_price((tempSeller.getCurrent_price() * tempSeller.getCounter() + tempAsk.getAsk()) / (tempSeller.getCounter() + 1));
                                    tempSeller.setCounter(tempSeller.getCounter() + 1);
                                    tempSeller.setCurrent_price(tempAsk.getAsk());
                                    tempSeller.setValue(tempSeller.getQuantity() * tempAsk.getAsk());
                                    newTrade.setAvg_price(tempSeller.getAvg_price());         
                                } 
                            }
                            sellerIter = sellerPortfolio.getAssets().get().iterator();
                            double tempGainLoss = 0;
                            while (sellerIter.hasNext()) {
                                Asset tempSellerAsset = sellerIter.next();
                                if (tempSellerAsset.getQuantity() > 0) tempGainLoss = tempGainLoss + tempSellerAsset.getGain_loss();
                                else tempSellerAsset.setGain_loss(0);
                            }
                            sellerPortfolio.setUnrealized_gain_loss(tempGainLoss);
                            updatePortfolio(sellerPortfolio, sellerPortfolio.getAssets());
                        }
                    }
                }
                if (newTrade.getFilled_quantity() == newTrade.getQuantity()) newTrade.setStatus("filled");
                updateQuote(tempQuote, "bid", tempQuote.getBid_volume() + (newTrade.getQuantity() - newTrade.getFilled_quantity()));
            }
 
            boolean assetFound = false;
            if (buyerPortfolio.isPresent()) {
                while (portfolioIter.hasNext()) {
                    Asset temp = portfolioIter.next();
                    if (temp.getCode().equals(newTrade.getSymbol())) {
                        double temp_value = temp.getQuantity() * price;
                        if (temp.getQuantity() > 0) temp.setGain_loss(temp.getValue() - temp_value);
                        temp.setQuantity(temp.getQuantity() + newTrade.getFilled_quantity());
                        temp.setAvg_price((temp.getCurrent_price() * temp.getCounter() + price) / (temp.getCounter() + 1));
                        temp.setCounter(temp.getCounter() + 1);
                        temp.setCurrent_price(price);
                        temp.setValue(temp.getQuantity() * price);
                        newTrade.setAvg_price(temp.getAvg_price());
                        assetFound = true;
                    } 
                }
                portfolioIter = buyerPortfolio.get().iterator();
                double tempGainLoss = 0;
                while (portfolioIter.hasNext()) {
                    Asset temp = portfolioIter.next();
                    if (temp.getQuantity() > 0) tempGainLoss = tempGainLoss + temp.getGain_loss();
                    else temp.setGain_loss(0);
                }
                userPortfolio.setUnrealized_gain_loss(tempGainLoss);
            }
            if (!assetFound) {
                if (buyerPortfolio.isPresent()) buyerPortfolio.get().add(new Asset(account_id, newTrade.getSymbol(), newTrade.getFilled_quantity(), price, userPortfolio));
                else {
                    List<Asset> tempList = new ArrayList<>();
                    Asset tempAsset = new Asset(account_id, newTrade.getSymbol(), newTrade.getFilled_quantity(), price, userPortfolio);
                    tempList.add(tempAsset);
                    buyerPortfolio = Optional.of(tempList);
                }
                newTrade.setAvg_price(price);
            }
            if (!newTrade.getStatus().equals("open")) {
                Transaction newTransactions = new Transaction(buyerId, -1, tempCost);
                updateBalanceSender(buyerId, newTransactions);
            }
        } else {
            price = newTrade.getAsk();
            if (price == 0) {
                price = tempQuote.getBid();
                marketTrade = true;
            }
            cost = price * quantity;
            double tempCost = 0;
            boolean portFound = false;
            if (!buyerPortfolio.isPresent()) throw new InvalidTradeException("Assets not found on portfolio");
            portfolioIter = buyerPortfolio.get().iterator();
            while (portfolioIter.hasNext()) {
                Asset temp = portfolioIter.next();
                if (temp.getCode().equals(newTrade.getSymbol())) {
                    if (temp.getQuantity() < newTrade.getQuantity()) throw new InvalidTradeException("Amount of assets not enough");
                    portFound = true;
                    break;
                }
            }
            if (!portFound) throw new InvalidTradeException("Assets not found on portfolio");
            if (marketTrade) {
                if (tempQuote.getBid_volume() >= quantity && cost < buyer.getBalance()) {
                    newTrade.setStatus("filled");
                    newTrade.setFilled_quantity(newTrade.getFilled_quantity() + quantity); 
                    updateQuote(tempQuote, "bid", tempQuote.getBid_volume() - newTrade.getQuantity());
                    tempCost = cost;
                } else {
                    newTrade.setStatus("partial-filled");
                    if (cost < buyer.getBalance()) {
                        newTrade.setFilled_quantity(tempQuote.getBid_volume());
                        tempCost = price * tempQuote.getBid_volume();
                    } else {
                        newTrade.setFilled_quantity(newTrade.getFilled_quantity() + ((int)((int)(buyer.getBalance() / tempQuote.getBid()/100)) * 100));
                        tempCost = price * ((int)((int)(buyer.getBalance() / tempQuote.getBid()/100)) * 100);
                    }
                    updateQuote(tempQuote, "bid", tempQuote.getBid_volume() - newTrade.getFilled_quantity()); 
                }
            } else {
                List<Trade> listCheck = trade.findBySymbol(newTrade.getSymbol());
                Collections.sort(listCheck, Comparator.comparing(Trade::getDate).thenComparing(Trade::getBid, (s1, s2) -> { 
                                                                                                    return s2.compareTo(s1);
                                                                                                }));
                Iterator<Trade> listIter = listCheck.iterator();
                while (newTrade.getFilled_quantity() <= newTrade.getQuantity() && listIter.hasNext()) {
                    Trade tempBid = listIter.next();
                    if ((tempBid.getStatus().equals("partial-filled") || tempBid.getStatus().equals("open")) && tempBid.getBid() != 0) {
                        if (tempBid.getBid() >= newTrade.getBid()) {
                            int temp = Math.min(tempBid.getQuantity() - tempBid.getFilled_quantity(), newTrade.getQuantity());
                            double temp_tempCost = temp * tempBid.getBid();
                            while (tempCost >= accountService.getAccount(tempBid.getAccount_id()).getBalance()) {
                                temp = temp - 100;
                                temp_tempCost = temp * tempBid.getBid();
                            }
                            tempBid.setFilled_quantity(tempBid.getFilled_quantity() + temp);
                            newTrade.setFilled_quantity(temp);
                            if (tempBid.getFilled_quantity() == tempBid.getQuantity()) tempBid.setStatus("filled");
                            updateQuote(tempQuote, "bid", tempQuote.getBid_volume() - temp);
                            Transaction newTransactions = new Transaction(tempBid.getAccount_id(), -1, tempCost);
                            updateBalanceSender(tempBid.getAccount_id(), newTransactions);
                            tempCost = tempCost + temp_tempCost;

                            Portfolio purchaserPortfolio = portfolio.findByCustomerId(tempBid.getCustomer_id());
                            Iterator<Asset> purchaserIter = purchaserPortfolio.getAssets().get().iterator();
                            while (purchaserIter.hasNext()) {
                                Asset tempPurchase = purchaserIter.next();
                                if (tempPurchase.getCode().equals(newTrade.getSymbol())) {
                                    double temp_value = tempPurchase.getQuantity() * tempBid.getBid();
                                    if (tempPurchase.getQuantity() > 0) tempPurchase.setGain_loss(tempPurchase.getValue() - temp_value);
                                    tempPurchase.setQuantity(tempPurchase.getQuantity() - newTrade.getFilled_quantity());
                                    tempPurchase.setAvg_price((tempPurchase.getCurrent_price() * tempPurchase.getCounter() + tempBid.getBid()) / (tempPurchase.getCounter() + 1));
                                    tempPurchase.setCounter(tempPurchase.getCounter() + 1);
                                    tempPurchase.setCurrent_price(tempBid.getBid());
                                    tempPurchase.setValue(tempPurchase.getQuantity() * tempBid.getBid());
                                    newTrade.setAvg_price(tempPurchase.getAvg_price());
                                } 
                            }
                            purchaserIter = purchaserPortfolio.getAssets().get().iterator();
                            double tempGainLoss = 0;
                            while (purchaserIter.hasNext()) {
                                Asset tempPurchaseAsset = purchaserIter.next();
                                if (tempPurchaseAsset.getQuantity() > 0) tempGainLoss = tempGainLoss + tempPurchaseAsset.getGain_loss();
                                else tempPurchaseAsset.setGain_loss(0);
                            }
                            purchaserPortfolio.setUnrealized_gain_loss(tempGainLoss);
                        }
                    }
                }
                if (newTrade.getFilled_quantity() == newTrade.getQuantity()) newTrade.setStatus("filled");
                updateQuote(tempQuote, "ask", tempQuote.getAsk_volume() + (newTrade.getQuantity() - newTrade.getFilled_quantity()));
            }

            portfolioIter = buyerPortfolio.get().iterator();
            while (portfolioIter.hasNext()) {
                Asset temp = portfolioIter.next();
                if (temp.getCode().equals(newTrade.getSymbol())) {
                    double temp_value = temp.getQuantity() * price;
                    if (!newTrade.getStatus().equals("open")) userPortfolio.setTotal_gain_loss(userPortfolio.getTotal_gain_loss() + (newTrade.getFilled_quantity() / temp.getQuantity() * temp.getGain_loss())); 
                    if (temp.getQuantity() > 0) temp.setGain_loss(temp_value - temp.getValue());
                    temp.setQuantity(temp.getQuantity() - newTrade.getFilled_quantity());
                    temp.setAvg_price((temp.getCurrent_price() * temp.getCounter() + price) / (temp.getCounter() + 1));
                    temp.setCounter(temp.getCounter() + 1);
                    temp.setCurrent_price(price);
                    temp.setValue(temp.getQuantity() * price);
                    newTrade.setAvg_price(temp.getAvg_price());
                } 
            }
            portfolioIter = buyerPortfolio.get().iterator();
            double tempGainLoss = 0;
            while (portfolioIter.hasNext()) {
                Asset temp = portfolioIter.next();
                if (temp.getQuantity() > 0) tempGainLoss = tempGainLoss + temp.getGain_loss();
                else temp.setGain_loss(0);
            }
            userPortfolio.setUnrealized_gain_loss(tempGainLoss);
            if (!newTrade.getStatus().equals("open")) {
                Transaction newTransactions = new Transaction(-1, buyerId, cost);
                updateBalanceReceiver(buyerId, newTransactions);
            }
        }
        updatePortfolio(userPortfolio, buyerPortfolio);
    }

    public void updateTrade() {
        List<Trade> list = trade.findByStatusOrStatus("open", "partial-filled");
        Iterator listIter = list.iterator();
        while (listIter.hasNext()) {
            Trade tempTrade = (Trade) listIter.next();
            _updateTrade(tempTrade);
        }
    }

    public Trade _updateTrade(Trade temp) {
        processTrade(temp);
        return trade.save(temp);
    }

    public Portfolio updatePortfolio(Portfolio user, Optional<List<Asset>> temp) {
        user.setAssets(temp);
        return portfolio.save(user);
    }

    public Quote updateQuote(Quote temp, String condition, Integer volume) {
        if (condition.equals("ask")) temp.setAsk_volume(volume);
        else temp.setBid_volume(volume);
        return quote.save(temp);
    }

    public Transaction updateBalanceSender(Integer id_from, Transaction t) {
        if (id_from == -1) return transactions.save(t);        
        
        Account from = accounts.findById(id_from).get();
        double amount = t.getAmount();

        return accounts.findById(id_from).map(account ->{
            account.setAvailable_balance(from.getAvailable_balance() - amount);
            account.setBalance(from.getBalance() - amount);
            t.setAccount(account);
            return transactions.save(t);
        }).orElseThrow(() -> new AccountNotFoundException(id_from));
    }

    public Transaction updateBalanceReceiver(Integer id_to, Transaction t) {
        if (id_to == -1) return transactions.save(t);
        
        Account to = accounts.findById(id_to).get();
        double amount = t.getAmount();

        return accounts.findById(id_to).map(account ->{
            account.setAvailable_balance(to.getAvailable_balance() + amount);
            account.setBalance(to.getBalance() + amount);
            t.setAccount(account);
            return transactions.save(t);
        }).orElseThrow(() -> new AccountNotFoundException(id_to));
    }
}