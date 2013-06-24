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
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

import org.alljoyn.bus.Variant;
import org.alljoyn.triumph.model.components.arguments.VariantArgument;

public class VariantArgumentView extends ArgumentView<Variant>  {

	@FXML private ComboBox<String> mSelection;
	@FXML private Pane mPayload;
	
	/**
	 * 
	 * @param arg
	 */
	public VariantArgumentView(VariantArgument arg) {
		super(arg);
		// FXML load the fxml layer.
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().
				getResource("VariantView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Label the Argument to present
		setLabel(arg.getSignature());
		hideError();

		// TODO Intialize drop down.
		
		// If the argument is an input argument 
		// we allow the uesr to continue to alter its state
		if (isInputArg())
			return;

		// Hide the irrelavent components
		hideSaveOption();
	}
	

	@Override
	@FXML
	public void onSaveCurrentValue() {

	}

}
