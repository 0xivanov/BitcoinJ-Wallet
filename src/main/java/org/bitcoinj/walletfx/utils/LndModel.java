package org.bitcoinj.walletfx.utils;

import io.grpc.stub.StreamObserver;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.lightningj.lnd.proto.LightningApi;
import org.lightningj.lnd.wrapper.AsynchronousLndAPI;
import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.*;

public class LndModel {


    //wallet balance
    private SimpleObjectProperty<WalletBalanceResponse> lndBalance;
    //channel list
    private SimpleObjectProperty<ListChannelsResponse> lndChannels;
    //invoices
    private SimpleObjectProperty<ListInvoiceResponse> lndInvoices;


    public LndModel(AsynchronousLndAPI lndApi) {
        try {
            setupLnd(lndApi);
        } catch (StatusException e) {
            throw new RuntimeException(e);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupLnd(AsynchronousLndAPI lndAPI) throws StatusException, ValidationException {
        lndAPI.subscribeTransactions(new GetTransactionsRequest(), new StreamObserver<>() {
            @Override
            public void onNext(Transaction transaction) {
                System.out.println("sdfsddf");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("sdfsddf");
            }

            @Override
            public void onCompleted() {
                System.out.println("sdfsddf");
            }
        });

        lndAPI.walletBalance(new StreamObserver<>() {
            @Override
            public void onNext(WalletBalanceResponse value) {
                lndBalance.set(value);
                System.out.println("Received WalletBalance response: " + value.toJsonAsString(true));
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error occurred during WalletBalance call: " + t.getMessage());
                t.printStackTrace(System.err);
            }

            @Override
            public void onCompleted() {
                System.out.println("WalletBalance call closed.");
            }
        });
    }

    public ReadOnlyObjectProperty<WalletBalanceResponse> balanceProperty() {
        return this.lndBalance;
    }
}
