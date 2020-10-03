package cs203.g1t7.content;

import java.util.List;

import javax.validation.Valid;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContentController {
    private ContentService contentService;

    public ContentController(ContentService cs){
        this.contentService = cs;
    }

    /**
     * List all content in the system
     * @return list of all contents
     */
    @GetMapping("/contents")
    public List<Content> getContents(){
        return contentService.listContents();
    }

    /**
     * Search for content with the given id
     * If there is no content with the given "id", throw a ContentNotFoundException
     * @param id
     * @return content with the given id
     */
    @GetMapping("/contents/{id}")
    public Content getContent(@PathVariable Long id){
        Content content = contentService.getContent(id);

        // Need to handle "content not found" error using proper HTTP status code
        // In this case it should be HTTP 404
        if(content == null) throw new ContentNotFoundException(id);
        return contentService.getContent(id);
    }

    @GetMapping("/contents/{id}/{approved}")
    public Content getApprovedContent(@PathVariable Long id){
        Content content = contentService.getContent(id);

        // Need to handle "content not found" error using proper HTTP status code
        // In this case it should be HTTP 404
        if(content == null) throw new ContentNotFoundException(id);
        if(!content.getApproved()) throw new ContentForbiddenException(id);
        return contentService.getContent(id);
    }

    /**
     * Add a new content with POST request to "/contents"
     * Note the use of @RequestBody
     * @param content
     * @return list of all contents
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/contents")
    public Content addContent(@Valid @RequestBody Content content) {
        return contentService.addContent(content);
    }

    /**
     * If there is no content with the given "id", throw a ContentNotFoundException
     * @param id
     * @param newContentInfo
     * @return the updated, or newly added content
     */
    @PutMapping("/contents/{id}")
    public Content updateContent(@PathVariable Long id, @Valid @RequestBody Content newContentInfo){
        Content content = contentService.updateContent(id, newContentInfo);
        if(content == null) throw new ContentNotFoundException(id);
        
        return content;
    }

    /**
     * For approval of content
     * If there is no content with the given "id", throw a ContentNotFoundException
     * @param id
     * @param newContentInfo
     * @return the updated, or newly added content
     */
    // @PutMapping("/contents/{id}")
    // public Content updateContentApproved(@PathVariable Long id, @Valid @RequestBody Content newContentInfo){
    //     Content content = contentService.updateContent(id, newContentInfo);
    //     if(content == null) throw new ContentNotFoundException(id);
        
    //     return content;
    // }

    /**
     * Remove a content with the DELETE request to "/contents/{id}"
     * If there is no content with the given "id", throw a ContentNotFoundException
     * @param id
     */
    @DeleteMapping("/contents/{id}")
    public void deleteContent(@PathVariable Long id){
        try{
            contentService.deleteContent(id);
         }catch(EmptyResultDataAccessException e) {
            throw new ContentNotFoundException(id);
         }
    }
}