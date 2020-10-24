package cs203.g1t7.account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> listAccounts();
    Account getAccount(Integer id);
    Account addAccount(Account account);
}