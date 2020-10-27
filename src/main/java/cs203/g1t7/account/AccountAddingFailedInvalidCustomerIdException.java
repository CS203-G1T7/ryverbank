package cs203.g1t7.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountAddingFailedInvalidCustomerIdException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccountAddingFailedInvalidCustomerIdException(Integer id) {
        super("User not found with id:  " + id);
    }
}