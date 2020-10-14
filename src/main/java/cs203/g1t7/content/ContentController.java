package cs203.g1t7.content;

import java.util.List;
import java.util.ArrayList;

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
import org.springframework.security.core.context.SecurityContextHolder;


import cs203.g1t7.users.User;
import cs203.g1t7.users.UserRepository;
import cs203.g1t7.users.CustomUserDetailsService;

@RestController
public class ContentController {
    private ContentService contentService;
    private ContentRepository cp;
    private UserRepository up;

    public ContentController(ContentService cs){
        this.contentService = cs;
    }

    /**
     * List all content in the system
     * @return list of all contents
     */
    @GetMapping("/contents")
    public List<Content> getContents(){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Content> all = contentService.listContents();
        List<Content> approved = new ArrayList<Content>();
        if(user.getAuthority().equals("ROLE_USER")) {
            for(int i = 0; i < all.size(); i++) {
                if(all.get(i).getApproved()) {
                    approved.add(all.get(i));
                }
            }
            return approved;
        }
        return all;
    }

    /**
     * Search for content with the given id and given authorisation
     * If there is no content with the given "id", throw a ContentNotFoundException
     * @param id
     * @return content with the given id
     */
    @GetMapping("/contents/{id}")
    public Content getContent(@PathVariable Integer id) {
        Content content = contentService.getContent(id);
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(content == null) throw new ContentNotFoundException(id);

        if(!content.getApproved() && user.getAuthority().equals("ROLE_USER")) throw new ContentForbiddenException(id);

        return content;
    }

    /**
     * Add a new content with POST request to "/contents"
     * Note the use of @RequestBody
     * @param content
     * @return list of all contents
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/contents")
    public Content addContent(@Valid @RequestBody Content newContentInfo) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(user.getAuthority().equals("ROLE_ANALYST")) {
            newContentInfo.setApproved(false);
        }
        return contentService.addContent(newContentInfo);
    }

    /**
     * If there is no content with the given "id", throw a ContentNotFoundException
     * @param id
     * @param newContentInfo
     * @return the updated, or newly added content
     */
    @PutMapping("/contents/{id}")
    public Content updateContent(@PathVariable Integer id, 
                                @Valid @RequestBody Content newContentInfo){
        Content content = contentService.updateContent(id, newContentInfo);
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(content == null) throw new ContentNotFoundException(id);
        if(user.getAuthority().equals("ROLE_USER")) throw new ContentForbiddenException(id);
        return content;
    }

    /**
     * Remove a content with the DELETE request to "/contents/{id}"
     * If there is no content with the given "id", throw a ContentNotFoundException
     * @param id
     */
    @DeleteMapping("/contents/{id}")
    public void deleteContent(@PathVariable Integer id){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try{
            if(user.getAuthority().equals("ROLE_USER")) {
                throw new ContentForbiddenException(id);
            }
            contentService.deleteContent(id);
         }catch(EmptyResultDataAccessException e) {
            throw new ContentNotFoundException(id);
         }
    }
}