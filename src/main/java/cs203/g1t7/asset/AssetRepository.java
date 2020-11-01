package cs203.g1t7.asset;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface AssetRepository extends JpaRepository<Asset, Integer>{
    // additional derived queries specified here will be implemented by Spring Data JPA
    // start the derived query with "findBy", then reference the entity attributes you want to filter
    @Query("select asset from Asset asset where asset.customer_id = ?1")
    Optional<Asset> findByCustomerId(Integer customerId);
}