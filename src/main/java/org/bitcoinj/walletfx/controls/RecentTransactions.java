package org.bitcoinj.walletfx.controls;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.bitcoinj.core.Coin;
import org.bitcoinj.walletfx.application.WalletApplication;
import org.bitcoinj.walletfx.domain.Transaction;
import org.bitcoinj.walletfx.overlay.OverlayController;
import org.bitcoinj.walletfx.overlay.OverlayableStackPaneController;

import java.io.IOException;
import java.util.List;

public class RecentTransactions extends AnchorPane implements OverlayController<ClickableBitcoinAddress> {

    private SimpleObjectProperty<List<Transaction>> recentTransactions = new SimpleObjectProperty<>();
    private WalletApplication app;
    @FXML
    protected HBox transaction1;
    @FXML
    protected HBox transaction2;
    @FXML
    protected HBox transaction3;
    @FXML
    protected HBox transaction4;


    public RecentTransactions() {
        try {
            app = WalletApplication.instance();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("recent_transactions.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.setClassLoader(getClass().getClassLoader());
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initOverlay(OverlayableStackPaneController rootController, OverlayableStackPaneController.OverlayUI<? extends OverlayController<ClickableBitcoinAddress>> ui) {
        recentTransactions.addListener((observableValue, transactions, t1) -> updateTransactions());
        updateTransactions();
    }

    public void updateTransactions() {
        org.bitcoinj.walletfx.domain.Transaction tr1 = recentTransactions.get().size() >= 1 ? recentTransactions.get().get(0) : null;
        Transaction tr2 = recentTransactions.get().size() >= 2 ? recentTransactions.get().get(1) : null;
        Transaction tr3 = recentTransactions.get().size() >= 3 ? recentTransactions.get().get(2) : null;
        Transaction tr4 = recentTransactions.get().size() >= 4 ? recentTransactions.get().get(3) : null;
        setTransactionUI(tr1, transaction1);
        setTransactionUI(tr2, transaction2);
        setTransactionUI(tr3, transaction3);
        setTransactionUI(tr4, transaction4);
    }

    private void setTransactionUI(Transaction tr, HBox hBox) {
        hBox.setVisible(true);
        if (tr == null) {
            hBox.setVisible(false);
            return;
        }
        ImageView image = (ImageView) hBox.getChildren().get(0);

        VBox vBox = (VBox) hBox.getChildren().get(1);
        Label date = (Label) vBox.getChildren().get(0);
        date.setText(tr.getTimestamp().toLocaleString());

        Label address = (Label) vBox.getChildren().get(1);
        address.setText(tr.getAddress().getAddress());

        Coin value = tr.getAmount();
        Label amount = (Label) hBox.getChildren().get(2);
        amount.setText(value.toBtc().toString());

        if (value.isGreaterThan(Coin.ZERO)) {
            image.setImage(new Image("/org/bitcoinj/walletfx/images/incoming.png"));
            amount.setStyle("-fx-text-fill: #bfbfbf;-fx-font-size: 13pt;");
            amount.setText("+" + amount.getText());
        } else {
            image.setImage(new Image("/org/bitcoinj/walletfx/images/outgoing.png"));
            amount.setStyle("-fx-text-fill: red;-fx-font-size: 13pt;");
        }
    }

    public SimpleObjectProperty<List<Transaction>> recentTransactionsProperty() {
        return recentTransactions;
    }
}
