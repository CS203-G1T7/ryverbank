package cs203.g1t7.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TransactionForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TransactionForbiddenException() {
        super("Transaction forbidden");
    }
    
}
