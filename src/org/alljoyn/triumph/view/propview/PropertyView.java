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

package org.alljoyn.triumph.view.propview;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.alljoyn.bus.BusException;
import org.alljoyn.triumph.model.TriumphModel;
import org.alljoyn.triumph.model.components.AllJoynInterface;
import org.alljoyn.triumph.model.components.AllJoynObject;
import org.alljoyn.triumph.model.components.AllJoynService;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.util.ViewLoader;
import org.alljoyn.triumph.view.argview.ArgumentView;

/**
 * View that is used to contain a Dbus/Alljoyn property
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class PropertyView extends VBox {

	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML
	private AnchorPane mContent;

	@FXML
	private Label mAccessLabel, mInterfaceName, mName, mObjectPath, mServiceName, mErrorLabel;

	@FXML
	private HBox mButtonPane;

	@FXML
	private Pane mButtonSpaceFiller;

	@FXML
	private Button mGetButton, mSetButton;


	/**
	 * A reference to the current Argument View
	 */
	private ArgumentView<?> mCurrentArgument;

	/**
	 * Property to build view with.
	 */
	private final Property mProperty;

	/**
	 * Creates a property view to present the value
	 * @param property 
	 */
	public PropertyView(Property property) {
		ViewLoader.loadView("PropertyView.fxml", this);

		mProperty = property;
		mName.setText(mProperty.getName());

		// Set the visibility of the buttons according to the access permission
		mSetButton.setVisible(mProperty.hasWriteAccess());
		mGetButton.setVisible(mProperty.hasReadAccess());

		StringBuffer buf = new StringBuffer();
		if (mProperty.hasReadAccess() && mProperty.hasWriteAccess())
			buf.append("Read & Write");
		else if (mProperty.hasReadAccess()) 
			buf.append("Read");
		else if (mProperty.hasWriteAccess())
			buf.append("Write");
		else // If there is no access to this property then remove the entire button pane
			getChildren().remove(mButtonPane);

		if (buf.length() > 0) {
			mAccessLabel.setText("Access: " + buf.toString());
		} else {
			getChildren().remove(mAccessLabel);
		}

		AllJoynInterface iface = mProperty.getInterface();
		AllJoynObject object = iface.getObject();
		AllJoynService service = object.getOwner();

		mObjectPath.setText(object.getName());
		mInterfaceName.setText(iface.getName());
		mServiceName.setText(service.getName());

		// Default the signature to be read only.
		if (mProperty.hasReadAccess()) {
			getProperty();
		}

		// Hide the error
		hideError();
	}

	@FXML
	void onGet(ActionEvent event) {
		getProperty();
	}

	@FXML
	void onSet(ActionEvent event) {
		setProperty();
	}

	/**
	 * Gets the current value of the property.  This is done remotely if
	 * this property is a remote Property.
	 * 
	 * @return Argument that pertains to the Property, null otherwise
	 */
	private void getProperty() {
		try {
			Object o = TriumphModel.getInstance().getProperty(mProperty);
			Argument<?> arg = ArgumentFactory.getArgument(mProperty.getName(), mProperty.getSignature(), o);
			mCurrentArgument = arg.getView();
			mContent.getChildren().clear();
			mContent.getChildren().add(mCurrentArgument);
		} catch (BusException e) {
			showError(e.getMessage());
		}
	}

	/**
	 * Set the Property to the current value
	 */
	private void setProperty() {
		try {
			mCurrentArgument.onSaveCurrentValue();
			TriumphModel.getInstance().setProperty(mProperty, mCurrentArgument.getArgument());
		} catch (Exception e) {
			showError(e.getMessage());
		}
	}

	/**
	 * Show error with the associated message 
	 * @param message Message
	 */
	private void showError(String message) {
		mErrorLabel.setVisible(true);
		mErrorLabel.setText(message == null ? "Unknown Error" : message);
	}

	private void hideError() {
		mErrorLabel.setVisible(false);
	}

	@FXML
	void initialize() {
		assert mButtonPane != null : "fx:id=\"mButtonPane\" was not injected: check your FXML file 'PropertyView.fxml'.";
		assert mButtonSpaceFiller != null : "fx:id=\"mButtonSpaceFiller\" was not injected: check your FXML file 'PropertyView.fxml'.";
		assert mContent != null : "fx:id=\"mContent\" was not injected: check your FXML file 'PropertyView.fxml'.";
		assert mGetButton != null : "fx:id=\"mGetButton\" was not injected: check your FXML file 'PropertyView.fxml'.";
		assert mInterfaceName != null : "fx:id=\"mInterfaceName\" was not injected: check your FXML file 'PropertyView.fxml'.";
		assert mName != null : "fx:id=\"mName\" was not injected: check your FXML file 'PropertyView.fxml'.";
		assert mObjectPath != null : "fx:id=\"mObjectPath\" was not injected: check your FXML file 'PropertyView.fxml'.";
		assert mServiceName != null : "fx:id=\"mServiceName\" was not injected: check your FXML file 'PropertyView.fxml'.";
		assert mSetButton != null : "fx:id=\"mSetButton\" was not injected: check your FXML file 'PropertyView.fxml'.";
	}
}
