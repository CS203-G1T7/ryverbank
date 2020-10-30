package cs203.g1t7.trade;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.*;

import cs203.g1t7.account.Account;

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
    private Integer account_id;

    @NotNull (message = "Stock symbol must not be null.")
    @Column (name = "trade_symbol")
    private String symbol;
    
    @NotNull (message = "Must specify transaction amount.")
    @Column (name = "trade_quantity")
    private Integer quantity;

    @NotNull (message = "Action must be specified")
    @Column (name = "action_needed")
    private String action;

    @Column (name = "trade_bid")
    private double bid;

    @Column (name = "trade_ask")
    private double ask;

    @Column (name = "trade_avg")
    private double avg_price;

    private Integer filled_quantity = 0;

    @Column (name = "trade_date")
    private long date;

    @Column (name = "trade_status")
    private String status;
    
    @NotNull (message = "Must specify customer id.")
    private Integer customer_id;

    public Trade(Integer account_id, String symbol, Integer quantity, String action, double bid, double ask,
                    double avg_price, Integer filled_quantity, long date, String status, Integer customer_id){
        this.account_id = account_id;
        this.symbol = symbol;
        this.quantity = quantity;
        this.action = action;
        this.bid = bid;
        this.ask = ask;
        this.avg_price = avg_price;
        this.filled_quantity = filled_quantity;
        this.date = date;
        this.status = status;
        this.customer_id = customer_id;
    }

}