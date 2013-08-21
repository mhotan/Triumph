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

package org.alljoyn.triumph.view.argview;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import org.alljoyn.triumph.model.components.arguments.Argument;

/**
 * A simple cell to present a single element argument
 * for a ListView UI. This class can be extends to implement
 * specific simple types such as Boolean, Integer, String etc
 * 
 * @author mhotan
 */
public abstract class SimpleArgumentView<T> extends ArgumentView<T> {

    /**
     * This is the main input variable for this view
     */
    @FXML protected TextField mInput;

    /**
     * Create the basis for a Simple Argument Cell.
     * @param argument argument to assign cell to.
     */
    protected SimpleArgumentView(Argument<T> argument) {
        super(argument);
//        ViewLoader.loadView("SimpleArgView.fxml", this);

        // Output argument should have an output 
        // value assigned.  This may be null
        T value = getValue();
        mInput.setText(value == null ? "Null" : value.toString());
        mInput.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                if (newValue == null) {
                    // Ignore new values
                    return;
                }

                attemptToSet(newValue);
            }
        });
    }
    
    @Override
    protected String getFXMLFileName() {
        return "SimpleArgView.fxml";
    }

    /**
     * Save the current value.
     */
    @FXML
    public String onSaveCurrentValue() {
        // hide any current error message
        return attemptToSet(mInput.getText());
    }

    /**
     * Attempts to set the value of the argument to the specific type.
     * 
     * @param value String representation of the Value to set the argument to
     * @return Error string or null otherwise
     */
    private String attemptToSet(String value) {
        hideError();
        StringBuffer buffer = new StringBuffer();
        if (setArgument(value.trim(), buffer)) 
            return null; // Successful set the argument
        showError(buffer.toString());
        return buffer.toString();
    }

    @Override
    public void setEditable(boolean editable) {
        mInput.setEditable(editable);
    }

    @Override
    protected boolean isEditable() {
        return mInput.isEditable();
    }

    /**
     * Attempts to set the value of the argument to the value stored in
     * the raw string.
     * 
     * If the raw value is not able to converted to the correct type, false will be returned 
     * and the error buffer will be filled with the Error message.
     * 
     * If successfully inputted then true is returned and the buffer
     * is filled with the raw value the argument was set to.  Note that 
     * the raw String value set to can be changed do to some conversion properties
     * 
     * @param raw Raw value
     * @param errorBuffer Buffer to hold the message.
     * @return true if the argument was set, false if error occurred
     */
    protected abstract boolean setArgument(String raw, StringBuffer errorBuffer);

}