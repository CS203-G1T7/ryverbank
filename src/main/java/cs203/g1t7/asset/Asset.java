package cs203.g1t7.asset;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.*;
import javax.validation.constraints.*;

import cs203.g1t7.account.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@IdClass(AssetId.class)
public class Asset {

    @NotNull (message = "Buyer account must not be null.")
    @JsonIgnore
    private @Id Integer customer_id;

    @NotNull (message = "Stock symbol must not be null.")
    @Column (name = "stock_symbol")
    private @Id String code;
    
    @NotNull (message = "Must specify transaction amount.")
    private int quantity;

    private double avg_price;

    @JsonIgnore
    private int counter = 0;

    private double current_price;

    private double value;

    private double gain_loss;

    @ManyToOne 
    @JoinColumn (name = "account_id", nullable = false)
    @JsonIgnore
    private Portfolio portfolio;
    
    public Asset(Integer customer_id, String code, int quantity, double current_price, Portfolio portfolio){
        this.customer_id = customer_id;
        this.code = code;
        this.quantity = quantity;
        this.current_price = current_price;
        this.avg_price = current_price;
        this.value = current_price * quantity;
        this.counter = 0;
        this.gain_loss = 0;
        this.portfolio = portfolio;
    }

}