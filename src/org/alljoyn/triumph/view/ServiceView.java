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

package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import org.alljoyn.triumph.model.components.AllJoynService;
import org.alljoyn.triumph.util.SessionPortStorage;
import org.alljoyn.triumph.util.ViewLoader;

/**
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class ServiceView extends HBox {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField mInputField;

	@FXML
	private Label mLabel;
	
	@FXML
	private Button mSaveButton;

	private final AllJoynService mService;
	
	public ServiceView(AllJoynService service) {
		ViewLoader.loadView("ServiceView.fxml", this);
		mService = service;
		mLabel.setText(service.getName());
	}
	
	@FXML
	void onPortSet(ActionEvent event) {
		onSave(event);
	}

	@FXML
	void onSave(ActionEvent event) {
		String text = mInputField.getText();
		
		Short portNum;
		try {
			portNum = Short.valueOf(text);
			if (portNum < 0) 
				portNum = 0;
		} catch (NumberFormatException e) {
			portNum = 0;
		}
		
		mInputField.setText("" + portNum);
		SessionPortStorage.savePort(mService.getName(), portNum);
	}

	@FXML
	void initialize() {
		assert mInputField != null : "fx:id=\"mInputField\" was not injected: check your FXML file 'ServiceView.fxml'.";
		assert mSaveButton != null : "fx:id=\"mSaveButton\" was not injected: check your FXML file 'ServiceView.fxml'.";
	}

}
