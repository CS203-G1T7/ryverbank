package cs203.g1t7.users;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) 
public class DuplicateNricFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateNricFoundException(String nric) {
        super("A user with this nric: " + nric + "already exists.");
    }
    
}