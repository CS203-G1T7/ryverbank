package cs203.g1t7.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository <Content, Integer> {
    List<Content> findByTitle(String title);
}
