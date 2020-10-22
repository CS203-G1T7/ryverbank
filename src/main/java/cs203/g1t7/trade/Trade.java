package cs203.g1t7.trade;

// import java.util.List;

// import javax.persistence.CascadeType;
// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.GenerationType;
// import javax.persistence.Id;
// import javax.persistence.ManyToOne;
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
public class Trade {
    private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Integer id;

    @NotNull (message = "Buyer account must not be null.")
    @Column (name = "buyer_account_id")
    private Integer buyer;

    @NotNull (message = "Stock symbol must not be null.")
    @Column (name = "stock_symbol")
    private Integer symbol;
    
    @NotNull (message = "Must specify transaction amount.")
    private Integer quantity;

    @NotNull (message = "Action must be specified")
    private String action;

    @NotNull (message = "Must specify bid amount.")
    private double bid;

    @NotNull (message = "Must specify ask amount.")
    private double ask;

    @NotNull (message = "Must specify average price amount.")
    private double avg_price;

    @NotNull (message = "Must specify filled quantity amount.")
    private Integer filled_quantity;

    @NotNull (message = "Must specify date.")
    private long date;

    @NotNull (message = "Must specify status.")
    private String status;
    
    @ManyToOne 
    @JoinColumn (name = "customer_id", nullable = false)
    private Account account;

    public Trade(Integer buyer, Integer symbol, Integer quantity, String action, double bid, double ask
                    double avg_price, Integer filled_quantity, Date date, String status){
        this.buyer = buyer;
        this.symbol = symbol;
        this.quantity = quantity;
        this.action = action;
        this.bid = bid;
        this.ask = ask;
        this.avg_price = avg_price;
        this.filled_quantity = filled_quantity;
        this.date = date;
        this.status = status;
    }

}