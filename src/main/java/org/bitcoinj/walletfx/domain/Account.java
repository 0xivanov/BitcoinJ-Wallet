package org.bitcoinj.walletfx.domain;

import lombok.Data;
import org.bitcoinj.core.Coin;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public Coin balance;
    @OneToMany
    public Set<Transaction> transactions;
    public Account() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Coin getBalance() {
        return balance;
    }

    public void setBalance(Coin balance) {
        this.balance = balance;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }
}
