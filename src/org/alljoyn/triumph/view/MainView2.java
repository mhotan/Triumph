package org.alljoyn.triumph.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.model.TriumphModel;
import org.alljoyn.triumph.model.components.AllJoynInterface;
import org.alljoyn.triumph.model.components.AllJoynObject;
import org.alljoyn.triumph.model.components.AllJoynService;
import org.alljoyn.triumph.util.SessionPortStorage;
import org.alljoyn.triumph.util.loaders.ViewLoader;

import com.sun.istack.internal.logging.Logger;

public class MainView2 extends VBox {

    private static final Logger LOG = Logger.getLogger(MainView2.class);

    @FXML
    private ScrollPane mDistributedEndpointsPane, mLocalEndpointsPane;

    private final TableView<EndPoint> mDistributedTable, mLocalTable;

    private final ObservableList<EndPoint> mDistEndPts, mLocalEndPts;

    private final Map<EndPoint, AllJoynService> mMap;

    public MainView2(Stage primaryStage) {
        ViewLoader.loadView("MainView2.fxml", this);
        mDistributedTable = new TableView<EndPoint>();
        mLocalTable = new TableView<EndPoint>();

        mDistEndPts = FXCollections.observableArrayList();
        mLocalEndPts = FXCollections.observableArrayList();

        mMap = new HashMap<EndPoint, AllJoynService>();
        setUpTable(mDistributedTable, mDistEndPts);
        setUpTable(mLocalTable, mLocalEndPts);
        
        mDistributedEndpointsPane.setContent(mDistributedTable);
        mLocalEndpointsPane.setContent(mLocalTable);
    }

    private void setUpTable(final TableView<EndPoint> tv, ObservableList<EndPoint> EndPoints) {

        TableColumn<EndPoint, Boolean> connectColumn = new TableColumn<EndPoint, Boolean>("Session Established");
        connectColumn.setCellValueFactory(new PropertyValueFactory<EndPoint, Boolean>("connected"));
        connectColumn.setCellFactory(new Callback<TableColumn<EndPoint,Boolean>, TableCell<EndPoint,Boolean>>() {

            @Override
            public TableCell<EndPoint, Boolean> call(
                    TableColumn<EndPoint, Boolean> param) {

                // 
                TableCell<EndPoint, Boolean> cell = new TableCell<EndPoint, Boolean>() {

                    @Override
                    public void updateItem(Boolean item, boolean empty) {


                        if(item != null){

                            // Check if the 
                            if (item.booleanValue()) { // if connected Show that the service is connected
                                CheckBox box = new CheckBox();
                                box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                                box.setSelected(true);
                                box.setDisable(true);
                                setGraphic(box);
                            } else {
                                Button but = new Button();
                                setGraphic(but);
                                but.setOnAction(new EventHandler<ActionEvent>() {

                                    @Override
                                    public void handle(ActionEvent event) {
                                        try {
                                            // TODO Auto-generated method stub
                                            EndPoint ep = tv.getItems().get(getIndex());
                                            TriumphModel.getInstance().buildService(ep.mService);
                                            ep.updateService();
                                            ep.setConnected(true);
                                        } catch (TriumphException e) {
                                            LOG.warning("Unable to build service " + e.getMessage());

                                            // TODO Show error
                                        }
                                    }
                                });
                            }
                        }
                    }
                };
                return cell;
            }
        });

        // Register the name column
        TableColumn<EndPoint, String> nameColumn = new TableColumn<EndPoint, String>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<EndPoint, String>("endpointName"));
        nameColumn.setEditable(false);

