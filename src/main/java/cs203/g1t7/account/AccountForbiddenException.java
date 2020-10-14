package cs203.g1t7.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccountForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccountForbiddenException(int id) {
        super("Forbidden to view account " + id);
    }
    public AccountForbiddenException() {
        super("Account forbidden ");
    }
}