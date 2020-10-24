package cs203.g1t7.transaction;

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
public class Transaction {
    private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Integer id;

    @NotNull (message = "Sender account must not be null.")
    @Column (name = "sender_account_id")
    private Integer from;

    @NotNull (message = "Receiver account must not be null.")
    @Column (name = "receiver_account_id")
    private Integer to;
    
    @NotNull (message = "Must specify transaction amount.")
    private double amount;
    
    @ManyToOne 
    @JoinColumn (name = "account_id", nullable = false)
    private Account account;

    public Transaction(Integer from, Integer to, double amount){
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

}