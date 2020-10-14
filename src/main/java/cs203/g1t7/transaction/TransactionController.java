package cs203.g1t7.transaction;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;


import cs203.g1t7.account.*;
import cs203.g1t7.users.*;

@RestController
public class TransactionController {
    private TransactionRepository transactions;
    private AccountRepository accounts;
    private AccountService accountService;

    public TransactionController(TransactionRepository transactions, AccountRepository accounts){
        this.transactions = transactions;
        this.accounts = accounts;
    }

    @GetMapping("/accounts/{id}/transactions/{t_id}")
    public Optional<Transaction> getTransactions(@PathVariable (value = "id") Integer id,
                                                @PathVariable (value = "t_id") Integer t_id) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(id != user.getId()) throw new TransactionForbiddenException();

        if(!accounts.existsById(id)) {
            throw new AccountNotFoundException(id);
        }
        return transactions.findByIdAndAccountId(t_id, id);
    }

    @GetMapping("/accounts/{id}/transactions")
    public List<Transaction> getTransactions(@PathVariable Integer id) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(id != user.getId()) throw new TransactionForbiddenException();

        if(!accounts.existsById(id)) {
            throw new AccountNotFoundException(id);
        }
        return transactions.findByAccountId(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accounts/{id}/transactions")
    public Transaction addTransaction(@PathVariable Integer id, @Valid @RequestBody Transaction newTransactionInfo) {
        Account from = accounts.findById(id).get();
        Account to = accounts.findById(newTransactionInfo.getTo()).get();

        if(from == null) throw new AccountNotFoundException(id);
        if(to == null) throw new AccountNotFoundException(newTransactionInfo.getFrom());

        //proceed
        double amount = newTransactionInfo.getAmount();

        if(from.getBalance() < newTransactionInfo.getAmount()) throw new InsufficientFundsException();

        //sufficient balance, proceed w/ the transaction
        Transaction newTransactionInfo2 = new Transaction(from.getId(), to.getId(), amount);
        updateBalanceReceiver(to.getId(), newTransactionInfo);
        updateBalanceSender(from.getId(), newTransactionInfo2);

        return newTransactionInfo;

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

    public Transaction updateBalanceReceiver(Integer id_to, Transaction t) {
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