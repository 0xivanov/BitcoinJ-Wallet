package wallettemplate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.bitcoinj.walletfx.overlay.OverlayController;
import org.bitcoinj.walletfx.overlay.OverlayableStackPaneController;
import org.bitcoinj.walletfx.utils.QRCodeImages;

public class RequestUriController implements OverlayController<RequestUriController> {

    public Label uriLabel;

    public ImageView imageView;

    private MainController rootController;

    private OverlayableStackPaneController.OverlayUI<? extends OverlayController<RequestUriController>> overlayUI;


    @Override
    public void initOverlay(OverlayableStackPaneController overlayableStackPaneController, OverlayableStackPaneController.OverlayUI<? extends OverlayController<RequestUriController>> ui) {
        rootController = (MainController) overlayableStackPaneController;
        overlayUI = ui;

        Image qrImage = QRCodeImages.imageFromString(rootController.uri, 320, 240);
        this.imageView.setImage(qrImage);
        this.uriLabel.setText(rootController.uri);
    }

    public void initialize() {
    }

    @FXML
    protected void copy(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(uriLabel.getText());
        clipboard.setContent(content);
    }

    public void cancel(ActionEvent event) {
        overlayUI.done();
    }
}
