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

// import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Asset {
    private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Integer id;

    @NotNull (message = "Buyer account must not be null.")
    @Column (name = "buyer_account_id")
    private Integer buyer;

    @NotNull (message = "Stock symbol must not be null.")
    @Column (name = "stock_symbol")
    private Integer symbol;
    
    @NotNull (message = "Must specify transaction amount.")
    private double amount;
    
    @ManyToOne 
    @JoinColumn (name = "customer_id", nullable = false)
    private Integer customerid;

    public Asset(Integer buyer, Integer symbol, double amount, String action){
        this.buyer = buyer;
        this.symbol = symbol;
        this.amount = amount;
    }

}