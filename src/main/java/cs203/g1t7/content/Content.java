package cs203.g1t7.content;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Content {
    private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Long id;

    @NotNull(message = "Content's title should not be null")
    @Size(min = 5, max = 200, message = "Content's title should be at least 5 characters long")
    private String title;
    
    @NotNull(message = "Content's summary should not be null")
    @Size(min = 5, max = 200, message = "Content's summary should be at least 5 characters long")
    private String summary;

    @NotNull(message = "Content's content should not be null")
    @Size(min = 5, message = "Content's content should be at least 5 characters long")
    private String content;

    @NotNull(message = "Content's link should not be null")
    @Size(min = 5, max = 200, message = "Content's link should be at least 5 characters long")
    private String link;

    @NotNull(message = "Content's approval should not be null")
    private boolean approved;
    
    public Content(String title, String summary, String content, String link, boolean approved){
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.link = link;
        this.approved = approved;
    }

    public boolean getApproved() {
        return approved;
    }
    
}