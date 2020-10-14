package cs203.g1t7.content;

import java.util.List;

public interface ContentService {
    List<Content> listContents();
    Content getContent(Integer id);
    Content addContent(Content content);
    Content updateContent(Integer id, Content content);

    /**
     * Change method's signature: do not return a value for delete operation
     * @param id
     */
    void deleteContent(Integer id);
}