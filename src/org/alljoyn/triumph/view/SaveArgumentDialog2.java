package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.model.components.arguments.Argument.DIRECTION;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.util.AJConstant;
import org.alljoyn.triumph.util.ArgumentStorage;
import org.alljoyn.triumph.util.loaders.ViewLoader;
import org.alljoyn.triumph.view.arguments.editable.ArgumentView;
import org.alljoyn.triumph.view.arguments.editable.EditableArgumentViewFactory;

/**
 * 
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class SaveArgumentDialog2 extends BorderPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ScrollPane mArgPane;

    @FXML
    private HBox mButtonBar;

    @FXML
    private Pane mButtonFillerC1, mButtonFillerL1, mButtonFillerL2, mButtonFillerL3, mButtonFillerR1;

    @FXML
    private Button mCancelButton, mSaveButton;

    @FXML
    private Label mError, mNameLabel, mSignatureLabel;

    @FXML
    private GridPane mGridPane;

    @FXML
    private TextField mNameInput, mSignatureInput;

    /**
     * Argument to show to save.
     */
    private final Argument<?> mArg;
    private final ArgumentView<?> mArgView;
    private final CloseListener mCloseListener;

    private static final String SAVE = "Save";
    private static final String OVERWRITE = "Overwrite";
    
    public SaveArgumentDialog2(CloseListener listener) {
        this(ArgumentFactory.getArgument("" + AJConstant.ALLJOYN_VARIANT, "", DIRECTION.IN), listener);
        mSignatureInput.setEditable(true);
    }

    public SaveArgumentDialog2(Argument<?> argument, CloseListener listener) {
        mArg = argument;
        mCloseListener = listener;
        
        ViewLoader.loadView("SaveArgumentView2.fxml", this);

        // If the error message disappears then have the space it took up disappear 
        mError.managedProperty().bind(mError.visibleProperty());
        hideError();

        // Make sure the save button is always as wide as the save button.
        mSaveButton.minWidthProperty().bind(mCancelButton.widthProperty());

        // Extract the argument view to present in this view.
        mArgView = EditableArgumentViewFactory.produceView(mArg);
//        prefWidthProperty().bind(mArgView.prefWidthProperty());
        // Hide the save button
        mArgView.hideSaveButton();
        mArgView.prefWidthProperty().bind(widthProperty());
        mArgPane.setContent(mArgView);

        // Set the input Text to a savable name
        String name = getSavableName();
        mNameInput.setText(name);
        mNameLabel.setTooltip(new Tooltip("The name to associate to this argument"));
        mNameInput.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent arg0) {
                checkName();
            }
        });
        
        mSignatureInput.setText(mArg.getDBusSignature());
        mSignatureInput.setEditable(false);
    }
    
    @FXML
    void onCancel(ActionEvent event) {
        mCloseListener.onRequestClose();
    }

    @FXML
    void onSave(ActionEvent event) {
        boolean validName = checkName();
        String currentName = mNameInput.getText();
        if (validName || mSaveButton.getText().equals(OVERWRITE)) {
            saveArg(currentName);
            mSaveButton.setText(SAVE);
            hideError();
            mCloseListener.onRequestClose();
            return;
        }
    }

    private String getSavableName() {
        // Try to decipher a name to associate to this argument
        // First attempt to use the last saved by name
        // Then the actual name of the argument
        // Then finally use the human readable signature
        // Then associate a number making the unique 
        String name = mArg.getSaveByName();
        if (name == null) {
            name = mArg.getName().trim();
            if (name.isEmpty()) {
                name = mArg.getSignature();
            }
        }
        
        mArg.setSaveByName(name);
        int number = 0;
        // While there is an argument with that name then
        // attempt to create a new unused name by appending a number.
        while (hasArg(name)) {
            String potentialSuffix = "(" + (number) + ")";
            if (name.endsWith(potentialSuffix)) {
                name = name.replace(potentialSuffix, "");
            }
            number += 1;
            name += "(" + (number) + ")";
            mArg.setSaveByName(name);
        }
        return name;
    }
    
    /**
     * Commits the current name in the text field.
     * Checks if the name is valid 
     * @return true if name is valid, false otherwise
     */
    private boolean checkName() {
        hideError();
        mSaveButton.setText(SAVE);
        
        String name = mNameInput.getText().trim();
        // update text field with correct name
        mNameInput.setText(name);
        
        // check if the name is empty as in there is no characters
        if (name.isEmpty()) {
            showError("Empty Name");
            return false;
        }
        
        // Set the argument save by name
        if (hasArg(name)) {
            showError("Name already taken");
            mSaveButton.setText(OVERWRITE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks whether there is already an argument of the same type with associated name. 
     * 
     * @param name Name to associate with this argument
     * @return whether or not there is already an argument of the same type with this name.
     */
    private boolean hasArg(String name) {
        String oldName = mArg.getSaveByName();
        mArg.setSaveByName(name);
        boolean val =  ArgumentStorage.getInstance().hasArgument(mArg);
        mArg.setSaveByName(oldName);
        return val;
    }
    
    /**
     * Saves the argument with this name.
     * <br>If the name of this argument is taken by an argument of the same type.
     * @param name name to associate to this argument.
     */
    private void saveArg(String name) {
        mArg.setSaveByName(name);
        ArgumentStorage.getInstance().saveArgument(mArg);
    }

    /**
     * Hides the error message
     */
    private void hideError() {
        mError.setVisible(false);
    }

    /**
     * Shows error message.
     * @param message Message to show
     */
    private void showError(String message) {
        if (message == null)
            message = "Unknown error";
        mError.setText(message);
        mError.setVisible(true);
    }

    @FXML
    void initialize() {
        assert mArgPane != null : "fx:id=\"mArgPane\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mButtonBar != null : "fx:id=\"mButtonBar\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mButtonFillerC1 != null : "fx:id=\"mButtonFillerC1\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mButtonFillerL1 != null : "fx:id=\"mButtonFillerL1\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mButtonFillerL2 != null : "fx:id=\"mButtonFillerL2\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mButtonFillerL3 != null : "fx:id=\"mButtonFillerL3\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mButtonFillerR1 != null : "fx:id=\"mButtonFillerR1\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mCancelButton != null : "fx:id=\"mCancelButton\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mError != null : "fx:id=\"mError\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mGridPane != null : "fx:id=\"mGridPane\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mNameInput != null : "fx:id=\"mNameInput\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mNameLabel != null : "fx:id=\"mNameLabel\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mSaveButton != null : "fx:id=\"mSaveButton\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mSignatureInput != null : "fx:id=\"mSignatureInput\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
        assert mSignatureLabel != null : "fx:id=\"mSignatureLabel\" was not injected: check your FXML file 'SaveArgumentView2.fxml'.";
    }

    public interface CloseListener {
        
        public void onRequestClose();
        
    }

}
