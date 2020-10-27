package cs203.g1t7.account;

import java.util.List;

import javax.persistence.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import cs203.g1t7.transaction.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Account {
    private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Integer id;

    @NotNull (message = "Customer id should not be null.")
    private Integer customer_id;

    @NotNull (message = "Balance should not be null.")
    private double balance;

    @NotNull (message = "available balance should not be null.")
    private double available_balance;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Transaction> transactions;

    public Account(Integer customer_id, double balance, double available_balance){
        this.customer_id = customer_id;
        this.balance = balance;
        this.available_balance = available_balance;
    }
}