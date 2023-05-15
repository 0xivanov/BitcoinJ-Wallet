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

package org.bitcoinj.walletfx.common;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.util.Duration;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.script.Script;
import org.bitcoinj.utils.MonetaryFormat;
import org.bitcoinj.walletfx.application.MainWindowController;
import org.bitcoinj.walletfx.application.WalletApplication;
import org.bitcoinj.walletfx.controls.ClickableBitcoinAddress;
import org.bitcoinj.walletfx.controls.NotificationBarPane;
import org.bitcoinj.walletfx.controls.RecentTransactions;
import org.bitcoinj.walletfx.domain.Transaction;
import org.bitcoinj.walletfx.repository.AccountRepository;
import org.bitcoinj.walletfx.repository.TransactionRepository;
import org.bitcoinj.walletfx.utils.BitcoinUIModel;
import org.bitcoinj.walletfx.utils.GuiUtils;
import org.bitcoinj.walletfx.utils.TextFieldValidator;
import org.bitcoinj.walletfx.utils.WTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

import static com.google.common.base.Preconditions.checkState;

/**
 * Gets created auto-magically by FXMLLoader via reflection. The widget fields are set to the GUI controls they're named
 * after. This class handles all the updates and event handling for the main UI.
 */
@Component
public class MainController extends MainWindowController {


    // bitcoin
    public Label balance;
    public Label pending;
    public Button sendMoneyOutBtn;
    public Button requestMoneyBtn;
    public TextField btcToRequest;
    public ClickableBitcoinAddress addressControl;
    public RecentTransactions recentTransactions;
    @Autowired
    public TransactionRepository transactionRepository;
    @Autowired
    public AccountRepository accountRepository;
    private NotificationBarPane.Item syncItem;
    private static final MonetaryFormat MONETARY_FORMAT = MonetaryFormat.BTC.noCode();
    protected String uri;
    private NotificationBarPane notificationBar;

    public BitcoinUIModel model ;
    private WalletApplication app;

    public void initialize() {
        this.model = new BitcoinUIModel(transactionRepository, accountRepository);
        app = WalletApplication.instance();
        scene = new Scene(uiStack);
        TextFieldValidator.configureScene(scene);
        new TextFieldValidator(btcToRequest, amount -> {
            checkButton(amount);
            return testAmountToRequest(amount);
        });
        addressControl.initOverlay(this, null);
        addressControl.setAppName(app.applicationName());
        addressControl.setOpacity(0.0);
        recentTransactions.setOpacity(0.0);
        requestMoneyBtn.setDisable(true);
    }

    @Override
    public void controllerStart(TabPane mainUI, String cssResourceName) {
        this.mainUI = mainUI;
        notificationBar = new NotificationBarPane(mainUI);
        // Add CSS that we need. cssResourceName will be loaded from the same package as this class.
        scene.getStylesheets().add(getClass().getResource(cssResourceName).toString());
        uiStack.getChildren().add(notificationBar);
        scene.getAccelerators().put(KeyCombination.valueOf("Shortcut+F"), () -> app.walletAppKit().peerGroup().getDownloadPeer().close());
    }

    @Override
    public void onBitcoinSetup() {
        model.setupWallet(this.app.walletAppKit().wallet());
        try {
        } catch (Exception e) {
            System.out.println(e);
        }

        addressControl.addressProperty().bind(model.addressProperty());
        balance.textProperty().bind(createBalanceStringBinding(model.balanceProperty()));
        pending.textProperty().bind(createBalanceStringBinding(model.pendingProperty()));

        sendMoneyOutBtn.disableProperty().bind(model.balanceProperty().isEqualTo(Coin.ZERO));
        recentTransactions.recentTransactionsProperty().bind(model.recentTransactionsProperty());

        showBitcoinSyncMessage();
        model.syncProgressProperty().addListener(x -> {
            if (model.syncProgressProperty().get() >= 1.0) {
                recentTransactions.initOverlay(this, null);
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

    public void sendMoneyOut(ActionEvent event){
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
        } catch (UnsupportedOperationException e) {
            GuiUtils.informationalAlert("Does not work on linux", "Perhaps you don't have one installed?");
        }
        this.overlayUI("request_uri.fxml");
    }

    public void settingsClicked(ActionEvent event) {
        OverlayUI<WalletSettingsController> screen = overlayUI("wallet_settings.fxml");
        screen.controller.initialize(null);
    }

    private static Binding<String> createBalanceStringBinding(ObservableValue<Coin> coinProperty) {
        return Bindings.createStringBinding(() -> formatCoin(coinProperty.getValue()), coinProperty);
    }

    private void checkButton(String current) {
        boolean value = testAmountToRequest(current);
        if(current.isEmpty()) value = false;
        requestMoneyBtn.setDisable(!value);
    }
    private boolean testAmountToRequest(String amount) {
        return amount.isEmpty() || !WTUtils.didThrow(() -> checkState(Long.valueOf(amount) > 0));
    }

    @Override
    public void restoreFromSeedAnimation() {
        TranslateTransition leave = new TranslateTransition(Duration.millis(1200));
        leave.setByY(80.0);
        leave.play();
    }

    public void readyToGoAnimation() {
        FadeTransition reveal = new FadeTransition(Duration.millis(1200), addressControl);
        reveal.setToValue(1.0);

        FadeTransition reveal2 = new FadeTransition(Duration.millis(1200), recentTransactions);
        reveal2.setToValue(1.0);

        ParallelTransition group = new ParallelTransition(reveal, reveal2);
        group.setDelay(NotificationBarPane.ANIM_OUT_DURATION);
        group.setCycleCount(1);
        group.play();
    }

    @Override
    public DownloadProgressTracker progressBarUpdater() {
        return model.getDownloadProgressTracker();
    }

    private void showBitcoinSyncMessage() {
        syncItem = notificationBar.pushItem("Synchronising with the Bitcoin network", model.syncProgressProperty());
    }

    private static String formatCoin(Coin coin) {
        return MONETARY_FORMAT.format(coin).toString();
    }

    private static String formatAmount(Long coin) {
        return String.valueOf(coin);
    }
}
