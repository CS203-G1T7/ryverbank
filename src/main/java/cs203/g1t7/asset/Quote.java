package cs203.g1t7.asset;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Quote {

    @NotNull (message = "Buyer account must not be null.")
    @Column (name = "symbol")
    private @Id String symbol;

    @NotNull (message = "Buyer account must not be null.")
    @Column (name = "last_price")
    private Double last_price;

    @NotNull (message = "Buyer account must not be null.")
    @Column (name = "bid_volume")
    private int bid_volume;

    @NotNull (message = "Buyer account must not be null.")
    @Column (name = "bid")
    private Double bid;

    @NotNull (message = "Buyer account must not be null.")
    @Column (name = "ask_volume")
    private int ask_volume;

    @NotNull (message = "Buyer account must not be null.")
    @Column (name = "ask")
    private Double ask;

    //@JsonIgnore
    private int originalAskVol;

    //@JsonIgnore
    private int originalBidVol;
    
    // public Quote() {
    //     try {
    //         String[] symbols = new String[] { "A17U", "C61U", "C31", "C38U", "C09", "C52", "D01", "D05", "G13", "H78",
    //             "C07", "J36", "J37", "BN4", "N2IU", "ME8U", "M44U", "O39", "S58", "U96", "S68", "C6L", "Z74", "S63",
    //             "Y92", "U11", "U14", "V03", "F34", "BS6" };

    //         for (String symbol : symbols) {
    //             Stock stock = YahooFinance.get(symbol + ".SI");
    //             // double bid = stock.getQuote().getBid().doubleValue();
    //             double bid = 0.0;
    //             double price = stock.getQuote().getPrice().doubleValue();
    //             // double ask = stock.getQuote().getAsk().doubleValue();
    //             double ask = 0.0;
    //             int bidVolume = 0;
    //             // stock.getQuote().getBidSize().intValue();
    //             int askVolume = 0;
    //             // stock.getQuote().getAskSize().intValue();
    //             stockRepository.save(new CustomStock(symbol, price, bidVolume, bid, askVolume, ask));
    //         }
    //     } catch (IOException e) {
    //         System.out.println("One of the stock is not found");
    //     }
    // }
}