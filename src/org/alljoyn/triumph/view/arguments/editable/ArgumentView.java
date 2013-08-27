/******************************************************************************
 * Copyright 2013, Qualcomm Innovation Center, Inc.
 *
 *    All rights reserved.
 *    This file is licensed under the 3-clause BSD license in the NOTICE.txt
 *    file for this project. A copy of the 3-clause BSD license is found at:
 *
 *        http://opensource.org/licenses/BSD-3-Clause.
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the license is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the license for the specific language governing permissions and
 *    limitations under the license.
 ******************************************************************************/

package org.alljoyn.triumph.view.arguments.editable;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.model.components.arguments.Argument.DIRECTION;
import org.alljoyn.triumph.util.loaders.ViewLoader;

/**
 * Custom JavaFX UI control element that represent a general argument.  This provides basic 
 * UI implementations for all sub classed views.
 * 
 * This also enforces and guides sub classes to implement and provide basic components for
 * basic actions.
 * 
 * All Subclasses must have all the appropiate Members of this argument.
 *  
 * @author mhotan 
 */
public abstract class ArgumentView<T> extends HBox {

    protected final Logger LOG = Logger.getLogger(getClass().getSimpleName());

    /**
     * Internal Argument this view represents.
     */
    protected final Argument<T> mArg;

    /**
     * NOTE: Every ArgumentView subclass must define this
     * label!
     * 
     * <b> Therefore there always exist a Label
     * element in the XML doc that contains <Label fx:id="mLabel" ... etc
     */
    @FXML protected Label mLabel;

    /**
     * ArgumentView that contains a save button will contains 
     * a FXML Label with fx id of "mError".
     * 
     * NOTE: not mandatory
     * 
     * IE: it will look like: <Label fx:id="mError" ...etc
     */
    @FXML protected Label mError;

    /**
     * Button that allows the user to save the current argument value.
     */
    @FXML protected Button mSave;

    /**
     * Button that explicitly request to set the value to null
     */
    @FXML protected Button mNullify;

    /**
     * ArgumentCellFactory Controls the ability to create cells.
     */
    protected ArgumentView(Argument<T> arg) {
        if (arg == null) 
            throw new IllegalArgumentException("ArgumentView, can't init with NULL argument");

        // Get the file name from the subclass
        String fileName = getFXMLFileName();
        if (fileName == null) {
            throw new RuntimeException(getClass().getSimpleName() + " provided an illegal null file name to load.");
        }

        // Load the view as using this instance as a root and controller.
        ViewLoader.loadView(fileName, this);

        // Set up the fields if they exists.
        mArg = arg;

        // For any component that exists bind the views visibility to its layout property
        // That is if a view is not visible then it should not effect the layout.
        // IE There shouldn't be a space filler for that component.
        // Therefore we bind the property of the layout to the property of the view.
        if (mLabel != null) {
            mLabel.managedProperty().bind(mLabel.visibleProperty());
        }
        if (mError != null) {
            mError.managedProperty().bind(mError.visibleProperty());
        }
        if (mSave != null) {
            mSave.managedProperty().bind(mSave.visibleProperty());
            mSave.setTooltip(new Tooltip("Save this argument for later use"));
        }
        if (mNullify != null) {
            mNullify.managedProperty().bind(mNullify.visibleProperty());
            mNullify.setTooltip(new Tooltip("Explicitly ues Null for this value"));
        }

        // Set the name of the argument
        setLabel(mArg.getSignature());

        // Hide any possible errors
        hideError();
        
        
    }

    /**
     * @return the internal argument
     */
    public Argument<T> getArgument() {
        return mArg;
    }

    /**
     * Every sublclass must provide a name of a fxml file to use
     * as it's view.  Therefore it is mandated that all subclasses return 
     * a String representation of a FXML file.
     * @return String file name
     */
    protected abstract String getFXMLFileName();

    /**
     * Gets the value of the argument as it appears at method
     * invocation time. 
     * 
     * @return Java Object representation of the argument value
     */
    public T getValue() {
        return mArg.getValue();
    }

    /**
     * Sets the value of the argument to the input desired
     * @param value Input argument to set value to.
     */
    protected void setValue(T value) {
        mArg.setValue(value);
    }

    /**
     * @return Whether is input argument.
     */
    protected boolean isInputArg() {
        return mArg.isInput();
    }

    /**
     * Gets the direction of the argument
     * @return Direction of the argument.
     */
    protected DIRECTION getArgDirection() {
        return mArg.getDirection();
    }

    /**
     * Return String representation of the label
     * 
     * @return String representation of the label
     */
    protected void setLabel(String label) {
        if (mLabel == null) return;
        mLabel.setText(label);
    }

    /**
     * Hides all the labels that don't pertain to showing 
     * an error.
     */
    public void hideLabel() {
        if (mLabel == null) return;
        mLabel.setVisible(false);
    }

    /**
     * Unhide all the labels if they were hidden
     */
    public void unhideLabel() {
        if (mLabel == null) return;
        mLabel.setVisible(true);
    }

    /**
     * Attempts to present an error to the user.
     * @param errMsg Error message to present
     */
    protected void showError(String errMsg) {
        if (mError == null) return;
        mError.setVisible(true);
        mError.setText(errMsg == null || errMsg.isEmpty() ? "Error": errMsg);
    }

    /**
     * Hides any showing error message
     */
    protected void hideError() {
        if (mError == null) return;
        mError.setVisible(false);
    }
    
    /**
     * Sets whether the save button can be used or not.
     * @param enable
     */
    public void setSaveEnable(boolean enable) {
        if (mSave == null) return;
        mSave.setDisable(!enable);
    }
    
    /**
     * Show the save button if it is available
     */
    public void showSaveButton() {
        if (mSave == null) return;
        mSave.setVisible(true);
    }
    
    /**
     * Hide the save button if it is available
     */
    public void hideSaveButton() {
        if (mSave == null) return;
        mSave.setVisible(false);
    }
    
    /**
     * User request to save the state of the current argument.
     * 
     * @param event Event that generated the save request
     */
    protected void onSave(ActionEvent event) {
        // TODO Show a dialog querying to save the argument
        
    }
    
    /**
     * Show the save button if it is available
     */
    public void showNullButton() {
        if (mNullify == null) return;
        mNullify.setVisible(true);
    }
    
    /**
     * Hide the save button if it is available
     */
    public void hideNullButton() {
        if (mNullify == null) return;
        mNullify.setVisible(false);
    }

    @Override
    public String toString() {
        return "View for " + mArg.getSignature();
    }

    /**
     * This initiates the view to pull the current state of the argument
     * based on the UI and save it to the argument.
     * 
     * @return null on success, else Error message
     */
    @FXML public abstract String onSetCurrentValue();

    /**
     * Set the ability to edit this argument.
     * 
     * @param editable true if this argument wishes to be editable, false otherwise.
     */
    public abstract void setEditable(boolean editable);

    /**
     * Each multi element structure has the ability to determine if a 
     * @return whether this argument is editable
     */
    protected abstract boolean isEditable();
}
