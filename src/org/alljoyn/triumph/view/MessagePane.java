package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import org.alljoyn.triumph.util.loaders.ViewLoader;

public class MessagePane extends VBox {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label mMessage;

    @FXML
    private Label mTitle;
    
    public MessagePane(String title, String message) {
        ViewLoader.loadView(this);
        mTitle.setText(title == null ? "" : title);
        mMessage.setText(message == null ? "" : message);
    }
    
    @FXML
    void initialize() {
        assert mMessage != null : "fx:id=\"mMessage\" was not injected: check your FXML file 'MessagePane.fxml'.";
        assert mTitle != null : "fx:id=\"mTitle\" was not injected: check your FXML file 'MessagePane.fxml'.";
    }
}
