package cs203.g1t7.content;

import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class ContentServiceImpl implements ContentService {
   
    private ContentRepository contents;
    

    public ContentServiceImpl(ContentRepository contents){
        this.contents = contents;
    }

    @Override
    public List<Content> listContents() {
        return contents.findAll();
    }

    
    @Override
    public Content getContent(Integer id){
        return contents.findById(id).orElse(null);
    }
    
    @Override
    public Content addContent(Content content) {
        return contents.save(content);
    }
    
    @Override
    public Content updateContent(Integer id, Content newContentInfo){
        return contents.findById(id).map(content -> {content.setTitle(newContentInfo.getTitle());
                                                    content.setSummary(newContentInfo.getSummary()); 
                                                    content.setLink(newContentInfo.getLink());
                                                    content.setContent(newContentInfo.getContent());
                                                    content.setApproved(newContentInfo.getApproved());
            return contents.save(content);
        }).orElse(null);
    }

    /**
     * Remove a content with the given id
     */
    @Override
    public void deleteContent(Integer id){
        contents.deleteById(id);
    }
}