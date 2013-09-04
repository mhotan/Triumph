package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;

import org.alljoyn.triumph.util.loaders.ViewLoader;

/**
 * Tabbed Support View.
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class TabbedSupportView extends HBox {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ScrollPane mLogPane;

    @FXML
    private ScrollPane mSignalReceivedPane;

    @FXML
    private TabPane mTabPane;

    public TabbedSupportView() {
        ViewLoader.loadView(this);
        mTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    }
    
    public void setLogView(LogView logView) {
        mLogPane.setContent(logView);
    }
    
    public void setSignalReceivedView(SignalReceivedView view) {
        mSignalReceivedPane.setContent(view);
    }
    
    @FXML
    void initialize() {
        assert mLogPane != null : "fx:id=\"mLogPane\" was not injected: check your FXML file 'TabbedSupportView.fxml'.";
        assert mSignalReceivedPane != null : "fx:id=\"mSignalReceivedPane\" was not injected: check your FXML file 'TabbedSupportView.fxml'.";
        assert mTabPane != null : "fx:id=\"mTabPane\" was not injected: check your FXML file 'TabbedSupportView.fxml'.";
    }
}
