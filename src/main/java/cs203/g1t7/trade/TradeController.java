package cs203.g1t7.trade;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import cs203.g1t7.transaction.*;
import cs203.g1t7.account.*;
import cs203.g1t7.users.*;
import cs203.g1t7.asset.*;

@RestController
public class TradeController {
    private TradeRepository trade;
    private TransactionRepository transactions;
    private AccountRepository accounts;
    private AccountService accountService;
    private QuoteController quote;

    public TradeController(TransactionRepository transactions, AccountRepository accounts, TradeRepository trade){
        this.transactions = transactions;
        this.accounts = accounts;
        this.trade = trade;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/trades")
    public Trade addTrade(@PathVariable Integer account_id, @Valid @RequestBody Trade newTrade) {
        Account buyer = accounts.findById(account_id).get();
        
        if (buyer == null) throw new AccountNotFoundException(id);

        int quantity = newTrade.getQuantity();
        
        if (quantity % 100 != 0) throw new InvalidTradeException("Buy or sell have to be in multiples of 100");

        String action = newTrade.getAction();

        if (!action.equals("buy") || !action.equals("sell") {
            throw new InvalidTradeException("Invalid action parameter");
        }

        double price;
        double cost;
        boolean marketTrade = false;

        if (action.equals("buy")) {
            price = newTrade.getBid();
            if (price = 0) {
                price = quote.getQuote(newTrade.getSymbol()).getPrice();
                marketTrade = true;
            }
            cost = price * quantity;
            if (cost > buyer.getBalance()) throw new InsufficientFundsException();
            if (marketTrade) {
                newTrade.setStatus("filled");
                // TODO: Generate transactions
                updateBalanceSender(account_id, newTransactions);
            }
        } else {
            price = newTrade.getAsk();
            if (price = 0) {
                price = quote.getQuote(newTrade.getSymbol()).getPrice();
                marketTrade = true;
            }
            cost = price * quantity;
            if (marketTrade) {
                newTrade.setStatus("filled");
                // TODO: Generate transactions
                updateBalanceSender(account_id, newTransactions);
            }
        }


    }

    @GetMapping("/trades/{t_id}")
    public Optional<Trade> getTrade(@PathVariable (value = "t_id") Integer t_id) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(id != user.getId()) throw new TradeForbiddenException();

        if(!accounts.existsById(id)) {
            throw new AccountNotFoundException(id);
        }
        return trade.findByIdAndAccountId(t_id, id);
    }

    @DeleteMapping("/trades/{id}")
    public void deleteTrade(@PathVariable Integer id){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try{
            if(user.getAuthority().equals("ROLE_USER")) {
                throw new TradeForbiddenException(id);
            }
            trade.deleteById(id);
         }catch(EmptyResultDataAccessException e) {
            throw new TradeNotFoundException(id);
         }
    }

    public Transaction updateBalanceSender(Integer id_from, Transaction t) {
        Account from = accounts.findById(id_from).get();
        double amount = t.getAmount();

        return accounts.findById(id_from).map(account ->{
            account.setAvailable_balance(from.getAvailable_balance() - amount);
            account.setBalance(from.getBalance() - amount);
            t.setAccount(account);
            return transactions.save(t);
        }).orElseThrow(() -> new AccountNotFoundException(id_from));
    }

}