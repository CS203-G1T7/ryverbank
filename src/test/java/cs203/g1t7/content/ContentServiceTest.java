package cs203.g1t7.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ContentServiceTest {
    @Mock 
    private ContentRepository contents; 

    @InjectMocks
    private ContentServiceImpl contentService;
    
    
    @Test
    void addContent_NewContent_ReturnSavedContent(){
        // arrange ***
        Content content = new Content("title", "summary", "content", "link", true);

        // mock the "save" operation 
        when(contents.save(any(Content.class))).thenReturn(content);

        // act ***
        Content savedContent = contentService.addContent(content);
        
        // assert ***
        assertNotNull(savedContent);
        verify(contents).save(content);
    }

    @Test
    void updateContent_NotFound_ReturnNull(){
        Content content = new Content("title", "summary", "content", "link", true);
        Integer contentId = 10;
        when(contents.findById(contentId)).thenReturn(Optional.empty());
        
        Content updatedContent = contentService.updateContent(contentId, content);
        
        assertNull(updatedContent);
        verify(contents).findById(contentId);
    }

    @Test
    void listContent_ReturnAllContent(){
        List<Content> listContent = contentService.listContents();
        verify(contents).findAll();
    }
}