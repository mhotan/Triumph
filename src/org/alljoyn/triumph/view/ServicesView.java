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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import org.alljoyn.triumph.controller.EndPointListener;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.EndPoint.SERVICE_TYPE;
import org.alljoyn.triumph.util.EndPointFilter;
import org.alljoyn.triumph.util.loaders.ViewLoader;
import org.alljoyn.triumph.view.EndPointFilterView.FilterViewListener;
import org.alljoyn.triumph.view.EndPointTable.EndPointRow;

/**
 * Specific view that presents a view of all the presen
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class ServicesView extends HBox implements FilterViewListener, ChangeListener<EndPointRow> {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ScrollPane mDistributedPane;

    @FXML
    private Tab mDistributedTab;

    @FXML
    private Pane mFilterContainer;

    @FXML
    private ScrollPane mLocalPane;

    @FXML
    private Tab mLocalTab;

    @FXML
    private TabPane mTabPane;

    // Create a table for both local and distributed endpoints
    private final EndPointTable mLocalTable, mDistributedTable;
    // Simple collection that holds our tables for simplifying 
    // reproducing actions.
    private final Collection<EndPointTable> mEndPointTables;

    // create a view to produce the first filter
    private final EndPointFilterView mFilterView;

    /**
     * A reference to the current filter
     * Should never be null;
     */
    private EndPointFilter mCurrFilter;
    
    /**
     * Listeners for responding to selection.
     */
    private final Collection<EndPointListener> mListeners;

    /**
     * Create a view that manages the presentation of all the EndPoints.
     */
    public ServicesView() {
        ViewLoader.loadView(getClass().getSimpleName() + ".fxml", this);

        mEndPointTables = new HashSet<EndPointTable>();

        // Create the filter view.
        mFilterView = new EndPointFilterView();
        mFilterView.addListener(this);
        mCurrFilter = mFilterView.getCurrentFilter();
        mFilterContainer.getChildren().add(mFilterView);

        // Make sure that this view is at least as big 
        minHeightProperty().bind(mFilterView.prefHeightProperty());

        mLocalTable = new EndPointTable(SERVICE_TYPE.LOCAL);
        mDistributedTable = new EndPointTable(SERVICE_TYPE.REMOTE);
        
        // Add the listener to 
        mLocalTable.getSelectionModel().selectedItemProperty().addListener(this);
        mDistributedTable.getSelectionModel().selectedItemProperty().addListener(this);
        
        // Add more tables here
        mEndPointTables.add(mLocalTable);
        mEndPointTables.add(mDistributedTable);
        // add to tracking list.

        // Add the tables to the view.
        mDistributedPane.setContent(mDistributedTable);
        mLocalPane.setContent(mLocalTable);

    /*    getChildren().remove(mTabPane);
        getChildren().add(mDistributedTable);
       */ 
        // Bind the layout of the tables to the scroll view.
        mLocalTable.prefWidthProperty().bind(mLocalPane.prefViewportWidthProperty());
        mDistributedTable.prefWidthProperty().bind(mDistributedPane.prefViewportWidthProperty());
        
        mListeners = new HashSet<EndPointListener>();
    }

    @FXML
    void initialize() {
        assert mDistributedPane != null : "fx:id=\"mDistributedPane\" was not injected: check your FXML file 'ServicesView.fxml'.";
        assert mDistributedTab != null : "fx:id=\"mDistributedTab\" was not injected: check your FXML file 'ServicesView.fxml'.";
        assert mFilterContainer != null : "fx:id=\"mFilterContainer\" was not injected: check your FXML file 'ServicesView.fxml'.";
        assert mLocalPane != null : "fx:id=\"mLocalPane\" was not injected: check your FXML file 'ServicesView.fxml'.";
        assert mLocalTab != null : "fx:id=\"mLocalTab\" was not injected: check your FXML file 'ServicesView.fxml'.";
        assert mTabPane != null : "fx:id=\"mTabPane\" was not injected: check your FXML file 'ServicesView.fxml'.";
 }

    @Override
    public void onFilterChanged(EndPointFilter newVal) {
        EndPointFilter oldFilter = mCurrFilter;
        for (EndPointTable table: mEndPointTables) {
            table.removeFilter(oldFilter);
            table.addFilter(newVal);
        }
        mCurrFilter = newVal;
    }

    /**
     * Update the state of the endpoints.
     * 
     * @param distributedEps Current state of endpoints
     * @param localEps 
     */
    public void updateState(List<EndPoint> distributedEps, List<EndPoint> localEps) {
        // TODO Auto-generated method stub
        mDistributedTable.update(distributedEps);
        mLocalTable.update(localEps);
    }
    
    public void addListener(EndPointListener list) {
        mListeners.add(list);
    }
    
    public void removeListener(EndPointListener list) {
        mListeners.remove(list);
    }

    @Override
    public void changed(ObservableValue<? extends EndPointRow> arg0,
            EndPointRow oldVal, EndPointRow newVal) {
       if (newVal == null) return;
       if (newVal.equals(oldVal)) return;
       
       // Notify all the listeners on the selections of the new value.
       for (EndPointListener list: mListeners) {
           list.onEndPointSelected(newVal.getEndPoint());
       }
    }


}
