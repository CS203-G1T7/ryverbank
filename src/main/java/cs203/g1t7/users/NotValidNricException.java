package cs203.g1t7.users;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE) 
public class NotValidNricException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotValidNricException(String nric) {
        super("Could not find nric " + nric);
    }
    
}