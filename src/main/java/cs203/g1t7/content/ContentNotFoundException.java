package cs203.g1t7.content;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContentNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ContentNotFoundException(Long id) {
        super("Could not find content " + id);
    }
    
}
