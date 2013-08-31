package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

import org.alljoyn.triumph.util.EndPointFilter;
import org.alljoyn.triumph.util.EndPointFilterStorage;
import org.alljoyn.triumph.util.EndPointFilterStorage.SaveListener;
import org.alljoyn.triumph.util.loaders.ViewLoader;

/**
 * A view that manages 
 *  
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class FilterView extends VBox implements SaveListener {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label mError;

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

    private final ObservableList<EndPointFilter> mAvailableList;
    
    private final List<FilterViewListener> mListeners;

    public FilterView() {
        ViewLoader.loadView("FilterView.fxml", this);
        mListeners = new ArrayList<FilterView.FilterViewListener>();
        
        // Set the combo box with the current list
        mAvailableList = FXCollections.observableArrayList(EndPointFilterStorage.getInstance().getFilters());
        mFilterComboBox.setItems(mAvailableList);
        EndPointFilterStorage.getInstance().addListener(this);
        mFilterComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EndPointFilter>() {
            @Override
            public void changed(ObservableValue<? extends EndPointFilter> arg0,
                    EndPointFilter old_val, EndPointFilter new_val) {
                if (new_val == null) return; // Weird case
                switchFilter(new_val);
            }
        });

        // Create the general filter
        switchFilter(new EndPointFilter("", "", null));

        // Set the errore
        mError.managedProperty().bind(mError.visibleProperty());
        hideError();


    }

    @FXML
    void onFilter(ActionEvent event) {
        for (FilterViewListener listener: mListeners) {
            listener.onFilterChanged(mCurFilter);
        }
    }

    @FXML
    void onSave(ActionEvent event) {
        hideError();
        
        String name = mSaveByNameInput.getText();
        if (name.isEmpty()) {
            showError("Can't have empty name");
            return;
        }
        
        mCurFilter.setSaveByName(name);
        EndPointFilterStorage.getInstance().saveFilter(mCurFilter);
    }

    @FXML
    void onSetPort(ActionEvent event) {
        hideError();
        try {
            String portStr = mPortInput.getText().trim();
            mPortInput.setText(portStr);
            if (portStr.isEmpty())
                mCurFilter.setPort(null);
            else
                mCurFilter.setPort(Short.valueOf(portStr));
        } catch (NumberFormatException e) {
            showError("Invalid Port");
        }
    }

    @FXML
    void onSaveByNameSet(ActionEvent event) {
        String name = mSaveByNameInput.getText();
        if (name.isEmpty()) {
            showError("Can't have empty name");
        } else {
            hideError();
        }
    }

    @FXML
    void onSetPrefix(ActionEvent event) {
        String val = mPrefixInput.getText().trim();
        mPrefixInput.setText(val);
        mCurFilter.setPrefix(val);
    }

    @FXML
    void onSetSuffix(ActionEvent event) {
        String val = mSuffixInput.getText().trim();
        mSuffixInput.setText(val);
        mCurFilter.setPrefix(val);
    }

    private void hideError() {
        mError.setVisible(false);
    }

    private void showError(String message) {
        mError.setText(message);
        mError.setVisible(true);
    }

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
        assert mError != null : "fx:id=\"mError\" was not injected: check your FXML file 'FilterView.fxml'.";
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
        if (list == null || mListeners.contains(list)) return;
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
