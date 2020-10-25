package cs203.g1t7.asset;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Integer>{
    // @Query("select asset from Asset asset where asset.customer_id = ?1")
    List<Asset> findByCustomerId(Integer custid);

    // @Query("select asset from Asset asset where asset.buyer_account_id = ?1")
    List<Asset> findByAccountId(Integer acctid);
}