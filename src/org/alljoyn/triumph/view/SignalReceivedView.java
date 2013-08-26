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

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import org.alljoyn.triumph.model.components.SignalContext;
import org.alljoyn.triumph.util.loaders.ViewLoader;

public class SignalReceivedView extends BorderPane {

    
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    private final ListView<SignalContext> mSignalListView;

    private final ObservableList<SignalContext> mList;
    private final ObservableList<SignalContectTableItem> mTableItems;

    private final SignalReceivedListener mListener;
    
    @FXML
    private HBox mUtilPane;

    public SignalReceivedView(SignalReceivedListener listener) {
        ViewLoader.loadView("SignalsReceivedView.fxml", this);
        mListener = listener;
        
        mTableItems = FXCollections.observableArrayList();
        TableView<SignalContectTableItem> tv = new TableView<SignalContectTableItem>(mTableItems);
        tv.setEditable(true);
        
        TableColumn<SignalContectTableItem, String> tc = new TableColumn<SignalContectTableItem, String>("Signal Received");
        tc.setMinWidth(200);
        tc.setCellValueFactory(new PropertyValueFactory<SignalContectTableItem, String>("description"));
        
        tv.getColumns().add(tc);
        
        
     // Create a list and associate it to the view
        mList = FXCollections.observableArrayList();
        mSignalListView = new ListView<SignalContext>(mList);
        mSignalListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        mSignalListView.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                SignalContext signal = mSignalListView.getSelectionModel().getSelectedItem();
                mListener.onSignalContextSelected(signal);
            }
        });
        setCenter(tv);
    }

    /**
     * Adds a received Signal Context to the internal list.
     * <br>User will notified of the new signal.
     * 
     * @param received Received SignalContext 
     */
    public void addSignal(SignalContext received) {
        mList.add(received);
        mTableItems.add(new SignalContectTableItem(received));
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
    
    public interface SignalReceivedListener {
        
        public void onSignalContextSelected(SignalContext context);
        
    }

   public static class SignalContectTableItem {
    
       private final SimpleStringProperty description;
       
       private final SignalContext mContext;
       
       private SignalContectTableItem(SignalContext context) {
           mContext = context;
           description = new SimpleStringProperty(mContext.getDescription());
       }
       
       public String getDescription() {
           return description.get();
       }
       public void setDescription(String fName) {
           description.set(fName);
       }
       
   }
}
