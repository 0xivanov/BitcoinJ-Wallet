package com.ivanov.btcwallet.util;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.ivanov.btcwallet.controller.MainController;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import javax.annotation.Nullable;

public class WalletUtil {

    private static WalletUtil instance = null;

    private static MainController mainController;

    private WalletUtil(MainController mainController) {
        WalletUtil.setMainController(mainController);
    }

    public static MainController getMainController() {
        return mainController;
    }

    public static void setMainController(MainController mainController) {
        WalletUtil.mainController = mainController;
    }

    public static WalletUtil getInstance() {
        return instance;
    }

    public static void setInstance(MainController mainController) {
        if(instance == null) instance = new WalletUtil(mainController);
    }

    public static void send(WalletAppKit kit, int sats, String addressTo, NetworkParameters params) {
        try {
            Coin amount = Coin.ofSat(sats);
            Address destination = Address.fromString(params, addressTo);
            SendRequest req;
            if (amount.equals(kit.wallet().getBalance()))
                req = SendRequest.emptyWallet(destination);
            else
                req = SendRequest.to(destination, amount);

            req.setFeePerVkb(Coin.ofSat(100));
            // Don't make the user wait for confirmations for now, as the intention is they're sending it
            // their own money!
            req.allowUnconfirmed();
            Wallet.SendResult sendResult = kit.wallet().sendCoins(req);
            Futures.addCallback(sendResult.broadcastComplete, new FutureCallback<>() {
                @Override
                public void onSuccess(@Nullable Transaction result) {
                    System.out.println(result.getConfidence());
                }

                @Override
                public void onFailure(Throwable t) {
                    System.out.println("failure");
                }
            }, MoreExecutors.directExecutor());
            sendResult.tx.getConfidence().addEventListener((tx, reason) -> {
                if (reason == TransactionConfidence.Listener.ChangeReason.SEEN_PEERS) {
                    System.out.println(tx.getConfidenceType());
                }
                else {
                    System.out.println(tx.getConfidenceType());
                }

            });
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        } catch (ECKey.KeyIsEncryptedException e) {
            e.printStackTrace();
        }
    }
}
