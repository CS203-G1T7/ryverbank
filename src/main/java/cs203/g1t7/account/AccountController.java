package cs203.g1t7.account;

import java.util.List;
import java.util.ArrayList;

import javax.validation.Valid;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.core.context.SecurityContextHolder;


import cs203.g1t7.users.User;
import cs203.g1t7.users.UserRepository;
import cs203.g1t7.users.UserNotFoundException;
import cs203.g1t7.transaction.Transaction;
import cs203.g1t7.users.CustomUserDetailsService;

@RestController
public class AccountController {
    private AccountService accountService;
    private AccountRepository accounts;
    private UserRepository users;
    private CustomUserDetailsService cuds = new CustomUserDetailsService(users);
    private UserDetailsManager udm;

    public AccountController(AccountService cs, AccountRepository accounts, UserRepository users){
        this.accountService = cs;
        this.accounts = accounts;
        this.users = users;
    }

    /**
     * List all accounts in the system
     * @return list of all accounts that the user has
     */
    @GetMapping("/api/accounts")
    public List<Account> getAccounts(){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Account> all = accountService.listAccounts();
        List<Account> seen = new ArrayList<Account>();
        // ROLE_USER can only see his/her own account
        if(user.getAuthority().equals("ROLE_USER")) {
            for(int i = 0; i < all.size(); i++) {
                if(all.get(i).getCustomer_id() == user.getId()) {
                    seen.add(all.get(i));
                }
            }
            return seen;
        }

        return all;
    }

    /**
     * Search for account with the given id and given authorisation
     * If there is no account with the given "id", throw a AccountNotFoundException
     * @param id
     * @return account with the given id
     */
    @GetMapping("/api/accounts/{id}")
    public Account getAccount(@PathVariable int id) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account account = accountService.getAccount(id);
        if(account == null) throw new AccountNotFoundException(id);
        // do not return Account that does not belong to the requester
        if(user.getAuthority().equals("ROLE_USER") && (user.getId() != account.getCustomer_id())) throw new AccountForbiddenException(id);

        return account;
    }

    /**
     * Add a new content with POST request to "/accounts"
     * Note the use of @RequestBody
     * @param account
     * @return list of all accounts
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/accounts")
    public Account addAccount(@Valid @RequestBody Account newAccountInfo) {
        Integer user_id = newAccountInfo.getCustomer_id();
        // customer does not exist do not add account
        if(!users.existsById(user_id)) throw new AccountAddingFailedInvalidCustomerIdException(user_id);
        User user = users.findById(user_id).get();
        // set proper fields to be added into the new account
        newAccountInfo.setTransactions(new ArrayList<Transaction>());
        newAccountInfo.setAvailable_balance(newAccountInfo.getBalance());
        newAccountInfo.setOriginal_balance(newAccountInfo.getBalance());

        return accountService.addAccount(newAccountInfo);
    }

}
