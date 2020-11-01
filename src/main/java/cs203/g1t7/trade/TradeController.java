package cs203.g1t7.trade;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
        if (hour < 9 || hour >= 17) {
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
                newTrade.setBid(price);
                newTrade.setAsk(price);
            }
            cost = price * quantity;
            double tempCost = 0;
            if (marketTrade || (!marketTrade && (newTrade.getBid() >= tempQuote.getBid()))) {
                if (((!marketTrade && tempQuote.getBid_volume() >= quantity) || (marketTrade && tempQuote.getAsk_volume() >= quantity)) && cost < buyer.getBalance()) {
                    newTrade.setStatus("filled");
                    newTrade.setFilled_quantity(newTrade.getFilled_quantity() + quantity); 
                    if (marketTrade) updateQuote(tempQuote, "ask", tempQuote.getAsk_volume() - newTrade.getQuantity());
                    else updateQuote(tempQuote, "bid", tempQuote.getAsk_volume() - newTrade.getQuantity());
                    tempCost = cost;
                } else {
                    newTrade.setStatus("partial-filled");
                    if (cost < buyer.getBalance()) {
                        if (!marketTrade) { 
                            newTrade.setFilled_quantity(tempQuote.getBid_volume());
                            tempCost = price * tempQuote.getBid_volume();
                        } else { 
                            newTrade.setFilled_quantity(tempQuote.getAsk_volume());
                            tempCost = price * tempQuote.getAsk_volume();
                        }
                    } else {
                        if (!marketTrade) { 
                            newTrade.setFilled_quantity(newTrade.getFilled_quantity() + ((int)((int)(buyer.getBalance() / newTrade.getBid()/100)) * 100));
                            tempCost = price * ((int)((int)(buyer.getBalance() / newTrade.getBid()/100)) * 100);
                        } else {
                            newTrade.setFilled_quantity(newTrade.getFilled_quantity() + ((int)((int)(buyer.getBalance() / newTrade.getAsk()/100)) * 100));
                            tempCost = price * ((int)((int)(buyer.getBalance() / newTrade.getAsk()/100)) * 100);
                        }
                    }
                    if (!marketTrade) updateQuote(tempQuote, "bid", 0); 
                    else updateQuote(tempQuote, "ask", 0); 
                    if (newTrade.getFilled_quantity() == 0) newTrade.setStatus("open");           
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
                Transaction newTransactions = new Transaction(buyerId, -1, tempCost);
                updateBalanceSender(buyerId, newTransactions);
            }
        } else {
            price = newTrade.getAsk();
            if (price == 0) {
                price = tempQuote.getBid();
                marketTrade = true;
                newTrade.setAsk(price);
                newTrade.setBid(price);
            }
            cost = price * quantity;
            boolean portFound = false;
            if (!buyerPortfolio.isPresent()) throw new InvalidTradeException("Assets not found on portfolio");
            while (portfolioIter.hasNext()) {
                Asset temp = portfolioIter.next();
                if (temp.getCode().equals(newTrade.getSymbol())) {
                    if (temp.getQuantity() < newTrade.getQuantity()) throw new InvalidTradeException("Amount of assets not enough");
                    portFound = true;
                    break;
                }
            }
            if (!portFound) throw new InvalidTradeException("Assets not found on portfolio");
            if (!marketTrade && newTrade.getFilled_quantity() == 0) {
                newTrade.setStatus("open");
            }
            if (marketTrade || (!marketTrade && (newTrade.getAsk() <= tempQuote.getAsk()))) {
                if ((!marketTrade && tempQuote.getAsk_volume() >= newTrade.getQuantity()) || (marketTrade && tempQuote.getBid_volume() >= newTrade.getQuantity())) {
                    newTrade.setStatus("filled");
                    newTrade.setFilled_quantity(newTrade.getQuantity());
                    if (marketTrade) updateQuote(tempQuote, "bid", tempQuote.getBid_volume() - newTrade.getQuantity());
                    else updateQuote(tempQuote, "ask", tempQuote.getAsk_volume() - newTrade.getQuantity());
                } else {
                    newTrade.setStatus("partial-filled");
                    if (!marketTrade) {
                        newTrade.setFilled_quantity(tempQuote.getAsk_volume());
                        updateQuote(tempQuote, "ask", 0);
                    } else {
                        newTrade.setFilled_quantity(tempQuote.getBid_volume());
                        updateQuote(tempQuote, "bid", 0);
                    }
                }
                portfolioIter = buyerPortfolio.get().iterator();
                while (portfolioIter.hasNext()) {
                    Asset temp = portfolioIter.next();
                    if (temp.getCode().equals(newTrade.getSymbol())) {
                        double temp_value = temp.getQuantity() * price;
                        if (temp.getQuantity() > 0) temp.setGain_loss(temp_value - temp.getValue());
                        temp.setQuantity(temp.getQuantity() - newTrade.getFilled_quantity());
                        temp.setAvg_price((temp.getCurrent_price() * temp.getCounter() + price) / (temp.getCounter() + 1));
                        temp.setCounter(temp.getCounter() + 1);
                        temp.setCurrent_price(price);
                        temp.setValue(temp.getQuantity() * price);
                        newTrade.setAvg_price(temp.getAvg_price());
                        userPortfolio.setTotal_gain_loss(userPortfolio.getTotal_gain_loss() + temp.getGain_loss());
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