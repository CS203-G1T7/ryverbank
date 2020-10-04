package cs203.g1t7.content;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ContentForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ContentForbiddenException(Long id) {
        super("Forbidden to view content " + id);
    }
    public ContentForbiddenException() {
        super("Content forbidden ");
    }
}