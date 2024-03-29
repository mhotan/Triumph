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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import org.alljoyn.triumph.model.EndPointFilterStorage;
import org.alljoyn.triumph.model.EndPointFilterStorage.SaveListener;
import org.alljoyn.triumph.util.EndPointFilter;
import org.alljoyn.triumph.util.loaders.ViewLoader;

/**
 * A view that manages the presentation of the 
 *  
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class EndPointFilterView extends VBox implements SaveListener {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button mFilterButton;

    @FXML
    private ComboBox<EndPointFilter> mFilterComboBox;

    @FXML
    private Label mFilterLabel;

    @FXML
    private GridPane mGridPane;

    @FXML
    private TextField mPortInput;

    @FXML
    private TextField mPrefixInput;

    @FXML
    private Button mSaveButton;

    @FXML
    private TextField mSaveByNameInput;

    @FXML
    private TextField mSuffixInput;

    private EndPointFilter mCurFilter;

    private ErrorDialog mErrorDialog;

    private final ObservableList<EndPointFilter> mAvailableList;

    // Internal collection of list.
    private final Collection<FilterViewListener> mListeners;

    /**
     * Create a basic view that presents a filter for searching for endpoints.
     */
    public EndPointFilterView() {
        // Load the view associated with the name of this class.
        // NOTE: FXML must have name EndPointFilterView.fxml
        ViewLoader.loadView(this);

        // Init collection to track listeners
        // We a simple collection that maintains uniqueness.
        mListeners = new HashSet<EndPointFilterView.FilterViewListener>();

        // Initialize the combo box for 
        mAvailableList = FXCollections.observableArrayList(EndPointFilterStorage.getInstance().getFilters());
        mFilterComboBox.setItems(mAvailableList);

        // Add a listener for updates in filters.
        EndPointFilterStorage.getInstance().addListener(this);

        // Make sure everytime the user selects a new filter we update appropiately
        mFilterComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EndPointFilter>() {
            @Override
            public void changed(ObservableValue<? extends EndPointFilter> arg0,
                    EndPointFilter old_val, EndPointFilter new_val) {
                if (new_val == null) return; // Weird case
                switchFilter(new_val);
            }
        });

        // Initialize the current filter with the a basic empty filter.
        switchFilter(new EndPointFilter("", "", null));

        // Initially hide the error, making sure that when it is invisible
        // It does not effect the layout inappropriately.
        mErrorDialog = new ErrorDialog("Error", "");
        hideError();
        
        addTextChangeListeners();
    }

    /**
     * Add all the correct change listeners to the text input boxes.
     */
    private void addTextChangeListeners() {
        
    }

    /**
     * Returns the current filter
     * @return Curernt endpoint filter
     */
    public EndPointFilter getCurrentFilter() {
        return mCurFilter;
    }



    @FXML
    void onFilter(ActionEvent event) {
        if (!checkFields()) return;

        for (FilterViewListener listener: mListeners) {
            listener.onFilterChanged(mCurFilter);
        }
    }

    @FXML
    void onSave(ActionEvent event) {
        hideError();

        // Check the save by name
        String name = mSaveByNameInput.getText().trim();
        mSaveByNameInput.setText(name);
        if (name.isEmpty()) {
            showError("Invalid Name", "Can't have empty name");
            return;
        }
        if (!checkFields()) return;

        mCurFilter.setSaveByName(name);
        EndPointFilterStorage.getInstance().saveFilter(mCurFilter);
    }

    /**
     * Attempts to set the port.
     * 
     * @return Error on failure, null on success
     */
    private String setPort(String portStr) {
        if (portStr == null || portStr.isEmpty()) {
            mPortInput.setText("");
            mCurFilter.setPort(null);
            return null;
        }
        try {
            portStr = portStr.trim();
            mCurFilter.setPort(Short.valueOf(portStr));
            mPortInput.setText(portStr);
        } catch (NumberFormatException e) {
            return "Invalid short";
        }
        return null;
    }
    
    @FXML
    void onSetPort(ActionEvent event) {
        hideError();
        String error = setPort(mPortInput.getText());
        if (error != null) {
            showError("Invalid Port", error);
        }
    }

    /**
     * Sets the name to save argument by.
     * 
     * @param name Name to save name by
     * @return Error string on failure, null on succes
     */
    private String setSaveByName(String name) {
        if (name.isEmpty()) {
            return "Can't have empty name";
        }
        mCurFilter.setSaveByName(name);
        return null;
    }

    @FXML
    void onSaveByNameSet(ActionEvent event) {
        hideError(); // hide any current error

        // Check for the error
        String error = setSaveByName(mSaveByNameInput.getText());
        if (error != null) {
            showError("Invalid Name", error);
            return;
        }
    }

    /**
     * Sets the current values prefix.
     * 
     * @param prefix Prefix to Check for
     */
    private void setPrefixName(String prefix) {
        if (prefix == null) 
            prefix = "";
        String val = prefix.trim();
        mPrefixInput.setText(val);
        mCurFilter.setPrefix(val);
    }

    @FXML
    void onSetPrefix(ActionEvent event) {
        setPrefixName(mPrefixInput.getText());
    }

    /**
     * Sets the suffix to look for.
     * @param suffix
     */
    private void setSuffixName(String suffix) {
        if (suffix == null)
            suffix = "";
        String val = suffix.trim();
        mSuffixInput.setText(val);
        mCurFilter.setPrefix(val);
    }
    
    @FXML
    void onSetSuffix(ActionEvent event) {
        setSuffixName(mSuffixInput.getText());
    }

    /**
     * Hides any error message
     */
    private void hideError() {
        mErrorDialog.hide();
    }

    /**
     * presents an error message
     * @param message Message to show.
     */
    private void showError(String title, String message) {
        mErrorDialog.setText(title, message);
        mErrorDialog.show();
    }

    private boolean checkFields() {
        String prefix = mPrefixInput.getText().trim();
        mPrefixInput.setText(prefix);
        String suffix = mSuffixInput.getText().trim();
        mSuffixInput.setText(suffix);
        String port = mPortInput.getText().trim();
        try {
            mPortInput.setText(port);
            if (port.isEmpty())
                mCurFilter.setPort(null);
            else
                mCurFilter.setPort(Short.valueOf(port));
            return true;
        } catch (NumberFormatException e) {
            showError("Invalid Port", "Not a short");
            return false;
        }
    }



    /**
     * Switches the current filter to the value inputted.
     * @param filter Filter to use.
     */
    private void switchFilter(EndPointFilter filter) {
        mCurFilter = filter;
        mPrefixInput.setText(mCurFilter.getPrefix());
        mSuffixInput.setText(mCurFilter.getSuffix());
        Short port = mCurFilter.getPort();
        if (port != null)
            mPortInput.setText("" + port);
        mSaveByNameInput.setText(mCurFilter.getSaveByName());
    }

    @FXML
    void initialize() {
        //        assert mError != null : "fx:id=\"mError\" was not injected: check your FXML file 'FilterView.fxml'.";
        assert mFilterButton != null : "fx:id=\"mFilterButton\" was not injected: check your FXML file 'FilterView.fxml'.";
        assert mFilterComboBox != null : "fx:id=\"mFilterComboBox\" was not injected: check your FXML file 'FilterView.fxml'.";
        assert mFilterLabel != null : "fx:id=\"mFilterLabel\" was not injected: check your FXML file 'FilterView.fxml'.";
        assert mGridPane != null : "fx:id=\"mGridPane\" was not injected: check your FXML file 'FilterView.fxml'.";
        assert mPortInput != null : "fx:id=\"mPortInput\" was not injected: check your FXML file 'FilterView.fxml'.";
        assert mPrefixInput != null : "fx:id=\"mPrefixInput\" was not injected: check your FXML file 'FilterView.fxml'.";
        assert mSaveButton != null : "fx:id=\"mSaveButton\" was not injected: check your FXML file 'FilterView.fxml'.";
        assert mSaveByNameInput != null : "fx:id=\"mSaveByNameInput\" was not injected: check your FXML file 'FilterView.fxml'.";
        assert mSuffixInput != null : "fx:id=\"mSuffixInput\" was not injected: check your FXML file 'FilterView.fxml'.";
    }

    public void addListener(FilterViewListener list) {
        if (list == null) return;
        mListeners.add(list);
    }

    public void removeListener(FilterViewListener list) {
        mListeners.remove(list);
    }

    /**
     * Interface for notification that filter has changed.
     * @author Michael Hotan, mhotan@quicinc.com
     */
    public interface FilterViewListener {

        public void onFilterChanged(EndPointFilter newVal);

    }

    // List for 
    @Override
    public void onSaved(EndPointFilter savedArg) {
        updateSavedList();
    }

    private void updateSavedList() {
        ObservableList<EndPointFilter> curList = 
                FXCollections.observableArrayList(EndPointFilterStorage.getInstance().getFilters());
        mAvailableList.removeAll(mAvailableList);
        mAvailableList.addAll(curList);
    }

}
