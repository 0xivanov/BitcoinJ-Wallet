package org.bitcoinj.walletfx.domain;

import lombok.Data;
import org.bitcoinj.core.Coin;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String transactionHash;
    private boolean isOutbound;
    @ManyToOne
    private Account address;
    private Coin amount;
    private Date timestamp;
}