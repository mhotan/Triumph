package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.alljoyn.triumph.model.components.AJObject;
import org.alljoyn.triumph.model.components.Interface;
import org.alljoyn.triumph.util.loaders.ViewLoader;

public class ComponentFilterView extends VBox {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private HBox mCheckBoxContainer;

    @FXML
    private GridPane mGridPane;

    @FXML
    private ComboBox<Interface> mIfaceCombo;

    @FXML
    private CheckBox mIsolateInterface, mIsolateObject, mMethodBox, mPropertiesBox, mSelectAllBox, mSignalBox;

    @FXML
    private ComboBox<AJObject> mObjectCombo;

    /**
     * 
     */
    public ComponentFilterView() {
        ViewLoader.loadView("ComponentFilter.fxml", this);
        
    }
    
    @FXML
    void onIsolateInterface(ActionEvent event) {
    }

    @FXML
    void onIsolateObject(ActionEvent event) {
    }

    @FXML
    void onPropertiesSelected(ActionEvent event) {
    }

    @FXML
    void onSelectAll(ActionEvent event) {
    }

    @FXML
    void onSelectInterface(ActionEvent event) {
    }

    @FXML
    void onSelectMethod(ActionEvent event) {
    }

    @FXML
    void onSelectObject(ActionEvent event) {
    }

    @FXML
    void onSignalSelected(ActionEvent event) {
    }

    @FXML
    void initialize() {
        assert mCheckBoxContainer != null : "fx:id=\"mCheckBoxContainer\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mGridPane != null : "fx:id=\"mGridPane\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mIfaceCombo != null : "fx:id=\"mIfaceCombo\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mIsolateInterface != null : "fx:id=\"mIsolateInterface\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mIsolateObject != null : "fx:id=\"mIsolateObject\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mMethodBox != null : "fx:id=\"mMethodBox\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mObjectCombo != null : "fx:id=\"mObjectCombo\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mPropertiesBox != null : "fx:id=\"mPropertiesBox\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mSelectAllBox != null : "fx:id=\"mSelectAllBox\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
        assert mSignalBox != null : "fx:id=\"mSignalBox\" was not injected: check your FXML file 'ComponentFilter.fxml'.";
    }
}
