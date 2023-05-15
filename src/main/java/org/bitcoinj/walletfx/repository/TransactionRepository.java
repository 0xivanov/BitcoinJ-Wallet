package org.bitcoinj.walletfx.repository;

import org.bitcoinj.walletfx.domain.Transaction;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsTransactionByTransactionHash(String hash);
    Transaction findTransactionByTransactionHash(String hash);
    void deleteTransactionByTransactionHash(String hash);
}
