package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

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
     * Not really a cache but just a mapping of InterfaceComponents
     * and its views.
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
        
        // Build the lists of interface components.
        mMethods = new ListManager<InterfaceComponent>(getMethods());
        mSignals = new ListManager<InterfaceComponent>(getSignals());
        mProperties = new ListManager<InterfaceComponent>(getProplist());
        
        buildListView(mMethods, mMethodsListView);
        buildListView(mSignals, mSignalsListView);
        buildListView(mProperties, mPropertiesListView);
        
        // Make sure the scroll pane is the same preferred with as the filter pane.
        mContentPane.prefWidthProperty().bind(mFilterPane.widthProperty());
        
        updateWithCurrentFilter();
    }
    
    private void buildListView(ListManager<InterfaceComponent> listManager,
            ListView<InterfaceComponent> listView) {
        listView.managedProperty().bind(listView.visibleProperty());
        listView.setItems(listManager.getUnderlyingList());
        listView.setOnMouseClicked(new ListSelectionListener(listView));
        listView.setEditable(true);
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
        // Update the filter so that the components match.
        mMethods.addFilter(mCurrFilter);
        mSignals.addFilter(mCurrFilter);
        mProperties.addFilter(mCurrFilter);
        
        // If the current filter is 
        mMethodsListView.setVisible(mCurrFilter.showMethods() && !mMethods.getUnderlyingList().isEmpty());
        mSignalsListView.setVisible(mCurrFilter.showSignals() && !mSignals.getUnderlyingList().isEmpty());
        mPropertiesListView.setVisible(mCurrFilter.showProperties() && !mProperties.getUnderlyingList().isEmpty());
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
     * Attempts to show view for component.
     * 
     * @param component Component to show
     */
    public void showViewForComponent(InterfaceComponent component) {
        Node view = viewCache.getViewForElement(component);
        if (view == null) {
            if (component.getType() == TYPE.METHOD) {
                view = new MethodView((Method) component);
            } else if (component.getType() == TYPE.SIGNAL) {
                view = new SignalView((Signal) component);
            } else if (component.getType() == TYPE.PROPERTY) {
                view = new PropertyView((Property) component);
            } else {
                throw new RuntimeException("Illegal type of interface component being selected " + component.getType());
            }
            
            // update the cache
            viewCache.addView(component, view);
        }
        setMainView(view);
    }
    
    /**
     * Listener for interface components being selected.
     * @author mhotan
     */
    private class ListSelectionListener implements EventHandler<MouseEvent> {

        private final ListView<InterfaceComponent> mView;
        
        public ListSelectionListener(ListView<InterfaceComponent> view) {
            mView = view;
        }

        @Override
        public void handle(MouseEvent event) {
            InterfaceComponent selected = mView.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            showViewForComponent(selected);
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
