/*
 * Copyright by the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitcoinj.walletfx.utils;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.wallet.Wallet;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import java.util.Date;
import java.util.List;

/**
 * A class that exposes relevant bitcoin stuff as JavaFX bindable properties.
 */
public class BitcoinUIModel {
    private SimpleObjectProperty<Address> address = new SimpleObjectProperty<>();
    private SimpleObjectProperty<Coin> balance = new SimpleObjectProperty<>(Coin.ZERO);
    private SimpleObjectProperty<Coin> pending = new SimpleObjectProperty<>(Coin.ZERO);
    private SimpleObjectProperty<List<Transaction>> recentTransactions = new SimpleObjectProperty<>();
    private SimpleDoubleProperty syncProgress = new SimpleDoubleProperty(-1);

    private ProgressBarUpdater syncProgressUpdater = new ProgressBarUpdater();


    public BitcoinUIModel(Wallet wallet) {
        setupWallet(wallet);
    }

    public final void setupWallet(Wallet wallet) {
        //wallet.addCoinsReceivedEventListener(Platform::runLater, (w, transaction, coin, coin1) -> updateBalance(w));
        wallet.addChangeEventListener(w -> {
            updateBalance(w);
            updateRecentTransactions(w);
        });
        wallet.addCurrentKeyChangeEventListener(Platform::runLater, () -> updateAddress(wallet));
        updateBalance(wallet);
        updateAddress(wallet);
        updateRecentTransactions(wallet);
    }

    private void updateRecentTransactions(Wallet wallet) {
        recentTransactions.set(wallet.getRecentTransactions(4, false));

    }

    private void updateBalance(Wallet wallet) {
        balance.set(wallet.getBalance());
        pending.set(wallet.getBalance(Wallet.BalanceType.ESTIMATED).subtract(wallet.getBalance()));
    }

    private void updateAddress(Wallet wallet) {
        address.set(wallet.currentReceiveAddress());
    }

    private class ProgressBarUpdater extends DownloadProgressTracker {
        @Override
        protected void progress(double pct, int blocksLeft, Date date) {
            super.progress(pct, blocksLeft, date);
            Platform.runLater(() -> syncProgress.set(pct / 100.0));
        }

        @Override
        protected void doneDownload() {
            super.doneDownload();
            Platform.runLater(() -> syncProgress.set(1.0));
        }
    }

    public DownloadProgressTracker getDownloadProgressTracker() { return syncProgressUpdater; }

    public ReadOnlyDoubleProperty syncProgressProperty() { return syncProgress; }

    public ReadOnlyObjectProperty<Address> addressProperty() {
        return address;
    }

    public ReadOnlyObjectProperty<Coin> balanceProperty() {
        return balance;
    }
    public SimpleObjectProperty<Coin> pendingProperty() {
        return pending;
    }
    public SimpleObjectProperty<List<Transaction>> recentTransactionsProperty() {
        return recentTransactions;
    }
}
