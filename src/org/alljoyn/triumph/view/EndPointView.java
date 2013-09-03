package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import org.alljoyn.triumph.model.components.AJObject;
import org.alljoyn.triumph.model.components.AllJoynComponent.TYPE;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.Interface;
import org.alljoyn.triumph.model.components.InterfaceComponent;
import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.Signal;
import org.alljoyn.triumph.util.ComponentFilter;
import org.alljoyn.triumph.util.ListManager;
import org.alljoyn.triumph.util.ViewCache;
import org.alljoyn.triumph.util.loaders.ViewLoader;
import org.alljoyn.triumph.view.ComponentFilterView.FilterListener;
import org.alljoyn.triumph.view.propview.PropertyView;

public class EndPointView extends BorderPane implements FilterListener {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ScrollPane mContentPane;

    @FXML
    private Pane mFilterPane;

    @FXML
    private VBox mLeftPane;
    
    @FXML
    private VBox mListContainer;

    /**
     * The EndPoint to build the view around of.
     */
    private final EndPoint mEndPoint;
    
    /**
     * List of methods to present
     */
    private final ListManager<InterfaceComponent> mMethods, mProperties, mSignals;
    
    /**
     * Method list view.
     */
    @FXML
    private ListView<InterfaceComponent> mMethodsListView, mPropertiesListView, mSignalsListView;
    
    /**
     * Current reference to the current filter.
     */
    private ComponentFilter mCurrFilter;
    
    /**
     * 
     */
    private final ViewCache<InterfaceComponent, Node> viewCache;
    
    /**
     * EndPointView constructor.
     * 
     * @param ep EndPoint to build view around.
     */
    public EndPointView(EndPoint ep) {
        ViewLoader.loadView(this);
        mEndPoint = ep;
        
        // Create a cache for all the views that is presented.
        viewCache = new ViewCache<InterfaceComponent, Node>();
        
        // Compose the filter view and add it to the view component.
        ComponentFilterView filterView = new ComponentFilterView(mEndPoint);
        filterView.addListener(this);
        mCurrFilter = filterView.getCurrentFilter();
        // TODO Apply the filter to all the current list of items
        
        // Bind the containing pane to the pane that is suppose to hold the filter.
        mFilterPane.prefWidthProperty().bind(filterView.prefWidthProperty());
        mFilterPane.prefHeightProperty().bind(filterView.prefHeightProperty());
        mFilterPane.getChildren().setAll(filterView);
        
        // Make sure when the list are set to not visible.
        mMethodsListView.managedProperty().bind(mMethodsListView.visibleProperty());
        mSignalsListView.managedProperty().bind(mSignalsListView.visibleProperty());
        mPropertiesListView.managedProperty().bind(mPropertiesListView.visibleProperty());
    
        // Build the lists of interface components.
        mMethods = new ListManager<InterfaceComponent>(getMethods());
        mSignals = new ListManager<InterfaceComponent>(getSignals());
        mProperties = new ListManager<InterfaceComponent>(getProplist());
        
        // set the items of the list of interface properties
        mMethodsListView.setItems(mMethods.getUnderlyingList());
        mSignalsListView.setItems(mSignals.getUnderlyingList());
        mPropertiesListView.setItems(mProperties.getUnderlyingList());
        
        // Add the listener for this view.
        ComponentSelectionListener selectListener = new ComponentSelectionListener();
        mMethodsListView.getSelectionModel().selectedItemProperty().addListener(selectListener);
        mSignalsListView.getSelectionModel().selectedItemProperty().addListener(selectListener);
        mPropertiesListView.getSelectionModel().selectedItemProperty().addListener(selectListener);
        
        // Tell the 
        ComponentListCellCallback cellCallBack = new ComponentListCellCallback();
        mMethodsListView.setCellFactory(cellCallBack);
        mSignalsListView.setCellFactory(cellCallBack);
        mPropertiesListView.setCellFactory(cellCallBack);
        
        // Make sure the scroll pane is the same preferred with as the filter pane.
        mContentPane.prefWidthProperty().bind(mFilterPane.widthProperty());
        
        updateWithCurrentFilter();
    }
  
    
    /**
     * Sets the main view to the current presentation.
     * 
     * @param node Node to show for the view.
     */
    public void setMainView(Node node) {
        if (node == null) return;
        setCenter(node);
    }

