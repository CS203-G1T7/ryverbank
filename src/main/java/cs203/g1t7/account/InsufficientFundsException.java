package cs203.g1t7.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientFundsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(Double cost) {
        super("Your account balance is insufficient for this transaction." + cost);
    }

    public InsufficientFundsException() {
        super("Your account balance is insufficient for this transaction.");
    }
    
}
