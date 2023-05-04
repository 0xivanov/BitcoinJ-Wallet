package org.bitcoinj.walletfx.domain;

import lombok.Data;
import org.bitcoinj.core.Coin;

import javax.persistence.*;

@Data
@Entity
@Table
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    @ManyToOne
    private Account sender;
    @ManyToOne
    private Account recipient;
    private Coin amount;
}