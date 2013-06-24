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

import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

import org.alljoyn.triumph.model.TriumphModel;
import org.alljoyn.triumph.model.components.AllJoynComponent;
import org.alljoyn.triumph.model.components.AllJoynService;
import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.Signal;

/**
 * A Click handler that handles the presentation and reaction of 
 * when an Object Attribute is clicked on.  IE this deciphers if the
 * object is amn instance of Service, Object, Method, Signal, or Property.
 * Then calls upon the model to decide on which action to take
 * 
 * Note: the model is responsible for generating the Introspect data.
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class ObjectAttributeTreeClickHandler implements EventHandler<MouseEvent> {

	/**
	 * A reference to the tree so that we can track down the selected item.
	 */
	private final TreeView<AllJoynComponent> mTree;
	
	/**
	 * 
	 * 
	 * @param model model to notify of data 
	 * @param treeView List of to handle click
	 */
	public ObjectAttributeTreeClickHandler(TreeView<AllJoynComponent> treeView) {
		assert treeView != null: "Cannot create a handler with a null list";

		mTree = treeView;

		// Actually do the registration of this list view with
		// this handler
		mTree.setOnMouseClicked(this);
	}

	@Override
	public void handle(MouseEvent event) {
		TreeItem<AllJoynComponent> treeItem = mTree.getSelectionModel().getSelectedItem();
		// Handle a click that is not on a tree item.
		if (treeItem == null) return;
		
		AllJoynComponent element = treeItem.getValue();

		// Attempt to create extract the specific instance type.
		AllJoynService service = element.toService();
		Method method = element.toMethod();
		Signal signal = element.toSignal();
		Property prop = element.toProperty();

		TriumphModel model = TriumphModel.getInstance();
		
		// Tell the model that the component was selected.
		if (service != null)
			model.onServiceSelected(service);
		if (method != null) 
			model.onMethodSelected(method);
		if (signal != null) 
			model.onSignalSelected(signal);
		if (prop != null) {
			model.onPropertySelected(prop);
		}
	}

}
