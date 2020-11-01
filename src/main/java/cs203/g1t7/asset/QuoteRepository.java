package cs203.g1t7.asset;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<Quote, String>{
    // additional derived queries specified here will be implemented by Spring Data JPA
    Quote findBySymbol(String symbol);
}