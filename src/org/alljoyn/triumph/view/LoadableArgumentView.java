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
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import org.alljoyn.triumph.model.ArgumentStorage;
import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.util.loaders.ViewLoader;
import org.alljoyn.triumph.view.arguments.editable.ArgumentView;
import org.alljoyn.triumph.view.arguments.editable.EditableArgumentViewFactory;

/**
 * A view that contains an a single argument view.  This view allows user to select
 * a pre saved arguments via a combobox..
 */
public class LoadableArgumentView extends BorderPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Argument<?>> mComboBox;
    // Associated list of arguments 
    private final ObservableList<Argument<?>> mArguments;

    private ArgumentView<?> mCurrentView;
    
    @FXML
    void initialize() {
        assert mComboBox != null : "fx:id=\"mComboBox\" was not injected: check your FXML file 'LoadableArgumentView.fxml'.";
    }

    /**
     * DBUS Signature to find arguments for.
     */
    private final String mSignature;

    /**
     * Creates a argument view that allows the 
     * @param arg Argument to place inside the view
     */
    public LoadableArgumentView(Argument<?> arg) {
        ViewLoader.loadView("LoadableArgumentView.fxml", this);

        // Create the argument view.
        ArgumentView<?> argView = EditableArgumentViewFactory.produceView(arg);
        setCenter(argView);
        mCurrentView = argView;
        
        mComboBox.prefWidthProperty().bind(widthProperty());
        mComboBox.managedProperty().bind(mComboBox.visibleProperty());

        // Get the DBus argument type
        mSignature = arg.getDBusSignature();

        // Create an initial list for an argumnet
        mArguments = FXCollections.observableArrayList();
        mComboBox.setItems(mArguments);

        // This tells the ComboBox how to draw its items.
        mComboBox.setCellFactory(new Callback<ListView<Argument<?>>, ListCell<Argument<?>>>() {

            @Override
            public ListCell<Argument<?>> call(ListView<Argument<?>> arg0) {
                return new ListCell<Argument<?>>() {

                    @Override
                    protected void updateItem(Argument<?> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) return;

                        // Show the name the argument was saved by.
                        setText(item.getSaveByName());
                        setTextFill(Color.BLACK);
                    }
                };
            }
        });

        // This tells the Combo box how to react to different selection.
        mComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Argument<?>>() {

            @Override
            public void changed(ObservableValue<? extends Argument<?>> arg0,
                    Argument<?> old_val, Argument<?> new_val) {
                if (new_val == null) return;
                if (new_val.equals(old_val)) return;

                // If the user chooses a different argument load
                ArgumentView<?> argView = EditableArgumentViewFactory.produceView(new_val);
                setCenter(argView);
                mCurrentView = argView;
            }

        });

        updateList();
    }

    /**
     * Update the list for
     */
    public void updateList() {
        // Another way to clear the argument list which also 
        // triggers the invalidation of the list
        mArguments.removeAll(mArguments);

        // Add all the arguments to the list of this 
        mArguments.addAll(ArgumentStorage.getInstance().getArguments(mSignature));
    }

    /**
     * 
     * @param editable true to set this
     */
    public void setEditable(boolean editable) {
        mComboBox.setVisible(editable);
        mCurrentView.setEditable(editable);
    }
    
    /**
     * Returns the current argument view.
     * @return Current argument view.
     */
    public ArgumentView<?> getCurrentView() {
        return mCurrentView;
    }

}
