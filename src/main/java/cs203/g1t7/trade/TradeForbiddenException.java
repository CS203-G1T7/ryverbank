package cs203.g1t7.trade;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TradeForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TradeForbiddenException(Integer id) {
        super("Forbidden to access trade " + id);
    }
    public TradeForbiddenException() {
        super("Trade forbidden");
    }
    
}
