package cs203.g1t7.account;

import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class AccountServiceImpl implements AccountService {
   
    private AccountRepository accounts;
    

    public AccountServiceImpl(AccountRepository accounts){
        this.accounts = accounts;
    }

    @Override
    public List<Account> listAccounts() {
        return accounts.findAll();
    }

    @Override
    public Account getAccount(Integer id){
        return accounts.findById(id).orElse(null);
    }
    
    @Override
    public Account addAccount(Account account) {
        return accounts.save(account);
    }
}