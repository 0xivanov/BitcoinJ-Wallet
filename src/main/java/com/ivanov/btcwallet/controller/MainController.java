package com.ivanov.btcwallet.controller;

import com.ivanov.btcwallet.WalletApplication;
import com.ivanov.btcwallet.model.BitcoinUIModel;
import com.ivanov.btcwallet.util.WalletUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.utils.MonetaryFormat;

public class MainController {

    @FXML
    private Label addressLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private TextField satsToSend;

    @FXML
    private TextField satsToReceive;

    @FXML
    private Button sendBtn;

    private WalletApplication app;

    private final BitcoinUIModel model = new BitcoinUIModel();

    private static final MonetaryFormat MONETARY_FORMAT = MonetaryFormat.BTC.noCode();


    @FXML
    protected void initialize() {
        app = WalletApplication.getInstance();
        this.sendBtn.setOnAction(this::send);
    }

    @FXML
    private void send(ActionEvent event) {
        int satsToSend = Integer.parseInt(this.satsToSend.getText());
        WalletUtil.send(app.getWalletAppKit(), satsToSend, "bcrt1qm0l949d7mvfmj2807wpg0ckvyeukkcktcj32fp", app.getParams());
    }

    public void onBitcoinSetup() {
        model.setWallet(app.getWalletAppKit().wallet());
        addressLabel.textProperty().bind(createAddressStringBinding(model.balanceProperty()));
        balanceLabel.textProperty().bind(createBalanceStringBinding(model.balanceProperty()));
    }

    private ObservableValue<String> createBalanceStringBinding(ReadOnlyObjectProperty<Coin> balanceProperty) {
        return Bindings.createStringBinding(() -> formatCoin(balanceProperty.getValue()), balanceProperty);
    }

    private static String formatCoin(Coin coin) {
        return MONETARY_FORMAT.format(coin).toString();
    }

    private ObservableValue<String> createAddressStringBinding(ReadOnlyObjectProperty<Coin> addressProperty) {
        return Bindings.createStringBinding(() -> addressProperty.getValue().toFriendlyString(), addressProperty);
    }

    public DownloadProgressTracker progressBarUpdater() {
        return model.getDownloadProgressTracker();
    }
}