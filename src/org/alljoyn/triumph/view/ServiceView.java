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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import org.alljoyn.triumph.model.components.AllJoynService;
import org.alljoyn.triumph.model.components.AllJoynService.SERVICE_TYPE;
import org.alljoyn.triumph.util.SessionPortStorage;
import org.alljoyn.triumph.util.loaders.ViewLoader;


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
    private Label mError;

    @FXML
    private TextField mInputField;

    @FXML
    private Label mLabel;

    private final AllJoynService mService;

    /**
     * 
     * @param service
     */
    public ServiceView(AllJoynService service) {
        ViewLoader.loadView("ServiceView.fxml", this);
        mService = service;
        mLabel.setText(service.getName());
        mInputField.setText("" + SessionPortStorage.getPort(service.getName()));

        // Only allow change in ports for remote objects
        SERVICE_TYPE type = service.getServiceType();
        if (type == SERVICE_TYPE.LOCAL) {
            mInputField.setEditable(false);
        } else if (type == SERVICE_TYPE.REMOTE) {
            mInputField.setEditable(true);
        }

        hideError();

        mInputField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                if (newValue == null) return;
                savePort(newValue);
            }

        });
    }

    public Label getLabel() {
        return mLabel;
    }

    @FXML
    void onPortSet(ActionEvent event) {
        savePort(mInputField.getText().trim());
    }

    private void savePort(String portString) {
        hideError(); // Attempt to hide error

        String text = portString;
        Short portNum;
        try {
            portNum = Short.valueOf(text);
            if (portNum < 0) 
                portNum = 0;
            mInputField.setText("" + portNum);
            SessionPortStorage.savePort(mService.getName(), portNum);
        } catch (NumberFormatException e) {
            showError();
        }
    }

    private void hideError() {
        mError.setVisible(false);
        getChildren().remove(mError);
    }

    private void showError() {
        if (getChildren().contains(mError))
            return;
        mError.setVisible(true);
        getChildren().add(mError);
    }

    @FXML
    void initialize() {
        assert mError != null : "fx:id=\"mError\" was not injected: check your FXML file 'ServiceView.fxml'.";
        assert mInputField != null : "fx:id=\"mInputField\" was not injected: check your FXML file 'ServiceView.fxml'.";
        assert mLabel != null : "fx:id=\"mLabel\" was not injected: check your FXML file 'ServiceView.fxml'.";
    }

}
