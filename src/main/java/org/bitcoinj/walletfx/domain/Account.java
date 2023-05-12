package org.bitcoinj.walletfx.domain;

import lombok.Data;
import lombok.Value;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String address;
    public Coin balance;
    @OneToMany
    public Set<Transaction> transactions;
}