    @FXML
    void initialize() {
        assert mContentPane != null : "fx:id=\"mContentPane\" was not injected: check your FXML file 'EndPointView.fxml'.";
        assert mFilterPane != null : "fx:id=\"mFilterPane\" was not injected: check your FXML file 'EndPointView.fxml'.";
        assert mLeftPane != null : "fx:id=\"mLeftPane\" was not injected: check your FXML file 'EndPointView.fxml'.";
        assert mMethodsListView != null : "fx:id=\"mMethodsListView\" was not injected: check your FXML file 'EndPointView.fxml'.";
        assert mPropertiesListView != null : "fx:id=\"mPropertiesListView\" was not injected: check your FXML file 'EndPointView.fxml'.";
        assert mSignalsListView != null : "fx:id=\"mSignalsListView\" was not injected: check your FXML file 'EndPointView.fxml'.";
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!o.getClass().equals(getClass())) return false;
        return mEndPoint.equals(((EndPointView) o).mEndPoint);
    }
    
    @Override
    public int hashCode() {
        return mEndPoint.hashCode() * 7;
    }

    /**
     * Update the state of the current filter.
     */
    private void updateWithCurrentFilter() {
        // If the current filter is 
        mMethodsListView.setVisible(mCurrFilter.showMethods());
        mSignalsListView.setVisible(mCurrFilter.showSignals());
        mPropertiesListView.setVisible(mCurrFilter.showProperties());
        
        // Update the filter so that the components match.
        mMethods.addFilter(mCurrFilter);
        mSignals.addFilter(mCurrFilter);
        mProperties.addFilter(mCurrFilter);
    }
    
    @Override
    public void onFilter(ComponentFilter filter) {
        if (filter == null) return;
        mMethods.removeFilter(mCurrFilter);
        mSignals.removeFilter(mCurrFilter);
        mProperties.removeFilter(mCurrFilter);
        
        // Re assign the new filter.
        mCurrFilter = filter;
        
        // Update the current filter with the current state of the view.
        updateWithCurrentFilter();
    }
    
    /**
     * List cell that describes how to draw the cell.
     * @author mhotan
     */
    private static class ComponentListCellCallback implements Callback<ListView<InterfaceComponent>, ListCell<InterfaceComponent>> {

        @Override
        public ListCell<InterfaceComponent> call(
                ListView<InterfaceComponent> arg0) {
            return new ListCell<InterfaceComponent>() {
                @Override
                protected void updateItem(InterfaceComponent item, boolean empty) {
                    setTextFill(Color.BLACK);
                    setText(item.getString());
                }
            };
        }
        
    }
    
    /**
     * Listener for interface components being selected.
     * @author mhotan
     */
    private class ComponentSelectionListener implements ChangeListener<InterfaceComponent> {

        @Override
        public void changed(ObservableValue<? extends InterfaceComponent> arg0,
                InterfaceComponent oldVal, InterfaceComponent newVal) {
            if (newVal == null) return;
            if (newVal.equals(oldVal)) return;
            Node view = viewCache.getViewForElement(newVal);
            if (view == null) {
                if (newVal.getType() == TYPE.METHOD) {
                    view = new MethodView((Method) newVal);
                } else if (newVal.getType() == TYPE.SIGNAL) {
                    view = new SignalView((Signal) newVal);
                } else if (newVal.getType() == TYPE.PROPERTY) {
                    view = new PropertyView((Property) newVal);
                } else {
                    throw new RuntimeException("Illegal type of interface component being selected " + newVal.getType());
                }
                
                // update the cache
                viewCache.addView(newVal, view);
            }
            setMainView(view);
        }
        
    }
    
    /**
     * @return List of methods from the endpoints objects
     */
    private ObservableList<InterfaceComponent> getMethods() {
        ObservableList<InterfaceComponent> methods = FXCollections.observableArrayList();
        List<AJObject> objects = new ArrayList<AJObject>(mEndPoint.getObjects());
        for (AJObject object: objects) {
            List<Interface> ifaces = new ArrayList<Interface>(object.getInterfaces());
            for (Interface iface: ifaces) {
                methods.addAll(iface.getMethods());
            }
        }
        return methods;
    }
    
    /**
     * @return List of signal from all the endpoints objects.
     */
    private ObservableList<InterfaceComponent> getSignals() {
        ObservableList<InterfaceComponent> signals = FXCollections.observableArrayList();
        List<AJObject> objects = new ArrayList<AJObject>(mEndPoint.getObjects());
        for (AJObject object: objects) {
            List<Interface> ifaces = new ArrayList<Interface>(object.getInterfaces());
            for (Interface iface: ifaces) {
                signals.addAll(iface.getSignals());
            }
        }
        return signals;
    }
    
    /**
     * @return List of properties from all the EndPoint's objects.
     */
    private ObservableList<InterfaceComponent> getProplist() {
        ObservableList<InterfaceComponent> props = FXCollections.observableArrayList();
        List<AJObject> objects = new ArrayList<AJObject>(mEndPoint.getObjects());
        for (AJObject object: objects) {
            List<Interface> ifaces = new ArrayList<Interface>(object.getInterfaces());
            for (Interface iface: ifaces) {
                props.addAll(iface.getProperties());
            }
        }
        return props;
    }
}
