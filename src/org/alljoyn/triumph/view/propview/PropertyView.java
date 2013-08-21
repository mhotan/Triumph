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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import org.alljoyn.bus.BusException;
import org.alljoyn.triumph.model.TriumphModel;
import org.alljoyn.triumph.model.components.AllJoynInterface;
import org.alljoyn.triumph.model.components.AllJoynObject;
import org.alljoyn.triumph.model.components.AllJoynService;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.util.loaders.ViewLoader;
import org.alljoyn.triumph.view.argview.ArgumentView;

/**
 * View that is used to present a Dbus/Alljoyn property.
 * This allows users to adjust writable properties.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class PropertyView extends BorderPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane mArgumentPane; // Pane that will hold arguments.

    @FXML
    private HBox mButtonPane;

    @FXML
    private Pane mButtonSpaceFiller;

    @FXML
    private Label mError;

    @FXML
    private Button mGetButton;

    @FXML
    private Button mSetButton;


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
        
        PropertyTitleView titleView = new PropertyTitleView(mProperty);
        HBox.setHgrow(titleView, Priority.ALWAYS);
        setTop(titleView);

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
            mArgumentPane.getChildren().clear();
            mArgumentPane.getChildren().add(mCurrentArgument);
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
        mError.setVisible(true);
        mError.setText(message == null ? "Unknown Error" : message);
    }

    private void hideError() {
        mError.setVisible(false);
    }

    @FXML
    void initialize() {
        assert mArgumentPane != null : "fx:id=\"mArgumentPane\" was not injected: check your FXML file 'PropertyView.fxml'.";
        assert mButtonPane != null : "fx:id=\"mButtonPane\" was not injected: check your FXML file 'PropertyView.fxml'.";
        assert mButtonSpaceFiller != null : "fx:id=\"mButtonSpaceFiller\" was not injected: check your FXML file 'PropertyView.fxml'.";
        assert mError != null : "fx:id=\"mError\" was not injected: check your FXML file 'PropertyView.fxml'.";
        assert mGetButton != null : "fx:id=\"mGetButton\" was not injected: check your FXML file 'PropertyView.fxml'.";
        assert mSetButton != null : "fx:id=\"mSetButton\" was not injected: check your FXML file 'PropertyView.fxml'.";
    }
}
