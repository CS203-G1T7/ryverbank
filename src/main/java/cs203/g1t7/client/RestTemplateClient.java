package cs203.g1t7.client;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import cs203.g1t7.content.Content;

@Component
public class RestTemplateClient {
    
    private final RestTemplate template;

    /**
     * Add authentication information for the RestTemplate
     * 
     */
    public RestTemplateClient(RestTemplateBuilder restTemplateBuilder) {
        this.template = restTemplateBuilder
                .basicAuthentication("marktan123", "goodpassword")
                .build();
    }
    /**
     * Get a book with given id
     * 
     * @param URI
     * @param id
     * @return
     */
    public Content getContent(final String URI, final Long id) {
        final Content content = template.getForObject(URI + "/" + id, Content.class);
        return content;
    }

    /**
     * Add a new book
     * 
     * @param URI
     * @param newContent
     * @return
     */
    public Content addContent(final String URI, final Content newContent) {
        final Content returned = template.postForObject(URI, newContent, Content.class);
        
        return returned;
    }

    /**
     * Get a book, but return a HTTP response entity.
     * @param URI
     * @param id
     * @return
     */
    public ResponseEntity<Content> getContentEntity(final String URI, final Long id){
        return template.getForEntity(URI + "/{id}", Content.class, Long.toString(id));
    }
    
}