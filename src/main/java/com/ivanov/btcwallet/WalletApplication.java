package com.ivanov.btcwallet;

import com.ivanov.btcwallet.controller.MainController;
import com.ivanov.btcwallet.util.WalletUtil;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.utils.AppDataDirectory;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.utils.Threading;
import org.bitcoinj.wallet.DeterministicSeed;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

public class WalletApplication implements AppDelegate {

    private static WalletApplication instance = null;

    private MainController mainController;
    private WalletAppKit walletAppKit;
    private final String applicationName;
    private final NetworkParameters params;
    private final Script.ScriptType preferredOutputScriptType;
    private final String walletFileName;

    private WalletApplication(String applicationName, NetworkParameters params, Script.ScriptType preferredOutputScriptType) {
        this.applicationName = applicationName;
        this.walletFileName = applicationName.replaceAll("[^a-zA-Z0-9.-]", "_") + "-" + params.getPaymentProtocolId();
        this.params = params;
        this.preferredOutputScriptType = preferredOutputScriptType;
    }

    public static WalletApplication setInstance(String applicationName, NetworkParameters params, Script.ScriptType preferredOutputScriptType) {
        if (instance == null) {
            instance = new WalletApplication(applicationName, params, preferredOutputScriptType);
        }

        return instance;
    }

    public static WalletApplication getInstance() {
        return instance;
    }

    public WalletAppKit getWalletAppKit() {
        return walletAppKit;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public NetworkParameters getParams() {
        return params;
    }

    public Script.ScriptType getPreferredOutputScriptType() {
        return preferredOutputScriptType;
    }

    public String getWalletFileName() {
        return walletFileName;
    }

    private void setupWalletKit(@Nullable DeterministicSeed seed) {


        Threading.USER_THREAD = Platform::runLater;
        File appDataDirectory = AppDataDirectory.get(applicationName).toFile();
        walletAppKit = new WalletAppKit(params, preferredOutputScriptType, null, appDataDirectory, walletFileName) {
            @Override
            protected void onSetupCompleted() {
                Platform.runLater(mainController::onBitcoinSetup);
                System.out.println(walletAppKit.wallet());
            }
        };
        if (params == RegTestParams.get()) {
            walletAppKit.connectToLocalHost();   // You should run a regtest mode bitcoind locally.
        }
        walletAppKit.setDownloadListener(mainController.progressBarUpdater())
                .setBlockingStartup(false)
                .setUserAgent(applicationName, "1.0");
        walletAppKit.startAsync();
    }



    @Override
    public void start(Stage primaryStage) throws Exception {
        startImpl(primaryStage);
    }

    private void startImpl(Stage primaryStage) {
        BriefLogFormatter.init();
        mainController = new MainController();
        WalletUtil.setInstance(mainController);
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main.fxml"));
        fxmlLoader.setController(mainController);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 800, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupWalletKit(null);
        if(params == RegTestParams.get()) primaryStage.setTitle("RegTest net");
        else if(params == TestNet3Params.get()) primaryStage.setTitle("TestNet 3");
        else primaryStage.setTitle("Main net");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {

    }
}
