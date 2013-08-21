package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.util.ArgumentStorage;
import org.alljoyn.triumph.util.loaders.ViewLoader;

public class SaveArgumentDialog extends GridPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private HBox actionParent;

    @FXML
    private Button mCancelButton;

    @FXML
    private Label mError;

    @FXML
    private TextField mInput;

    @FXML
    private Button mOkButton;

    @FXML
    private Button mOverwriteButton;

    @FXML
    private Label messageLabel;

    @FXML
    private HBox okParent;

    private final Argument<?> mArg;

    private final ArgumentStorage mStorage;

    /**
     * Creates a dialog to save the argument assigned to it.
     * 
     * @param argToSave Argument to save.
     */
    public SaveArgumentDialog(Argument<?> argToSave) {
        ViewLoader.loadView("SaveArgumentDialog.fxml", this);
        mArg = argToSave;
        mStorage = ArgumentStorage.getInstance();

        mOverwriteButton.managedProperty().bind(mOverwriteButton.visibleProperty());
        mOverwriteButton.setVisible(false);

        mError.managedProperty().bind(mError.visibleProperty());
        hideError();

        mInput.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {

            }
        });
    }

    @FXML
    void onSetName(ActionEvent event) {
        // Reset the values
        mOverwriteButton.setVisible(false);
        mOkButton.setVisible(true);
        hideError();

        String nameToSave = mInput.getText();
        if (nameToSave == null || nameToSave.isEmpty()) {
            showError("Can't save name with '" + nameToSave + "'");
            return;
        }

        mArg.setSaveByName(nameToSave);
        // Check if name is already taken.
        if (mStorage.hasArgument(mArg)) {
            showError("Name already exist! Would you like to overwrite?");
            mOverwriteButton.setVisible(true);
            mOkButton.setVisible(false);
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        Stage stage = (Stage) getScene().getWindow();
        stage.close();
    }

    @FXML
    void onOk(ActionEvent event) {
        onSetName(event);
    }

    @FXML
    void onOverwrite(ActionEvent event) {
        mOverwriteButton.setVisible(false);
        mOkButton.setVisible(true);
        hideError();

        mStorage.saveArgument(mArg);
    }

    private void showError(String message) {
        if (message == null)
            message = "Unknown error";
        mError.setText(message);
        mError.setVisible(true);
    }

    private void hideError() {
        mError.setVisible(false);
    }

    @FXML
    void initialize() {
        assert actionParent != null : "fx:id=\"actionParent\" was not injected: check your FXML file 'SaveArgumentDialog.fxml'.";
        assert mCancelButton != null : "fx:id=\"mCancelButton\" was not injected: check your FXML file 'SaveArgumentDialog.fxml'.";
        assert mError != null : "fx:id=\"mError\" was not injected: check your FXML file 'SaveArgumentDialog.fxml'.";
        assert mInput != null : "fx:id=\"mInput\" was not injected: check your FXML file 'SaveArgumentDialog.fxml'.";
        assert mOkButton != null : "fx:id=\"mOkButton\" was not injected: check your FXML file 'SaveArgumentDialog.fxml'.";
        assert mOverwriteButton != null : "fx:id=\"mOverwriteButton\" was not injected: check your FXML file 'SaveArgumentDialog.fxml'.";
        assert messageLabel != null : "fx:id=\"messageLabel\" was not injected: check your FXML file 'SaveArgumentDialog.fxml'.";
        assert okParent != null : "fx:id=\"okParent\" was not injected: check your FXML file 'SaveArgumentDialog.fxml'.";
    }
}