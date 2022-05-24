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

package wallettemplate;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.core.Coin;
import org.bitcoinj.script.Script;
import org.bitcoinj.utils.MonetaryFormat;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.bitcoinj.walletfx.application.MainWindowController;
import org.bitcoinj.walletfx.application.WalletApplication;
import org.bitcoinj.walletfx.utils.*;
import org.bitcoinj.walletfx.controls.ClickableBitcoinAddress;
import org.bitcoinj.walletfx.controls.NotificationBarPane;
import org.bitcoinj.walletfx.utils.easing.EasingMode;
import org.bitcoinj.walletfx.utils.easing.ElasticInterpolator;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkState;

/**
 * Gets created auto-magically by FXMLLoader via reflection. The widget fields are set to the GUI controls they're named
 * after. This class handles all the updates and event handling for the main UI.
 */
public class MainController extends MainWindowController {
    public Label balance;
    public Label pending;
    public Button sendMoneyOutBtn;
    public Button requestMoneyBtn;
    public TextField btcToRequest;
    public ClickableBitcoinAddress addressControl;

    private final BitcoinUIModel model = new BitcoinUIModel();
    private NotificationBarPane.Item syncItem;
    private static final MonetaryFormat MONETARY_FORMAT = MonetaryFormat.BTC.noCode();

    protected String uri;

    private WalletApplication app;
    private NotificationBarPane notificationBar;

    // Called by FXMLLoader.
    public void initialize() {
        app = WalletApplication.instance();
        scene = new Scene(uiStack);
        TextFieldValidator.configureScene(scene);
        new TextFieldValidator(btcToRequest, amount -> {
            checkButton(amount);
            return testAmountToRequest(amount);
        });
        //btcToRequest.textProperty().addListener((observableValue, prev, current) -> checkButton(current));
        // Special case of initOverlay that passes null as the 2nd parameter because ClickableBitcoinAddress is loaded by FXML
        // TODO: Extract QRCode Pane to separate reusable class that is a more standard OverlayController instance
        addressControl.initOverlay(this, null);
        addressControl.setAppName(app.applicationName());
        addressControl.setOpacity(0.0);
        requestMoneyBtn.setDisable(true);
    }

    @Override
    public void controllerStart(TabPane mainUI, String cssResourceName) {
        this.mainUI = mainUI;
        // Configure the window with a StackPane so we can overlay things on top of the main UI, and a
        // NotificationBarPane so we can slide messages and progress bars in from the bottom. Note that
        // ordering of the construction and connection matters here, otherwise we get (harmless) CSS error
        // spew to the logs.
        notificationBar = new NotificationBarPane(mainUI);
        // Add CSS that we need. cssResourceName will be loaded from the same package as this class.
        scene.getStylesheets().add(getClass().getResource(cssResourceName).toString());
        uiStack.getChildren().add(notificationBar);
        scene.getAccelerators().put(KeyCombination.valueOf("Shortcut+F"), () -> app.walletAppKit().peerGroup().getDownloadPeer().close());
    }

    @Override
    public void onBitcoinSetup() {
        model.setWallet(app.walletAppKit().wallet());
        addressControl.addressProperty().bind(model.addressProperty());
        balance.textProperty().bind(createBalanceStringBinding(model.balanceProperty()));
        pending.textProperty().bind(createBalanceStringBinding(model.pendingProperty()));
        // Don't let the user click send money when the wallet is empty.
        sendMoneyOutBtn.disableProperty().bind(model.balanceProperty().isEqualTo(Coin.ZERO));

        showBitcoinSyncMessage();
        model.syncProgressProperty().addListener(x -> {
            if (model.syncProgressProperty().get() >= 1.0) {
                readyToGoAnimation();
                if (syncItem != null) {
                    syncItem.cancel();
                    syncItem = null;
                }
            } else if (syncItem == null) {
                showBitcoinSyncMessage();
            }
        });
    }

    private static String formatCoin(Coin coin) {
        return MONETARY_FORMAT.format(coin).toString();
    }

    private static Binding<String> createBalanceStringBinding(ObservableValue<Coin> coinProperty) {
        return Bindings.createStringBinding(() -> formatCoin(coinProperty.getValue()), coinProperty);
    }

    private void showBitcoinSyncMessage() {
        syncItem = notificationBar.pushItem("Synchronising with the Bitcoin network", model.syncProgressProperty());
    }

    public void sendMoneyOut(ActionEvent event) {
        // Hide this UI and show the send money UI. This UI won't be clickable until the user dismisses send_money.
        overlayUI("send_money.fxml");
    }

    public void requestMoney(ActionEvent event) {
        app.walletAppKit().wallet().freshReceiveAddress(Script.ScriptType.P2WPKH);
        Coin amountToRequest = Coin.parseCoin(btcToRequest.getText());
        this.uri = addressControl.uri(amountToRequest);
        try {
            Desktop.getDesktop().browse(URI.create(this.uri));
        } catch (IOException e) {
            GuiUtils.informationalAlert("Opening wallet app failed", "Perhaps you don't have one installed?");
        }
        this.
        overlayUI("request_uri.fxml");
    }

    public void settingsClicked(ActionEvent event) {
        OverlayUI<WalletSettingsController> screen = overlayUI("wallet_settings.fxml");
        screen.controller.initialize(null);
    }

    @Override
    public void restoreFromSeedAnimation() {
        // Buttons slide out ...
        TranslateTransition leave = new TranslateTransition(Duration.millis(1200));
        leave.setByY(80.0);
        leave.play();
    }

    public void readyToGoAnimation() {
        // Buttons slide in and clickable address appears simultaneously.
        //TranslateTransition arrive = new TranslateTransition(Duration.millis(1200));
        //arrive.setInterpolator(new ElasticInterpolator(EasingMode.EASE_OUT, 1, 2));
        //arrive.setToY(0.0);
        FadeTransition reveal = new FadeTransition(Duration.millis(1200), addressControl);
        reveal.setToValue(1.0);
        ParallelTransition group = new ParallelTransition(reveal);
        group.setDelay(NotificationBarPane.ANIM_OUT_DURATION);
        group.setCycleCount(1);
        group.play();
    }

    @Override
    public DownloadProgressTracker progressBarUpdater() {
        return model.getDownloadProgressTracker();
    }

    private void checkButton(String current) {
        boolean value = testAmountToRequest(current);
        if(current.isEmpty()) value = false;
        requestMoneyBtn.setDisable(!value);
    }
    private boolean testAmountToRequest(String amount) {
        return amount.isEmpty() || !WTUtils.didThrow(() -> checkState(Coin.parseCoin(amount).value > 0));
    }

}
