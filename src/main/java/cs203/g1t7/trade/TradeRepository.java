package cs203.g1t7.trade;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Integer>{
    // additional derived queries specified here will be implemented by Spring Data JPA
    // start the derived query with "findBy", then reference the entity attributes you want to filter
    List<Trade> findByAccountId(Integer accountId);
    Optional<Trade> findByIdAndAccountId(Integer id, Integer accountId);
}