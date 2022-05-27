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
import org.bitcoinj.core.Transaction;
import org.bitcoinj.walletfx.application.WalletApplication;
import org.bitcoinj.walletfx.overlay.OverlayController;
import org.bitcoinj.walletfx.overlay.OverlayableStackPaneController;

import java.io.IOException;
import java.util.List;

public class RecentTransactions extends AnchorPane implements OverlayController<ClickableBitcoinAddress> {

    private SimpleObjectProperty<List<Transaction>> recentTransactions = new SimpleObjectProperty<>();
    private WalletApplication app;

    //private Image incoming = new Image("/org/bitcoinj/walletfx/images/incoming.png");


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
        recentTransactions.addListener((observableValue, transactions, t1) -> {
            updateTransactions();
        });
        updateTransactions();
    }

    public void updateTransactions() {
        recentTransactions.get().forEach(transaction -> {
            Coin value = transaction.getValue(app.walletAppKit().wallet());
            String date = transaction.getUpdateTime().toLocaleString();
            //transactions.getChildren().add(createTransactionRow(value, date));
        });

        Transaction tr1 = recentTransactions.get().get(0);
        Transaction tr2 = recentTransactions.get().get(1);
        Transaction tr3 = recentTransactions.get().get(2);
        Transaction tr4 = recentTransactions.get().get(3);
        setTransactionUI(tr1, transaction1);
        setTransactionUI(tr2, transaction2);
        setTransactionUI(tr3, transaction3);
        setTransactionUI(tr4, transaction4);
    }

    private void setTransactionUI(Transaction tr, HBox hBox) {
        if(tr == null) {
            hBox.setVisible(false);
            return;
        }
        Coin value = tr.getValue(app.walletAppKit().wallet());
        ImageView image = (ImageView) hBox.getChildren().get(0);

        VBox vBox = (VBox) hBox.getChildren().get(1);
        Label date = (Label) vBox.getChildren().get(0);
        date.setText(tr.getUpdateTime().toLocaleString());

        Label amount = (Label) hBox.getChildren().get(2);
        amount.setText(value.toBtc().toString());

        if(value.isGreaterThan(Coin.ZERO)) {
            image.setImage(new Image("/org/bitcoinj/walletfx/images/incoming.png"));
            amount.setStyle("-fx-text-fill: #bfbfbf;-fx-font-size: 13pt;");
            amount.setText("+" + amount.getText());
        }
        else {
            image.setImage(new Image("/org/bitcoinj/walletfx/images/outgoing.png"));
            amount.setStyle("-fx-text-fill: red;-fx-font-size: 13pt;");
        }
    }

    private HBox createTransactionRow(Coin value, String date) {
        Label valueLabel = new Label(value.toBtc().toString());
        if (value.isLessThan(Coin.ZERO)) {
            valueLabel.setStyle("-fx-text-fill: red;-fx-font-size: 13pt;");
        } else {
            valueLabel.setStyle("-fx-text-fill: #bfbfbf;-fx-font-size: 13pt;");
        }
        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-font-size: 11pt;");
        HBox hBox = new HBox();
        //hBox.getChildren().add(incoming);
        hBox.getChildren().add(dateLabel);
        hBox.getChildren().add(valueLabel);
        hBox.setSpacing(10.0);

        return hBox;
    }

    public SimpleObjectProperty<List<Transaction>> recentTransactionsProperty() {
        return recentTransactions;
    }
}
