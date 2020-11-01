package cs203.g1t7.asset;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer>{
    // additional derived queries specified here will be implemented by Spring Data JPA
    @Query("select portfolio from Portfolio portfolio where portfolio.customer_id = ?1")
    Portfolio findByCustomerId(Integer customerId);
}