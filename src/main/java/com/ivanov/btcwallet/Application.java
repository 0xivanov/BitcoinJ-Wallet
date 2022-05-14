package com.ivanov.btcwallet;

import javafx.stage.Stage;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.script.Script;

public class Application extends javafx.application.Application {


    private final AppDelegate delegate;

    public Application() {
        delegate = WalletApplication.setInstance("Wallet", RegTestParams.get(), Script.ScriptType.P2PKH);
    }

    public static void main(String[] args) { launch(args); }

    @Override
    public void init() throws Exception {
        delegate.init(this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        delegate.start(primaryStage);
    }

    @Override
    public void stop() throws Exception {
        delegate.stop();
    }
}