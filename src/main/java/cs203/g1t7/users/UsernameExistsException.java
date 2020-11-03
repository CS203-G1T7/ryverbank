package cs203.g1t7.users;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) 
public class UsernameExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UsernameExistsException(String username) {
        super("A user with this username: " + username + "already exists.");
    }
    
}