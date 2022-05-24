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

package org.bitcoinj.walletfx.controls;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.walletfx.overlay.OverlayController;
import org.bitcoinj.walletfx.overlay.OverlayableStackPaneController;

import javax.annotation.Nullable;
import java.io.IOException;

import static javafx.beans.binding.Bindings.convert;

/**
 * A custom control that implements a clickable, copyable Bitcoin address. Clicking it opens a local wallet app. The
 * address looks like a blue hyperlink. Next to it there are two icons, one that copies to the clipboard and another
 * that shows a QRcode.
 */
public class ClickableBitcoinAddress extends AnchorPane implements OverlayController<ClickableBitcoinAddress> {
    @FXML protected Label addressLabel;
    @FXML protected Label copyWidget;

    protected SimpleObjectProperty<Address> address = new SimpleObjectProperty<>();
    private final StringExpression addressStr;

    //private OverlayableStackPaneController rootController;

    private String appName = "app-name";

    @Override
    public void initOverlay(OverlayableStackPaneController overlayableStackPaneController, OverlayableStackPaneController.OverlayUI<? extends OverlayController<ClickableBitcoinAddress>> ui) {
        //rootController = overlayableStackPaneController;
    }

    /**
     * @param theAppName The application name to use in Bitcoin URIs
     */
    public void setAppName(String theAppName) {
        appName = theAppName;
    }



    public ClickableBitcoinAddress() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bitcoin_address.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            // The following line is supposed to help Scene Builder, although it doesn't seem to be needed for me.
            loader.setClassLoader(getClass().getClassLoader());
            loader.load();
            AwesomeDude.setIcon(copyWidget, AwesomeIcon.COPY);
            Tooltip.install(copyWidget, new Tooltip("Copy address to clipboard"));

            addressStr = convert(address);
            addressLabel.textProperty().bind(addressStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String uri(@Nullable Coin amount) {
        return BitcoinURI.convertToBitcoinURI(address.get(), amount, appName, null);
    }

    public ObjectProperty<Address> addressProperty() {
        return address;
    }

    @FXML
    protected void copyAddress(ActionEvent event) {
        // User clicked icon or menu item.
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(addressStr.get());
        content.putHtml(String.format("<a href='%s'>%s</a>", uri(null), addressStr.get()));
        clipboard.setContent(content);
    }


    @FXML
    protected void copyWidgetClicked(MouseEvent event) {
        copyAddress(null);
    }

}