        // Register the port number column
        TableColumn<EndPoint, String> portColumn = new TableColumn<MainView2.EndPoint, String>("Port");
        portColumn.setCellValueFactory(new PropertyValueFactory<EndPoint, String>("portNumber"));
        portColumn.setCellFactory(new Callback<TableColumn<EndPoint,String>, TableCell<EndPoint,String>>() {

            @Override
            public TableCell<EndPoint, String> call(
                    TableColumn<EndPoint, String> param) {
                return new TextFieldTableCell<EndPoint, String>();
            }
        });
        portColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<EndPoint,String>>() {

            @Override
            public void handle(CellEditEvent<EndPoint, String> event) {
                EndPoint ep = event.getRowValue();
                String portStr = event.getNewValue();
                if (portStr == null || portStr.isEmpty()) {
                    portStr = "0";
                }
                short s = 0;
                try {
                    s = Short.valueOf(portStr);
                } catch (NumberFormatException e) {}

                s = (short) Math.max(0, Math.min(Short.MAX_VALUE, s));
                String str = "" + s;
                ep.setPortNumber(str);
            }
        });

        // Machine ID Column
        TableColumn<EndPoint, String> machineIdColumn = new TableColumn<EndPoint, String>("Machine ID");
        machineIdColumn.setCellValueFactory(new PropertyValueFactory<EndPoint, String>("machineId"));
        machineIdColumn.setEditable(false);

        // Register the Current object 
        TableColumn<EndPoint, String> objectColumn = new TableColumn<MainView2.EndPoint, String>("Object Path");
        objectColumn.setCellValueFactory(new PropertyValueFactory<EndPoint, String>("object"));
        objectColumn.setCellFactory(new Callback<TableColumn<EndPoint,String>, TableCell<EndPoint,String>>() {

            @Override
            public TableCell<EndPoint, String> call(TableColumn<EndPoint, String> param) {

                return new ComboBoxTableCell<EndPoint, String>() {

                    @Override
                    public void updateItem(String objectName, boolean empty) {
                        super.updateItem(objectName, empty);

                        // Attempt to set the list of objects if they are available
                        EndPoint ep = tv.getItems().get(getIndex());
                        if (ep.getObjects().size() > 1 && getItems().isEmpty()) {
                            getItems().addAll(ep.getObjects());
                            LOG.info("Object Combo Box set for " + ep.getEndpointName());
                        }

                        if (objectName != null) {
                            setText(objectName);
                        }
                    }
                };
            }
        });
        objectColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<EndPoint,String>>() {
            
            @Override
            public void handle(CellEditEvent<EndPoint, String> event) {
                // TODO Handle the selection of the object
                LOG.info("Object " + event.getNewValue() + " for " + event.getRowValue().getEndpointName());
            }
        });
        
        
     // Register the Interface column 
        TableColumn<EndPoint, String> ifaceColumn = new TableColumn<MainView2.EndPoint, String>("Interfaces");
        ifaceColumn.setCellValueFactory(new PropertyValueFactory<EndPoint, String>("iface"));
        ifaceColumn.setCellFactory(new Callback<TableColumn<EndPoint,String>, TableCell<EndPoint,String>>() {

            @Override
            public TableCell<EndPoint, String> call(TableColumn<EndPoint, String> param) {

                return new ComboBoxTableCell<EndPoint, String>() {

                    @Override
                    public void updateItem(String objectName, boolean empty) {
                        super.updateItem(objectName, empty);

                        // Attempt to set the list of objects if they are available
                        EndPoint ep = tv.getItems().get(getIndex());
                        if (ep.getInterfaces().size() > 1 && getItems().isEmpty()) {
                            getItems().addAll(ep.getInterfaces());
                            LOG.info("Interface Combo Box set for " + ep.getEndpointName());
                        }

                        if (objectName != null) {
                            setText(objectName);
                        }
                    }
                };
            }
        });
        ifaceColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<EndPoint,String>>() {
            
            @Override
            public void handle(CellEditEvent<EndPoint, String> event) {
                // TODO Handle the selection of the interface
                LOG.info("Interface " + event.getNewValue() + " for " + event.getRowValue().getEndpointName());
            }
        });

        // Set the data to be observed
        EndPoints.add(new EndPoint(new AllJoynService("test")));
        tv.setItems(EndPoints);
        tv.getColumns().addAll(connectColumn, nameColumn, portColumn, machineIdColumn, objectColumn, ifaceColumn);

    }

    void updateLocalState(List<AllJoynService> currentServices) {
        
        Set<EndPoint> eps = new HashSet<EndPoint>();
        for (AllJoynService service: currentServices) {
            eps.add(new EndPoint(service));
        }
        
        for (EndPoint ep: eps) {
            if (mLocalEndPts.contains(ep)) continue;
            mLocalEndPts.add(ep);
        }
        
        Set<EndPoint> toRemove = new HashSet<EndPoint>();
        for (EndPoint ep: mLocalEndPts) {
            if (!eps.contains(ep)) continue;
            toRemove.add(ep);
        }
        
        for (EndPoint ep: toRemove)
            mLocalEndPts.remove(ep);
        
    }

    void updateDistributedState(List<AllJoynService> distributedServices) {

        Set<EndPoint> eps = new HashSet<EndPoint>();
        for (AllJoynService service: distributedServices) {
            eps.add(new EndPoint(service));
        }
        
        for (EndPoint ep: eps) {
            if (mDistEndPts.contains(ep)) continue;
            mDistEndPts.add(ep);
        }
        
        Set<EndPoint> toRemove = new HashSet<EndPoint>();
        for (EndPoint ep: mDistEndPts) {
            if (!eps.contains(ep)) continue;
            toRemove.add(ep);
        }
        
        for (EndPoint ep: toRemove)
            mDistEndPts.remove(ep);
        
    }

    public static class EndPoint {

        /*  private final */
        private final SimpleBooleanProperty connected;
        private final SimpleStringProperty endpointName;
        private final SimpleStringProperty portNumber;
        private final SimpleStringProperty machineId;
        private final SimpleStringProperty object; // Current selected Object
        private final SimpleStringProperty iface; // Current selected Interface
        private final ObservableList<String> objects, interfaces;

        final AllJoynService mService;

        EndPoint(AllJoynService service) {
            mService = service;
            String serviceName = service.getName();
            this.connected = new SimpleBooleanProperty(false); // Set to not connected
            this.endpointName = new SimpleStringProperty(serviceName);
            this.portNumber = new SimpleStringProperty("" + SessionPortStorage.getPort(serviceName));
            this.machineId = new SimpleStringProperty("?");
            this.object = new SimpleStringProperty("Any");
            this.iface = new SimpleStringProperty("Any");

            this.objects = FXCollections.observableArrayList();
            this.interfaces = FXCollections.observableArrayList();
            objects.add("Any");
            interfaces.add("Any");
        }

        public void updateService() {
            objects.clear();
            interfaces.clear();
            objects.add("Any");
            interfaces.add("Any");

            for (AllJoynObject object: mService.getObjects()) {
                objects.add(object.getName());
                for (AllJoynInterface iface : object.getInterfaces()) {
                    interfaces.add(object.getName() + "->" + iface.getName());
                } 
            }
        }

        public Boolean getConnected() {
            return connected.getValue();
        }

        public void setConnected(Boolean connected) {
            this.connected.set(connected);
        }

        public String getEndpointName() {
            return this.endpointName.get();
        } 

        public void setEndpointName(String name) {
            this.endpointName.set(name);
        }

        public String getPortNumber() {
            return this.portNumber.get();
        } 

        public void setPortNumber(String port) {
            this.portNumber.set(port);
            SessionPortStorage.savePort(mService.getName(), Short.valueOf(portNumber.get()));
        }

        public String getMachineId() {
            return this.machineId.get();
        } 

        public void setMachineId(String id) {
            this.machineId.set(id);
        }

        public String getObject() {
            return this.object.get();
        } 

        public void setObject(String obj) {
            this.object.set(obj);
        }

        public String getIface() {
            return this.iface.get();
        } 

        public void setIface(String iface) {
            this.iface.set(iface);
        }

        public ObservableList<String> getObjects() {
            return objects;
        }

        public ObservableList<String> getInterfaces() {
            return interfaces;
        }

        public void setObjects(List<String> oNames) {
            objects.setAll(oNames);
        }

        public void setInterfaces(List<String> ifaces) {
            interfaces.setAll(ifaces);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!o.getClass().equals(getClass())) return false;
            EndPoint ep = (EndPoint) o;
            return ep.endpointName.get().equals(this.endpointName.get());
        }

        @Override
        public int hashCode() {
            return this.endpointName.get().hashCode();
        }

    }


}
