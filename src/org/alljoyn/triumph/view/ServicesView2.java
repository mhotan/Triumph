package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.util.ListManager;
import org.alljoyn.triumph.util.SessionPortStorage;
import org.alljoyn.triumph.util.loaders.ViewLoader;

public class ServicesView2 extends BorderPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox mFilterBox;

    @FXML
    private Label mFilterLabel;

    @FXML
    private GridPane mGridPane;

    @FXML
    private TableView<EndPointRow> mTableView;

    private final ObservableList<EndPointRow> mEndpoints;
    private final ListManager<EndPointRow> mListManager;
    
    public ServicesView2() {
        ViewLoader.loadView(getClass().getSimpleName() + ".fxml", this);
        
        // Always grow this view when inside an HBox
        HBox.setHgrow(this, Priority.ALWAYS);
        
        mListManager = new ListManager<EndPointRow>();
        mEndpoints = mListManager.getUnderlyingList();
        
        // Create the port column
        TableColumn<EndPointRow,Integer> portColumn = new TableColumn<EndPointRow,Integer>("Port #");
        portColumn.setCellValueFactory(new PropertyValueFactory<EndPointRow,Integer>("port"));
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
        
        // Create the name column
        TableColumn<EndPointRow,String> nameColumn = new TableColumn<EndPointRow,String>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<EndPointRow,String>("name"));
        
        mTableView.setItems(mEndpoints);
        mTableView.setEditable(true);
        mTableView.getColumns().setAll(portColumn, nameColumn);
    }
    
    @FXML
    void initialize() {
        assert mFilterBox != null : "fx:id=\"mFilterBox\" was not injected: check your FXML file 'ServicesView2.fxml'.";
        assert mFilterLabel != null : "fx:id=\"mFilterLabel\" was not injected: check your FXML file 'ServicesView2.fxml'.";
        assert mGridPane != null : "fx:id=\"mGridPane\" was not injected: check your FXML file 'ServicesView2.fxml'.";
        assert mTableView != null : "fx:id=\"mTableView\" was not injected: check your FXML file 'ServicesView2.fxml'.";
    }

    public class EndPointRow {

        private final EndPoint mEp;

        private StringProperty name;

        private IntegerProperty port;

        public IntegerProperty portProperty() {
            if (this.port == null) {
                int port = (int)SessionPortStorage.getPort(mEp.getName());
                this.port = new SimpleIntegerProperty(port);
            }
            return this.port;
        }
        
        public EndPoint getEndPoint() {
            return mEp;
        }

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

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!o.getClass().equals(getClass())) return false;
            return mEp.equals(((EndPointRow) o).mEp);
        }
        
        @Override 
        public int hashCode() {
            return mEp.hashCode();
        }
    }
    
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
    
    private class PortCallback implements Callback<TableColumn<EndPointRow, Integer>, TableCell<EndPointRow, Integer>> {

        @Override
        public TableCell<EndPointRow, Integer> call(
                TableColumn<EndPointRow, Integer> arg0) {
            return new PortTableCell();
        }
        
    }
    
    private class PortTableCell extends TextFieldTableCell<EndPointRow, Integer> {
        
        public PortTableCell() {
            super(new IntegerStringConverter());
        }
    }

    public void updateState(List<EndPoint> endpoints) {
        List<EndPointRow> newRows = new ArrayList<ServicesView2.EndPointRow>(endpoints.size());
        for (EndPoint ep: endpoints) {
            newRows.add(new EndPointRow(ep));
        }
        mListManager.updateState(newRows);
    }

}
