package cs203.g1t7.transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer>{
    // additional derived queries specified here will be implemented by Spring Data JPA
    // start the derived query with "findBy", then reference the entity attributes you want to filter
    List<Transaction> findByAccountId(Integer accountId);
    Optional<Transaction> findByIdAndAccountId(Integer id, Integer accountId);
}