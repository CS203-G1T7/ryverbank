package cs203.g1t7.users;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserForbiddenException() {
        super("Forbidden to view other user's account");
    }
    
}