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
    
}