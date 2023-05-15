package org.bitcoinj.walletfx.controls;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.bitcoinj.core.Coin;
import org.bitcoinj.walletfx.application.WalletApplication;
import org.bitcoinj.walletfx.common.MainController;
import org.bitcoinj.walletfx.domain.Transaction;
import org.bitcoinj.walletfx.overlay.OverlayController;
import org.bitcoinj.walletfx.overlay.OverlayableStackPaneController;

import java.io.IOException;
import java.util.List;

public class RecentTransactions extends AnchorPane implements OverlayController<ClickableBitcoinAddress> {

    public SimpleObjectProperty<List<Transaction>> recentTransactions = new SimpleObjectProperty<>();
    private WalletApplication app;
    @FXML
    protected HBox transaction1;
    @FXML
    protected HBox transaction2;
    @FXML
    protected HBox transaction3;
    @FXML
    protected HBox transaction4;
    MainController mainController;

    public RecentTransactions() {
        try {
            app = WalletApplication.instance();
            mainController = (MainController) app.mainWindowController();
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
        mainController = (MainController) app.mainWindowController();
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

        HBox hbox1 = (HBox) vBox.getChildren().get(1);
        Label address = (Label) hbox1.getChildren().get(0);
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
    public void delete1() {
        String deletedHash = recentTransactions.get().get(0).getTransactionHash();
        mainController.transactionRepository.delete(mainController.transactionRepository.findTransactionByTransactionHash(deletedHash));
        mainController.model.updateRecentTransactions(app.walletAppKit().wallet(), deletedHash);
    }
    public void delete2() {
        String deletedHash = recentTransactions.get().get(1).getTransactionHash();
        mainController.transactionRepository.delete(mainController.transactionRepository.findTransactionByTransactionHash(deletedHash));
        mainController.model.updateRecentTransactions(app.walletAppKit().wallet(), deletedHash);
    }
    public void delete3() {
        String deletedHash = recentTransactions.get().get(2).getTransactionHash();
        mainController.transactionRepository.delete(mainController.transactionRepository.findTransactionByTransactionHash(deletedHash));
        mainController.model.updateRecentTransactions(app.walletAppKit().wallet(), deletedHash);
    }
    public void delete4() {
        String deletedHash = recentTransactions.get().get(3).getTransactionHash();
        mainController.transactionRepository.delete(mainController.transactionRepository.findTransactionByTransactionHash(deletedHash));
        mainController.model.updateRecentTransactions(app.walletAppKit().wallet(), deletedHash);
    }
    public void copy1() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(recentTransactions.get().get(0).getAddress().getAddress());
        clipboard.setContent(content);
    }
    public void copy2() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(recentTransactions.get().get(1).getAddress().getAddress());
        clipboard.setContent(content);
    }
    public void copy3() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(recentTransactions.get().get(2).getAddress().getAddress());
        clipboard.setContent(content);
    }
    public void copy4() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(recentTransactions.get().get(3).getAddress().getAddress());
        clipboard.setContent(content);
    }
    public SimpleObjectProperty<List<Transaction>> recentTransactionsProperty() {
        return recentTransactions;
    }
}
