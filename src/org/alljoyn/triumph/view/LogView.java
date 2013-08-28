package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import org.alljoyn.triumph.model.TransactionLogger.Transaction;
import org.alljoyn.triumph.util.loaders.ViewLoader;

public class LogView extends BorderPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<Transaction> mList;

    @FXML
    private HBox mToolPane;
    
    private final List<OnClickListener> mListeners;

    public LogView(ObservableList<Transaction> list) {
        ViewLoader.loadView("LogView.fxml", this);
        
        mListeners = new ArrayList<LogView.OnClickListener>();
        
        // Get the list of all possible transactions
        mList.setItems(list);
        setClickListener();
        
        mList.prefWidthProperty().bind(widthProperty());
        mList.maxWidthProperty().bind(maxWidthProperty());
        mList.maxHeightProperty().bind(heightProperty());
        
        mToolPane.prefWidthProperty().bind(widthProperty());
        mToolPane.maxWidthProperty().bind(maxWidthProperty());
    }
    
    @FXML
    void initialize() {
        assert mList != null : "fx:id=\"mList\" was not injected: check your FXML file 'LogView.fxml'.";
        assert mToolPane != null : "fx:id=\"mToolPane\" was not injected: check your FXML file 'LogView.fxml'.";
    }
    
    private void setClickListener() {
        mList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Transaction>() {

            @Override
            public void changed(ObservableValue<? extends Transaction> arg0,
                    Transaction old_val, Transaction new_val) {
                if (new_val == null) return;
                if (new_val.equals(old_val)) return;
                for (OnClickListener listener: mListeners) 
                    listener.onTransactionClicked(new_val);
            }
        });
    }
    
    public void addListener(OnClickListener listener) {
        if (listener == null) return;
        mListeners.add(listener);
    }
    
    public void removeListener(OnClickListener listener) {
        mListeners.remove(listener);
    }

    public interface OnClickListener {
        
        public void onTransactionClicked(Transaction transaction);
        
    }
    
}
