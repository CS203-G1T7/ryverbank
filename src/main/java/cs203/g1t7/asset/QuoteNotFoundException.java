package cs203.g1t7.asset;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class QuoteNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public QuoteNotFoundException(String id) {
        super("Could not find quote with id:  " + id);
    }
    
}