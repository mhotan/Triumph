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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

import org.alljoyn.triumph.controller.EndPointListener;
import org.alljoyn.triumph.controller.TriumphController;
import org.alljoyn.triumph.model.SessionPortStorage;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.EndPoint.SERVICE_TYPE;
import org.alljoyn.triumph.util.EndPointFilter;
import org.alljoyn.triumph.util.ListManager;
import org.alljoyn.triumph.view.EndPointTable.EndPointRow;

/**
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class EndPointTable extends TableView<EndPointRow> {

    /**
     * The internal list manager that handles the adding and removing
     * of the endpoints
     */
    private final ListManager<EndPointRow> mListManager;

    /**
     * Type that defines the type of table.
     */
    private final SERVICE_TYPE mType;

    /**
     * Listeners for responding to selection.
     */
    private final Collection<EndPointListener> mListeners;
    
    private final double DEFAULT_MAX_COLUMN_WIDTH = 100;
    private final double DEFAULT_MIN_COLUMN_WIDTH = 80;
    
    /**
     * Create an empty table.
     * 
     * @param type type of table to use
     */
    public EndPointTable(SERVICE_TYPE type) {
        super();
        mType = type;
        mListManager = new ListManager<EndPointRow>();
        mListeners = new HashSet<EndPointListener>();
        init();
    }

    /**
     * Creates an table with current list of endpoints. 
     * 
     * @param type The type of table.
     * @param endpoints List to initialize to
     */
    public EndPointTable(SERVICE_TYPE type, ObservableList<EndPoint> endpoints) {
        super();
        mType = type;
        mListManager = new ListManager<EndPointRow>();
        update(endpoints);
        mListeners = new HashSet<EndPointListener>();
        init();
    }
    
    /**
     * Initial method called by constructor to set specific attributes of the table.
     */
    @SuppressWarnings("unchecked")
    private void init() {
        // Set up the table column resize policy.
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        HBox.setHgrow(this, Priority.ALWAYS);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        // Create the column for the table that is associated if it is connected.
        // Here we bind the property to the column.
        TableColumn<EndPointRow, Boolean> connectedCol = new TableColumn<EndPointRow, Boolean>("Connected");
        connectedCol.setCellValueFactory(new PropertyValueFactory<EndPointRow, Boolean>("connected"));
        connectedCol.setCellFactory(CheckBoxTableCell.forTableColumn(connectedCol));
        connectedCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<EndPointRow,Boolean>>() {
            
            @Override
            public void handle(CellEditEvent<EndPointRow, Boolean> event) {
             // Attempt to connect.
                EndPointRow row = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if (row == null) return;
                
                Boolean connect = event.getNewValue();
                if (!connect) return;
                
                
                row.setConnected(TriumphController.getInstance().buildService(row.getEndPoint()));
            }
        });
        connectedCol.setMaxWidth(DEFAULT_MAX_COLUMN_WIDTH);
        connectedCol.setMinWidth(DEFAULT_MIN_COLUMN_WIDTH);
        
        
        // Create the port column
        // Bind the value to the EndPoint port row.
        // Create a text input
        TableColumn<EndPointRow,Integer> portColumn = new TableColumn<EndPointRow,Integer>("Port #");
        portColumn.setCellValueFactory(new PropertyValueFactory<EndPointRow,Integer>("port"));
        portColumn.setMaxWidth(DEFAULT_MAX_COLUMN_WIDTH);
        portColumn.setMinWidth(DEFAULT_MIN_COLUMN_WIDTH);
        
        // If the port for this endpoint is distributed and requires a session port then 
        // Make sure the user can edit this value.
        if (mType == SERVICE_TYPE.REMOTE) {
            portColumn.setEditable(true);
            
            portColumn.setCellFactory(TextFieldTableCell.<EndPointRow,Integer>forTableColumn(new IntegerStringConverter()));
            portColumn.setEditable(true);
            portColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<EndPointRow,Integer>>() {
                
                @Override
                public void handle(CellEditEvent<EndPointRow, Integer> t) {
                    // Oracle 
                    EndPointRow row = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    row.setPort(t.getNewValue());
                }
            });
        } else {
            portColumn.setEditable(false);
        }
        
        // Create the name column
        // This is the same for both 
        TableColumn<EndPointRow,String> nameColumn = new TableColumn<EndPointRow,String>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<EndPointRow,String>("name"));
        
        setItems(mListManager.getUnderlyingList());
        setEditable(true);
        getColumns().setAll(connectedCol, portColumn, nameColumn);
    
        getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EndPointRow>() {

            @Override
            public void changed(ObservableValue<? extends EndPointRow> arg0,
                    EndPointRow oldVal, EndPointRow newVal) {
                if (newVal == null) return;
                if (newVal.equals(oldVal)) return;
                
                // If the endpoint is not connected.
                if (!newVal.getConnected()) {
                    return;
                }
                
                for (EndPointListener list: mListeners) {
                    list.onEndPointSelected(newVal.getEndPoint());
                }
            }
        });
    }
    

    
    /**
     * Update the state of the current rows.
     * 
     * @param eps Current state of known endpoints
     * @return Collection of lost endpoints.
     */
    public Collection<EndPoint> update(Collection<EndPoint> eps) {
        Collection<EndPointRow> currentRows = new ArrayList<EndPointTable.EndPointRow>();
        for (EndPoint p: eps)
            currentRows.add(new EndPointRow(p));
        
        // Find out all the Endpoints we have lost
        List<EndPointRow> oldList = new ArrayList<EndPointRow>(mListManager.getUnderlyingList());
        oldList.removeAll(currentRows);
        mListManager.updateState(currentRows);
        
        // Put the list of lost rows to the list of endpoints
        Collection<EndPoint> lostEps = new ArrayList<EndPoint>();
        for (EndPointRow row: oldList) {
            for (EndPointListener list: mListeners)
                list.onEndPointRemoved(row.getEndPoint());
        }
        return lostEps;
    }

    /**
     * Attempts to add filter.
     * 
     * @param filter Filter to add
     */
    public void addFilter(EndPointFilter filter) {
        if (filter == null) return;
        mListManager.addFilter(filter);
    }
    
    /**
     * Attempts to remove filters
     * 
     * @param filter Filter to remove from list manager
     * @return true if filter was found, false otherwise
     */
    public boolean removeFilter(EndPointFilter filter) {
        return mListManager.removeFilter(filter);
    }
    
    /**
     * Adds EndPointListener
     * 
     * @param list Listener to add
     */
    public void addListener(EndPointListener list) {
        if (list == null) return;
        mListeners.add(list);
    }
    
    /**
     * Removes EndPoint listener
     * 
     * @param list EndPoint listener to remove
     */
    public void removeListener(EndPointListener list) {
        mListeners.remove(list);
    }
    
    /**
     * Container class that encapsulates and presents the value of an endpoint.
     * 
     * @author Michael Hotan, mhotan@quicinc.com
     */
    public class EndPointRow {

        /**
         * Single contained endpoint.
         */
        private final EndPoint mEp;

        private BooleanProperty connected;
        
        private StringProperty name;

        private IntegerProperty port;

        /**
         * Gets the contained endpoint
         * @return endpoint
         */
        public EndPoint getEndPoint() {
            return mEp;
        }
        
        public BooleanProperty connectedProperty() {
            if (this.connected == null) {
                this.connected = new SimpleBooleanProperty(mEp.getServiceType() == SERVICE_TYPE.LOCAL);
            }
            return this.connected;
        }
        
        /**
         * Port number property
         * @return port property.
         */
        public IntegerProperty portProperty() {
            if (this.port == null) {
                int port = (int)SessionPortStorage.getPort(mEp.getName());
                this.port = new SimpleIntegerProperty(port);
            }
            return this.port;
        }
        
        /**
         * @return name property
         */
        public StringProperty nameProperty() {
            if (this.name == null) {
                this.name = new SimpleStringProperty(mEp.getName());
            }
            return this.name;
        }
        
        public void setConnected(boolean value) {
            connectedProperty().set(value);
        }

        public boolean getConnected() {
            return connectedProperty().get();
        }
        
        public void setName(String value) {
            nameProperty().set(value);
        }

        public String getName() {
            return nameProperty().get();
        }

        public void setPort(int value) { 
            portProperty().set(value);
            SessionPortStorage.savePort(mEp.getName(), (short)value);
        }

        public int getPort() { 
            return portProperty().get(); 
        }

        public EndPointRow(EndPoint ep) {
            mEp = ep;
        }

        /**
         * it is important to override this to make sure
         * that an endpoint row is equivalent to its container type.
         */
        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!o.getClass().equals(getClass())) return false;
            return mEp.equals(((EndPointRow) o).mEp);
        }
        
        /**
         * Must override this to equals.
         */
        @Override 
        public int hashCode() {
            return mEp.hashCode();
        }
    }

    /**
     * Basic converter that converts Integer to String and back
     * @author Michael Hotan, mhotan@quicinc.com
     */
    private class IntegerStringConverter extends StringConverter<Integer> {

        @Override
        public Integer fromString(String string) {
            return Integer.valueOf(string);
        }

        @Override
        public String toString(Integer intVal) {
            if (intVal == null) return "";
            return intVal.toString();
        }
    }
}
