package cs203.g1t7.asset;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.*;
import javax.validation.constraints.*;

import cs203.g1t7.trade.Trade;
import cs203.g1t7.asset.Asset;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Portfolio {

    private @Id int customer_id;
    
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asset> assets = new ArrayList<>();

    private double unrealized_gain_loss;

    private double total_gain_loss;

    public Optional<List<Asset>> getAssets() {
        return Optional.ofNullable(assets);
    }

    public void setAssets(final Optional<List<Asset>> assets) {
        if (assets.isPresent()) this.assets = assets.get();
    }
}
