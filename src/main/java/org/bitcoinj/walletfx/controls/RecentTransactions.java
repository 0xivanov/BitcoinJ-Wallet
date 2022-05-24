package org.bitcoinj.walletfx.controls;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.bitcoinj.core.*;
import org.bitcoinj.walletfx.application.WalletApplication;
import org.bitcoinj.walletfx.overlay.OverlayController;
import org.bitcoinj.walletfx.overlay.OverlayableStackPaneController;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class RecentTransactions extends AnchorPane implements OverlayController<ClickableBitcoinAddress> {

    //private OverlayableStackPaneController rootController;
    private SimpleObjectProperty<List<Transaction>> recentTransactions = new SimpleObjectProperty<>();
    private WalletApplication app;

    @FXML
    protected VBox transactions;

    public RecentTransactions() {
        try {
            app = WalletApplication.instance();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("recent_transactions.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            // The following line is supposed to help Scene Builder, although it doesn't seem to be needed for me.
            loader.setClassLoader(getClass().getClassLoader());
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initOverlay(OverlayableStackPaneController rootController, OverlayableStackPaneController.OverlayUI<? extends OverlayController<ClickableBitcoinAddress>> ui) {
        recentTransactions.get().forEach(transaction -> {
            Coin value = transaction.getValue(app.walletAppKit().wallet());
            String date = transaction.getUpdateTime().toLocaleString();
            transactions.getChildren().add(createTransactionRow(value, date));
        });
    }

    private HBox createTransactionRow(Coin value, String date) {
        HBox hBox = new HBox();
        hBox.getChildren().add(new Label(value.toBtc().toString()));
        hBox.getChildren().add(new Label(date));
        hBox.setSpacing(10.0);

        return hBox;
    }

    private TransactionOutput getOutput(Transaction transaction) {
        for (TransactionOutput output : transaction.getOutputs()) {
            if(output.isMine(app.walletAppKit().wallet())) return output;
        }
        return null;
    }

    private TransactionInput getInput(Transaction transaction) {
        for (TransactionInput input : transaction.getInputs()) {
            if(input.getConnectedOutput().isMine(app.walletAppKit().wallet())) return input;
        }
        return null;
    }

    public SimpleObjectProperty<List<Transaction>> recentTransactionsProperty() {
        return recentTransactions;
    }
}
