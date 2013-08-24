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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import org.alljoyn.triumph.model.components.SignalContext;
import org.alljoyn.triumph.util.loaders.ViewLoader;

public class SignalReceivedView extends BorderPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<SignalContext> mSignalListView;

    private final ObservableList<SignalContext> mList;

    @FXML
    private HBox mUtilPane;

    public SignalReceivedView() {
        ViewLoader.loadView("SignalsReceivedView.fxml", this);
        
        // Create a list and associate it to the view
        mList = FXCollections.observableArrayList();
        mSignalListView.setItems(mList);
        mSignalListView.setCellFactory(new Callback<ListView<SignalContext>, ListCell<SignalContext>>() {

            @Override
            public ListCell<SignalContext> call(ListView<SignalContext> param) {
                return new SimpleSignalContextListCell();
            }
        });

    }

    /**
     * Adds a received Signal Context to the internal list.
     * <br>User will notified of the new signal.
     * 
     * @param received Received SignalContext 
     */
    public void addSignal(SignalContext received) {
        mList.add(received);
    }

    @FXML
    void initialize() {
        assert mSignalListView != null : "fx:id=\"mSignalListView\" was not injected: check your FXML file 'SignalsReceivedView.fxml'.";
        assert mUtilPane != null : "fx:id=\"mUtilPane\" was not injected: check your FXML file 'SignalsReceivedView.fxml'.";
    }

    private static class SimpleSignalContextListCell extends ListCell<SignalContext> {

        @Override
        public void updateItem(SignalContext item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setTextFill(Color.BLACK);
                setText(item.getDescription());
            }
        }
    }

}
