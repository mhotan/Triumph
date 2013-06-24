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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

import org.alljoyn.triumph.model.components.arguments.BooleanArgument;

/**
 * Argument Cell that controls the presentation of Boolean Objects.
 * 
 * @author mhotan 
 */
public class BooleanArgumentView extends ArgumentView<Boolean> {

	private static final String TRUE = "True"; //TODO Internationalize
	private static final String FALSE = "False";

	/**
	 * Input variable for this
	 */
	@FXML private ToggleButton mBoolToggle;

	/**
	 * Attempt to load a view based off an FXML document.
	 * @param arg Boolean Argument to assign to this view.
	 */
	public BooleanArgumentView(BooleanArgument arg) {
		super(arg);
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BooleanArgView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (!isInputArg()) {
			mBoolToggle.setDisable(true);
			boolean bValue = getValue();
			mBoolToggle.setSelected(bValue);
			mBoolToggle.setText(bValue ? TRUE : FALSE);
		}
	}

	@FXML
	void onToggled() {
		mBoolToggle.setText(getString(mBoolToggle.isSelected()));
		setValue(mBoolToggle.isSelected());
	}

	/**
	 * Using this method can eventually lead to the
	 * internationalization of this app.
	 * @param bool Boolean value to return string of
	 * @return String String representation
	 */
	private static String getString(boolean bool) {
		// TODO Implement Localization and Internationalization.
		return bool ? TRUE : FALSE;
	}

	/**
	 * Return the current value of the Boolean argument
	 * @return True if 
	 */
	public Boolean getValue() {
		return mBoolToggle.isSelected();
	}

	@Override
	public void onSaveCurrentValue() {
		onToggled();
	}

}
