module com.ivanov.btcwallet {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.bitcoinj.core;
    requires com.google.common;
    requires jsr305;
    requires fontawesomefx;

    opens com.ivanov.btcwallet to javafx.fxml;
    exports com.ivanov.btcwallet;
    exports com.ivanov.btcwallet.controller;
    opens com.ivanov.btcwallet.controller to javafx.fxml;
}