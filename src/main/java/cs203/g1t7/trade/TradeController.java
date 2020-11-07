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
    private AccountService accountService;
    private TradeService tradeService;

    public TradeController(TradeRepository trade, AccountService accountService, TradeService tradeService){
        this.trade = trade;
        this.accountService = accountService;
        this.tradeService = tradeService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/trades")
    public Trade addTrade(@Valid @RequestBody Trade newTrade) {
        newTrade.setDate(Instant.now().toEpochMilli());
        newTrade.setStatus("open");
        newTrade.setFilled_quantity(0);
        newTrade.setCounter(0);

        Integer account_id = newTrade.getAccount_id();

        Account buyer = accountService.getAccount(account_id);
        
        if (buyer == null) throw new AccountNotFoundException(account_id);

        int quantity = newTrade.getQuantity();
        
        if (quantity % 100 != 0) throw new InvalidTradeException("Buy or sell have to be in multiples of 100");

        String action = newTrade.getAction();

        if (!action.equals("buy") && !action.equals("sell")) {
            throw new InvalidTradeException("Invalid action parameter");
        }
        
        tradeService.updateTrade();
        tradeService.processTrade(newTrade, false);
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

    @PutMapping("/api/trades/{id}")
    public Trade updateTrade(@PathVariable Integer id, @Valid @RequestBody Trade newTrade){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try{
            Trade temp = trade.findById(id).get();
            if(user.getId() != temp.getCustomer_id()) {
                throw new TradeForbiddenException(id);
            }
            if (newTrade.getStatus().equals("cancelled")) temp.setStatus("cancelled");
            else throw new InvalidTradeException("Invalid update for trade: " + id);
            return trade.save(temp);
         }catch(NoSuchElementException e) {
            throw new TradeNotFoundException(id);
         }
    }
}
