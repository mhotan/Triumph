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

package org.alljoyn.triumph.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import org.alljoyn.triumph.model.TriumphModel;

/**
 * Main Controlling Mechanism for major functionality
 * 
 * Functionality including:
 * 1. l
 *  
 * @author mhotan
 */
public class MainController implements Initializable {

	private TriumphModel mModel;
	
	@FXML // Refresh Button
	private Button refresh;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		assertExist(refresh, "refresh");

		// Get a reference to the model
		mModel = TriumphModel.getInstance();
	}
	
	/**
	 * Refresh the screen repopulating the entire screen with all the available Bus attachments.
	 * 
	 * @param event characteristics of the button press
	 */
	public void refreshButtonClicked(ActionEvent event) {
//		LOGGER.log(Level., msg)("refresh button clicked");
		// Let the model broadcast its changes
		mModel.sync();
	}
	
	/**
	 * Assert the existence of the element and show message on failure.
	 * 
	 * @param element Element to check for existence.
	 * @param id Message to present on failure.
	 */
	private void assertExist(Button element, String id) {
		assert element != null : "fx:id=\\" + id + " was not injected: check your FXML file 'Main.fxml'.";
	}

	
	
}
