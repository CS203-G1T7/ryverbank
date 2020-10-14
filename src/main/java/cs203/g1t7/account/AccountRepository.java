package cs203.g1t7.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository <Account, Integer> {
    @Query("select account from Account account where account.customer_id = ?1")
    List<Account> findByCustomerId(Integer id);
}
