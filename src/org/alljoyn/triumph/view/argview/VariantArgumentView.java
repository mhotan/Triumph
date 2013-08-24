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

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.model.components.arguments.VariantArgument;

/**
 * Class that presents a variant argument to the user.  Allowing the user to  
 */
public class VariantArgumentView extends ArgumentView<Variant>  {
	
	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private BorderPane mComponentPane;
	@FXML private Label mError;
	@FXML private TextField mSignatureInput;

	/**
	 * Variant Argument for this.
	 */
	private final VariantArgument mArg;
	
	/**
	 * Internal Argument View to present
	 */
	private ArgumentView<?> mCurrentValue;

	/**
	 * Signature of the current 
	 */
	private String mArgSignature;

	@FXML
	void initialize() {
		assert mComponentPane != null : "fx:id=\"mComponentPane\" was not injected: check your FXML file 'VariantArgView.fxml'.";
		assert mError != null : "fx:id=\"mError\" was not injected: check your FXML file 'VariantArgView.fxml'.";
		assert mSignatureInput != null : "fx:id=\"mSignatureInput\" was not injected: check your FXML file 'VariantArgView.fxml'.";
	}

	/**
	 * Creates an argument view and populates the variant if it has a existing value.
	 * 
	 * @param arg VariantArgument to present in this view.
	 */
	public VariantArgumentView(VariantArgument arg) {
		super(arg);
//		ViewLoader.loadView("VariantArgView.fxml", this);
		mArg = arg;
		
		// Label the Argument to present
		setLabel(arg.getSignature());
		// Hide any common errors
		hideError();

		mCurrentValue = null;
		mArgSignature = null;

		// Attempt to extract any current values
		Variant var = arg.getValue();
		if (var == null) return;

		// If there is a current value for this argument
		setValue(var);
	}
	
	@Override
    protected String getFXMLFileName() {
        return "VariantArgView.fxml";
    }

	@FXML
	void onSignatureSet(ActionEvent event) {
		String signature = mSignatureInput.getText();

		// Ignore any null text
		if (signature == null) return;

		// Check if the the signature does not
		// match the current one.
		if (signature.equals(mArgSignature)) return;
		
		// We have a new signature, there fore we have to create
		// a new argument.
		Argument<?> argument = ArgumentFactory.getArgument(signature, mArg.getName(), mArg.getDirection());
		if (argument == null) {
			// Show the error that the supported signature is not supported
			showError(signature + " does not resolve to a distinct type");
		} else {
			// We have a 
			setVariant(argument);
		}
	}

	/**
	 * Sets the view to present this argument.
	 * @param arg Argument to present.
	 */
	private void setVariant(Argument<?> arg) {
		// Get the alljoyn type signature
	    mLabel.setText(arg.getSignature());
		mArgSignature = arg.getDBusSignature();
		mCurrentValue = EditableArgumentViewFactory.produceView(arg);
		mCurrentValue.hideSaveButton();
		mComponentPane.setCenter(mCurrentValue);
	}
	
	@Override
	protected void setValue(Variant value) {
		hideError();
		
		Object current = null;
		String signature = null;
		try {
			// Use the signature of this argument and the object to get
			// the correct argument
			signature = value.getSignature();
			// Extract the current object as a basic Object
			current = value.getObject(Object.class);
			Argument<?> currentArg = ArgumentFactory.getArgument(mArg.getName(), signature, current);
			
			// Set the value in the current UI.
			setVariant(currentArg);
			
			// Finally set the value of the argument.
			super.setValue(value);
		} catch (BusException e) {
			if (signature == null) {
				showError("Unable to show variant " + value);
			} else if (current == null) {
				showError("Unable to show variant with signature: " + signature);
			} else {
				showError(e.getMessage());
			}
		} 	
	}

	@Override
	@FXML
	public String onSetCurrentValue() {
		if (mCurrentValue == null) return null;
		
		String error = mCurrentValue.onSetCurrentValue();
		if (error != null) return error;
		
		// Set the value to the variant
		setValue(new Variant(mCurrentValue.getValue(), mArgSignature));
		// Return null notifying success.
		return null;
	}

	@Override
	public void setEditable(boolean editable) {
		// Disable the the input box.
		mSignatureInput.setEditable(editable);

		// Check if there current 
		if (mCurrentValue == null) return;
		mCurrentValue.setEditable(editable);
	}

	@Override
	protected boolean isEditable() {
		if (mCurrentValue == null) return false;
		return mCurrentValue.isEditable();
	}

}
