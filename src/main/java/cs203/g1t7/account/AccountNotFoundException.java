package cs203.g1t7.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccountNotFoundException(int id) {
        super("Could not find account " + id);
    }

    public AccountNotFoundException() {
        super("There is no account with customer id");
    }
    
}
