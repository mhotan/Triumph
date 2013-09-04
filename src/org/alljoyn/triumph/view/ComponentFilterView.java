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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import org.alljoyn.triumph.model.components.AJObject;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.Interface;
import org.alljoyn.triumph.util.ComponentFilter;
import org.alljoyn.triumph.util.loaders.ViewLoader;

public class ComponentFilterView extends VBox {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private HBox mCheckBoxContainer;

    @FXML
    private Button mFilterButton;

    @FXML
    private GridPane mGridPane;

    @FXML
    private ComboBox<Interface> mIfaceCombo;

    @FXML
    private CheckBox mMethodBox, mPropertiesBox, mSelectAllBox, mSignalBox;

    @FXML
    private TextField mNameInput;

    @FXML
    private ComboBox<AJObject> mObjectCombo;

    @FXML
    private Label mTitle;

    /**
     * Listeners to listen for filter change
     */
    private final Collection<FilterListener> mListeners;

    /**
     * Reference to the internal filter
     */
    private final ComponentFilter mFilter;

    /**
     * The Endpoint to build view around.
     */
    private final EndPoint mEndPoint;

    /**
     * Component filter that filters all the objects and
     * interfaces.
     * 
     * @param ep Endpoint that supplies objects and interfaces.
     */
    public ComponentFilterView(EndPoint ep) {
        ViewLoader.loadView("ComponentFilter.fxml", this);
        mEndPoint = ep;

        // Create a set because we only need to notiy a filter just once.
        mListeners = new HashSet<FilterListener>();
        mFilter = new ComponentFilter();

        // Hide the filter button because we filter everytime we change a value.
        mFilterButton.managedProperty().bind(mFilterButton.visibleProperty());
        mFilterButton.setVisible(false);
        
        mNameInput.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0,
                    String oldVal, String newVal) {
                if (newVal == null)
                    newVal = "";
                if (newVal.equals(oldVal)) return;
                setPrefixName(newVal);
            }
        });
        syncView();
    }

    /**
     * Synchronizes the view to this filter.
     */
    private void syncView() {
        boolean methods = mFilter.showMethods();
        boolean props = mFilter.showProperties();
        boolean signals = mFilter.showSignals();

        mSelectAllBox.setSelected(methods && props && signals);
        mSignalBox.setSelected(signals);
        mPropertiesBox.setSelected(props);
        mMethodBox.setSelected(methods);
        
        List<AJObject> objects = mEndPoint.getObjects();
        List<Interface> ifaces = new ArrayList<Interface>();
        for (AJObject object: objects) {
            ifaces.addAll(object.getInterfaces());
        }
        
        // Insert the null placer.
        objects.add(0, null);
        ifaces.add(0, null);
        
        mIfaceCombo.setItems(FXCollections.observableArrayList(ifaces));
        mObjectCombo.setItems(FXCollections.observableArrayList(objects));
        
        // Update the View presentation for 
        mIfaceCombo.setCellFactory(new Callback<ListView<Interface>, ListCell<Interface>>() {

            @Override
            public ListCell<Interface> call(ListView<Interface> arg0) {
                return new ListCell<Interface>() {
                    
                    @Override
                    protected void updateItem(Interface item, boolean empty) {
                        super.updateItem(item, empty);
                        setTextFill(Color.BLACK);
                        if (item == null) {
                            setText("Any");
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        
        });
        mObjectCombo.setCellFactory(new Callback<ListView<AJObject>, ListCell<AJObject>>() {
            
            @Override
            public ListCell<AJObject> call(ListView<AJObject> arg0) {
                return new ListCell<AJObject>() {
                  
                    @Override
                    protected void updateItem(AJObject item, boolean empty) {
                        super.updateItem(item, empty);
                        setTextFill(Color.BLACK);
                        if (item == null) {
                            setText("Any");
                        } else {
                            setText(item.getName());
                        }
                    }
                    
                };
            }
        });
        
        mNameInput.setText(mFilter.getCurrentName());
    }

    /**
     * @return Returns the current filter.
     */
    public ComponentFilter getCurrentFilter() {
        return mFilter;
    }

    /**
     * When the user selects the properties check box.
     * @param event event that generated properties selected
     */
    @FXML
    void onCheckProperties(ActionEvent event) {
        boolean showProps = mPropertiesBox.isSelected();
        if (!showProps) {
            mSelectAllBox.setSelected(false);
        }
        mFilter.setShowProperties(showProps);
        notifyListeners();
    }

    @FXML
    void onCheckSelectAll(ActionEvent event) {
        boolean showAll = mSelectAllBox.isSelected();
        mFilter.setShowAll(showAll);
        if (!showAll) return;
        mMethodBox.setSelected(showAll);
        mSignalBox.setSelected(showAll);
        mPropertiesBox.setSelected(showAll);
        onCheckedMethods(event);
        onCheckedSignals(event);
        onCheckProperties(event);
        notifyListeners();
    }

    @FXML
    void onCheckedMethods(ActionEvent event) {
        boolean showMethods = mMethodBox.isSelected();
        if (!showMethods) {
            mSelectAllBox.setSelected(false);
        }
        mFilter.setShowMethods(showMethods);
        notifyListeners();
    }

    @FXML
    void onCheckedSignals(ActionEvent event) {
        boolean showSigs = mSignalBox.isSelected();
        if (!showSigs)
            mSelectAllBox.setSelected(false);
        mFilter.setShowSignals(showSigs);
        notifyListeners();
    }

    @FXML
    void onFilter(ActionEvent event) {
        notifyListeners();
    }
    
    /**
     * Notifies the listeners of the filter to use.
     */
    private void notifyListeners() {
        Set<FilterListener> listeners = new HashSet<FilterListener>(mListeners);
        for (FilterListener list: listeners) {
            list.onFilter(mFilter);
        }
    }

    @FXML
    void onSelectInterface(ActionEvent event) {
        Interface iface = mIfaceCombo.getSelectionModel().getSelectedItem();
        mFilter.setInterface(iface);
        notifyListeners();
    }

    @FXML
    void onSelectObject(ActionEvent event) {
        AJObject object = mObjectCombo.getSelectionModel().getSelectedItem();
        mFilter.setObject(object);
        notifyListeners();
    }

    @FXML
    void onSetFilterName(ActionEvent event) {
        setPrefixName(mNameInput.getText());
    }
    
    private void setPrefixName(String name) {
        mFilter.setName(name);
        notifyListeners();
    }

    @FXML
    void initialize() {
        assert mCheckBoxContainer != null : "fx:id=\"mCheckBoxContainer\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mFilterButton != null : "fx:id=\"mFilterButton\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mGridPane != null : "fx:id=\"mGridPane\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mIfaceCombo != null : "fx:id=\"mIfaceCombo\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mMethodBox != null : "fx:id=\"mMethodBox\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mNameInput != null : "fx:id=\"mNameInput\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mObjectCombo != null : "fx:id=\"mObjectCombo\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mPropertiesBox != null : "fx:id=\"mPropertiesBox\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mSelectAllBox != null : "fx:id=\"mSelectAllBox\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mSignalBox != null : "fx:id=\"mSignalBox\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mTitle != null : "fx:id=\"mTitle\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
    }

    /**
     * Adds a listener to notify filter has changed.
     * 
     * @param listener Listener to add 
     */
    public void addListener(FilterListener listener) {
        mListeners.add(listener);
    }

    /**
     * Removes listener if it exists.
     * 
     * @param listener Listener to remove.
     */
    public boolean removeListener(FilterListener listener) {
        return mListeners.remove(listener);
    }

    /**
     * Listener for when request to filter is requested.
     * 
     * @author mhotan
     */
    public interface FilterListener {

        /**
         * 
         * 
         * <br>Never returns null
         * @param filter Filter that
         */
        public void onFilter(ComponentFilter filter);

    }

}
