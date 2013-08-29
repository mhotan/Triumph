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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import org.alljoyn.triumph.model.components.SignalContext;
import org.alljoyn.triumph.util.loaders.ViewLoader;

public class SignalReceivedView extends BorderPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    private final ListView<SignalContext> mSignalListView;
    private final TableView<SignalContectTableItem> mTableView;
    
    
    private final ObservableList<SignalContext> mList;
    private final ObservableList<SignalContectTableItem> mTableItems;

    private final SignalReceivedListener mListener;
    
    @FXML
    private HBox mUtilPane;

    public SignalReceivedView(SignalReceivedListener listener) {
        ViewLoader.loadView("SignalsReceivedView.fxml", this);
        mListener = listener;
        
        mTableItems = FXCollections.observableArrayList();
        mTableView = new TableView<SignalContectTableItem>(mTableItems);
        mTableView.setEditable(true);
        
        // Bind the width of this table to the width of this view.
        mTableView.prefWidthProperty().bind(widthProperty());
        
        TableColumn<SignalContectTableItem, String> tc = new TableColumn<SignalContectTableItem, String>("Signal Received");
        tc.setMinWidth(200);
        tc.setCellValueFactory(new PropertyValueFactory<SignalContectTableItem, String>("description"));
        mTableView.getColumns().add(tc);
        
     // Create a list and associate it to the view
        mList = FXCollections.observableArrayList();
        mSignalListView = new ListView<SignalContext>(mList);
        mSignalListView.prefWidthProperty().bind(widthProperty());
        
        mSignalListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        mSignalListView.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                SignalContext signal = mSignalListView.getSelectionModel().getSelectedItem();
                mListener.onSignalContextSelected(signal);
            }
        });
        setCenter(mSignalListView);
    }

    /**
     * Adds a received Signal Context to the internal list.
     * <br>User will notified of the new signal.
     * 
     * @param received Received SignalContext 
     */
    public void addSignal(SignalContext received) {
        add(received);
        add(new SignalContectTableItem(received));
    }
    
    private void add(SignalContext context) {
        List<SignalContext> signals = new ArrayList<SignalContext>(mList);
        signals.add(context);
        mList.removeAll(mList);
        for (SignalContext signal : signals)
            mList.add(signal);
    }
    
    private void add(SignalContectTableItem item) {
        List<SignalContectTableItem> signals = new ArrayList<SignalReceivedView.SignalContectTableItem>(mTableItems);
        signals.add(item);
        mTableItems.removeAll(mTableItems);
        for (SignalContectTableItem signal : signals)
            mTableItems.add(signal);
        mTableView.setItems(FXCollections.observableArrayList(mTableItems));
    }

    @FXML
    void initialize() {
        assert mSignalListView != null : "fx:id=\"mSignalListView\" was not injected: check your FXML file 'SignalsReceivedView.fxml'.";
        assert mUtilPane != null : "fx:id=\"mUtilPane\" was not injected: check your FXML file 'SignalsReceivedView.fxml'.";
    }

//    private static class SimpleSignalContextListCell extends ListCell<SignalContext> {
//
//        @Override
//        public void updateItem(SignalContext item, boolean empty) {
//            super.updateItem(item, empty);
//            if (item != null) {
//                setTextFill(Color.BLACK);
//                setText(item.getDescription());
//            }
//        }
//    }
    
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
