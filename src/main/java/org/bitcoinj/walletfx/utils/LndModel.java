package org.bitcoinj.walletfx.utils;

import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.lightningj.lnd.wrapper.*;
import org.lightningj.lnd.wrapper.message.*;

import java.util.List;

public class LndModel {

    private Amount amount = new Amount();

    //channel balance
    private static SimpleObjectProperty<Long> lndBalance = new SimpleObjectProperty<>(0L);
    //invoices
    private SimpleListProperty<Invoice> lndInvoices = new SimpleListProperty<>();


    public LndModel() {
        amount.setSat(12);
    }

    public void setupLnd(AsynchronousLndAPI lndAPI) throws StatusException, ValidationException {
        lndAPI.subscribeInvoices(new InvoiceSubscription(), new StreamObserver<>() {
            @Override
            public void onNext(Invoice invoice) {
                Platform.runLater(() -> {
                    if(invoice.getSettled()) {
                        Long value = invoice.getValue();
                        lndInvoices.remove(lndInvoices.get(lndInvoices.size()-1));
                        updateBalance(value);
                    } else {
                        lndInvoices.add(invoice);
                    }
                });
            }
            @Override
            public void onError(Throwable t) {
                System.err.println("Error occurred " + t.getMessage());
                t.printStackTrace(System.err);
            }
            @Override
            public void onCompleted() {}
        });

        lndAPI.channelBalance(new StreamObserver<>() {
            @Override
            public void onNext(ChannelBalanceResponse value) {
                Platform.runLater(() -> {
                    try {
                        lndBalance.set(value.getLocalBalance().getSat());
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                });
            }
            @Override
            public void onError(Throwable t) {
                System.err.println("Error occurred " + t.getMessage());
                t.printStackTrace(System.err);
            }
            @Override
            public void onCompleted() {}
        });
    }

    public static void updateBalance(Long value) {
        Platform.runLater(() -> {
            try {
                lndBalance.set(lndBalance.getValue() + value);
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }
    public ReadOnlyObjectProperty<Long> balanceProperty() {
        return this.lndBalance;
    }

    public SimpleListProperty<Invoice> invoicesProperty() {
        return this.lndInvoices;
    }
}
