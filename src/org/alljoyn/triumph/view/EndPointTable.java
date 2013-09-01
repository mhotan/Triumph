package org.alljoyn.triumph.view;

import java.util.ArrayList;
import java.util.Collection;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.EndPoint.SERVICE_TYPE;
import org.alljoyn.triumph.util.EndPointFilter;
import org.alljoyn.triumph.util.ListManager;
import org.alljoyn.triumph.util.SessionPortStorage;
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
     * Create an empty table.
     * 
     * @param type type of table to use
     */
    public EndPointTable(SERVICE_TYPE type) {
        super();
        mType = type;
        mListManager = new ListManager<EndPointRow>();
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
        init();
    }
    
    /**
     * Initial method called by constructor to set specific attributes of the table.
     */
    private void init() {
        // Set up the table column resize policy.
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        HBox.setHgrow(this, Priority.ALWAYS);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        // Create the port column
        TableColumn<EndPointRow,Integer> portColumn = new TableColumn<EndPointRow,Integer>("Port #");
        portColumn.setCellValueFactory(new PropertyValueFactory<EndPointRow,Integer>("port"));
        
        // If the port for this endpoint is distributed and requires a session port then 
        // Make sure the user can edit this value.
        if (mType == SERVICE_TYPE.REMOTE) {
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
        }
        
        // Create the name column
        // This is the same for both 
        TableColumn<EndPointRow,String> nameColumn = new TableColumn<EndPointRow,String>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<EndPointRow,String>("name"));
        
        setItems(mListManager.getUnderlyingList());
        setEditable(true);
        getColumns().setAll(portColumn, nameColumn);
    }
    
    public void addFilter(EndPointFilter filter) {
        if (filter == null) return;
        mListManager.addFilter(filter);
    }
    
    public boolean removeFilter(EndPointFilter filter) {
        return mListManager.removeFilter(filter);
    }
    
    /**
     * Update the state of the current rows
     * @param eps endpoints
     */
    public void update(Collection<EndPoint> eps) {
        Collection<EndPointRow> currentRows = new ArrayList<EndPointTable.EndPointRow>();
        for (EndPoint p: eps)
            currentRows.add(new EndPointRow(p));
        mListManager.updateState(currentRows);
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
            return intVal.toString();
        }
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

        private StringProperty name;

        private IntegerProperty port;

        /**
         * Gets the contained endpoint
         * @return endpoint
         */
        public EndPoint getEndPoint() {
            return mEp;
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
}
