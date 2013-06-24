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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.alljoyn.triumph.model.components.Method;

public class MethodInvokationHandler implements EventHandler<ActionEvent> {

	private final Pane mPane;
	private final javafx.scene.control.Label mLabel;
	private final Button mButton;
	
	private Method mMethod;
	
	public MethodInvokationHandler(Pane pane) {
		mPane = pane;
		mLabel = new javafx.scene.control.Label("");
		Pane innerpane = new Pane();
		mButton = new Button("invoke");
		
		// In case outer pane is an Hbox.
		HBox.setHgrow(innerpane, Priority.SOMETIMES);
		// In case outer pane is a VBox
		VBox.setVgrow(innerpane, Priority.SOMETIMES);
		
		mPane.getChildren().addAll(mLabel, innerpane, mButton);
	}
	
	
	public void setMethod(Method method) {
		mMethod = method;
		update();
	}
	
	private void update() {
		if (mMethod == null) {
			mLabel.setText("");
			mButton.setVisible(false);
			return;
		} 
		mLabel.setText(mMethod.getName());
		mButton.setVisible(true);
	}
	
	@Override
	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

}
