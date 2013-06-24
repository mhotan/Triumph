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

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

	private static final String DEF_ERROR_MSG = "Error";

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

		// FXML load the fxml layer.
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().
				getResource("SimpleArgView.fxml"));

		// Root is defined by fx:root is FXML
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Label the Argument to present
		setLabel(argument.getSignature());
		
		// Hide any error message
		hideError();

		// If the argument is an input argument 
		// we allow the uesr to continue to alter its state
		if (isInputArg())
			return;

		// Output argument should have an output 
		// value assigned.  This may be null
		T value = getValue();
		mInput.setText(value == null ? "Null" : value.toString());
	}
	
	@Override
	public void hideSaveOption() {
		super.hideSaveOption();
		mInput.setEditable(false);
	}

	@Override
	public void unhideSaveOption() {
		super.unhideSaveOption();
		mInput.setEditable(true);
	}
	
	/**
	 * Save the current value.
	 */
	@FXML
	public void onSaveCurrentValue() {
		// hide any current error message
		hideError();

		String raw = mInput.getText();
		StringBuffer buffer = new StringBuffer();
		if (setArgument(raw.trim(), buffer)) 
			return; // Successful setting

		// Handle failure case
		String error = buffer.length() == 0 ? DEF_ERROR_MSG: buffer.toString();
		showError(error);
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