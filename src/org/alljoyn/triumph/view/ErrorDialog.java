package org.alljoyn.triumph.view;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.alljoyn.triumph.util.loaders.ImageLoader;
import org.alljoyn.triumph.util.loaders.ViewLoader;

/**
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class ErrorDialog extends GridPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private HBox actionParent;

    @FXML
    private Label detailsLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private Label messageLabel;

    @FXML
    private Button okButton;

    @FXML
    private HBox okParent;

    private final Stage mStage;

    /**
     * Creates an error dialog with a title and message.
     * 
     * @param title Title of the error dialog.
     * @param message Message to support the title
     */
    public ErrorDialog(String title, String message) {
        ViewLoader.loadView(this);

        setText(title, message);

        mStage = new Stage();
        mStage.initModality(Modality.WINDOW_MODAL);
        mStage.setScene(new Scene(this));

        try {
            imageView.setImage(ImageLoader.loadImage("img-alljoyn-logo.png"));
        } catch (FileNotFoundException e) {
            // Do nothing.
        }
    }

    public void setText(String title, String message) {
        messageLabel.setText(title);
        detailsLabel.setText(message);
    }

    public void show() {
        mStage.show();
    }

    public void hide() {
        mStage.hide();
    }

    @FXML
    void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE || 
                event.getCode() == KeyCode.ENTER) {
            hide();
        }
    }
    
    @FXML
    void onOK(ActionEvent event) {
        hide();
    }

    @FXML
    void initialize() {
        assert actionParent != null : "fx:id=\"actionParent\" was not injected: check your FXML file 'ErrorDialog.fxml'.";
        assert detailsLabel != null : "fx:id=\"detailsLabel\" was not injected: check your FXML file 'ErrorDialog.fxml'.";
        assert imageView != null : "fx:id=\"imageView\" was not injected: check your FXML file 'ErrorDialog.fxml'.";
        assert messageLabel != null : "fx:id=\"messageLabel\" was not injected: check your FXML file 'ErrorDialog.fxml'.";
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'ErrorDialog.fxml'.";
        assert okParent != null : "fx:id=\"okParent\" was not injected: check your FXML file 'ErrorDialog.fxml'.";
    }

}
