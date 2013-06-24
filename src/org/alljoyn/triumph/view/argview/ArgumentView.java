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

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.model.components.arguments.Argument.DIRECTION;

/**
 * An Cell that provides the UI interface for a particular Argument
 * for a remote method invocation. This allows Cells to maintain a specific
 * UI interface that must have particular traits for triumph.
 * 
 * Any super class must create a view that has a root type 
 * of AchorPane.  This allows us to maintain inheritance properties
 * and simple UI standardization.
 * 
 * All Subclasses must have all the appropiate Members of this argument.
 *  
 * @author mhotan 
 */
public abstract class ArgumentView<T> extends AnchorPane {

	/**
	 * Internal Argument this view represents.
	 */
	private final Argument<T> mArg;
	
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
	 * a FXML button with fx id of "mSave".
	 * 
	 * NOTE: not mandatory
	 * 
	 * IE: it will look like: <Button fx:id="mSave" ...etc
	 */
	@FXML protected Button mSave;
	
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
	 * ArgumentCellFactory Controls the ability to create cells.
	 */
	protected ArgumentView(Argument<T> arg) {
		if (arg == null) 
			throw new IllegalArgumentException("ArgumentView, can't init with NULL argument");
		mArg = arg;
	}
	
	public Argument<T> getArgument() {
		return mArg;
	}
	
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
	 * Explicitly hides the save option
	 */
	public void hideSaveOption() {
		if (mSave == null) return;
		mSave.setVisible(true);
	}
	
	/**
	 * If any save features were hidden then then re show those 
	 * features.
	 */
	public void unhideSaveOption() {
		if (mSave == null) return;
		mSave.setVisible(false);
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
		mError.setText("");
	}
	
	public String toString() {
		return mArg.getSignature();
	}
	
	/**
	 * This initiates the view to pull the current state of the argument
	 * based on the UI and save it to the argument.
	 */
	@FXML public abstract void onSaveCurrentValue();
}
