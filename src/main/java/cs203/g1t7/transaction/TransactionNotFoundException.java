package cs203.g1t7.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class TransactionNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public TransactionNotFoundException(Integer id) {
        super("Could not find transaction with id:  " + id);
    }
    
}