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

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;


@Getter
@Setter
@ToString
public class Quote {
    private String symbol;
    private Double last_price;
    private int bid_volume;
    private Double bid;
    private int ask_volume;
    private Double ask;
    
    public Quote(String symbol, Double last_price, int bid_volume, Double bid, int ask_volume, Double ask){
        this.symbol = symbol;
        this.last_price = last_price;
        this.bid_volume = bid_volume;
        this.bid = bid;
        this.ask_volume = ask_volume;
        this.ask = ask;
    }
}