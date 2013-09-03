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
import java.util.ResourceBundle;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
     * 
     * @param ep
     */
    public ComponentFilterView(EndPoint ep) {
        ViewLoader.loadView("ComponentFilter.fxml", this);
        mEndPoint = ep;
        
        // Create a set because we only need to notiy a filter just once.
        mListeners = new HashSet<FilterListener>();
        mFilter = new ComponentFilter();
        
//        mObjectCombo.setItems(arg0);
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
    }

    @FXML
    void onCheckSelectAll(ActionEvent event) {
        boolean showAll = mSelectAllBox.isSelected();
        mFilter.setShowAll(showAll);
    }

    @FXML
    void onCheckedMethods(ActionEvent event) {
        boolean showMethods = mMethodBox.isSelected();
        if (!showMethods) {
            mSelectAllBox.setSelected(false);
        }
        mFilter.setShowMethods(showMethods);
    }

    @FXML
    void onCheckedSignals(ActionEvent event) {
        boolean showSigs = mSignalBox.isSelected();
        if (!showSigs)
            mSelectAllBox.setSelected(false);
        mFilter.setShowSignals(showSigs);
    }

    @FXML
    void onFilter(ActionEvent event) {
        Set<FilterListener> listeners = new HashSet<FilterListener>(mListeners);
        for (FilterListener list: listeners) {
            list.onFilter(mFilter);
        }
    }

    @FXML
    void onSelectInterface(ActionEvent event) {
        Interface iface = mIfaceCombo.getSelectionModel().getSelectedItem();
        mFilter.setInterface(iface);
    }

    @FXML
    void onSelectObject(ActionEvent event) {
        AJObject object = mObjectCombo.getSelectionModel().getSelectedItem();
        mFilter.setObject(object);
    }

    @FXML
    void onSetFilterName(ActionEvent event) {
        String name = mNameInput.getText();
        mFilter.setName(name);
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
