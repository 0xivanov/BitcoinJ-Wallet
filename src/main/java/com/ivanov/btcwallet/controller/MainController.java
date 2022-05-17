package com.ivanov.btcwallet.controller;

import com.ivanov.btcwallet.WalletApplication;
import com.ivanov.btcwallet.model.BitcoinUIModel;
import com.ivanov.btcwallet.util.WalletUtil;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.utils.MonetaryFormat;
import org.controlsfx.glyphfont.FontAwesome;

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

    @FXML
    private Button copyAddress;

    @FXML
    private FontAwesomeIcon copyIcon;

    private WalletApplication app;

    private final BitcoinUIModel model = new BitcoinUIModel();

    private static final MonetaryFormat MONETARY_FORMAT = MonetaryFormat.BTC.noCode();


    @FXML
    protected void initialize() {
        app = WalletApplication.getInstance();
        this.sendBtn.setOnAction(this::send);
        this.copyIcon.setIcon(FontAwesomeIcons.COPY);
    }

    @FXML
    private void send(ActionEvent event) {
        int satsToSend = Integer.parseInt(this.satsToSend.getText());
        WalletUtil.send(app.getWalletAppKit(), satsToSend, "bcrt1qm0l949d7mvfmj2807wpg0ckvyeukkcktcj32fp", app.getParams());
    }

    public void onBitcoinSetup() {
        model.setWallet(app.getWalletAppKit().wallet());
        addressLabel.textProperty().bind(createAddressStringBinding(model.addressProperty()));
        balanceLabel.textProperty().bind(createBalanceStringBinding(model.balanceProperty()));
    }

    private ObservableValue<String> createBalanceStringBinding(ReadOnlyObjectProperty<Coin> balanceProperty) {
        return Bindings.createStringBinding(() -> formatCoin(balanceProperty.getValue()), balanceProperty);
    }

    private static String formatCoin(Coin coin) {
        return MONETARY_FORMAT.format(coin).toString();
    }

    private ObservableValue<String> createAddressStringBinding(ReadOnlyObjectProperty<Address> addressProperty) {
        return Bindings.createStringBinding(() -> addressProperty.getValue().toString(), addressProperty);
    }

    public DownloadProgressTracker progressBarUpdater() {
        return model.getDownloadProgressTracker();
    }
}