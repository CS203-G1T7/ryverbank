package cs203.g1t7.trade;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface TradeRepository extends JpaRepository<Trade, Integer>{
    // additional derived queries specified here will be implemented by Spring Data JPA
    // start the derived query with "findBy", then reference the entity attributes you want to filter
    Trade findTradeById(Integer id);
    // List<Trade> findByBuyer(Integer buyer);
    @Query("select trade from Trade trade where trade.customer_id = ?2 and trade.id = ?1")
    Optional<Trade> findByIdAndCustomerId(Integer id, Integer accId);
    @Query("select trade from Trade trade where trade.account_id = ?2 and trade.id = ?1")
    Optional<Trade> findByIdAndAccountId(Integer id, Integer buyer);
    List<Trade> findByStatusOrStatus(String status1, String status2);
}