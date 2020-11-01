package cs203.g1t7.trade;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTradeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidTradeException(String desc) {
        super(desc);
    }
    
}
