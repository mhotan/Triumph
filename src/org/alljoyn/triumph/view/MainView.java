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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

import org.alljoyn.triumph.util.loaders.ViewLoader;

/**
 * Constructs the Main View for showing the application
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class MainView extends BorderPane {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML private MenuItem mAboutMenuItem, mCloseMenuItem, mDeleteMenuItem;

	@FXML
    private TabPane mTabPane;
	
	@FXML
	private Tab mDistributedTab, mLocalTab;

	@FXML
	private Button mRefreshButton;

	private final MainViewInterface mViewHandler;
	
	private BusView mDistributedBusView, mLocalBusView;

	/**
	 * Prepares the main view to be used.
	 * @param Must have handler for view callbacks
	 */
	private MainView(MainViewInterface handler) {
		if (handler == null)
			throw new NullPointerException("MainViewInterface cannot be null");
		mViewHandler = handler;
		
		// load the FXML class
		ViewLoader.loadView("MainView.fxml", this);
		
		// add 
		mTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

			@Override
			public void changed(ObservableValue<? extends Tab> observable,
					Tab oldValue, Tab newValue) {
				if (oldValue.equals(newValue)) return; // No change
				
				if (newValue.equals(mDistributedTab)) {
					mViewHandler.onBusViewChanged(mDistributedBusView);
				} else if (newValue.equals(mLocalTab)) {
					mViewHandler.onBusViewChanged(mLocalBusView);
				}
			}
		});
		
		// Set the selected tab to be the Distributed Pane.
		SingleSelectionModel<Tab> selectionModel = mTabPane.getSelectionModel();
		selectionModel.select(mDistributedTab);
	}

	public MainView(MainViewInterface handler, BusView distributed, BusView local) {
		this(handler);
		setDistributedBusView(distributed);
		setLocalBusView(local);
		
		setWidth(800);
		setHeight(600);
	}

	/**
	 * Finds the current bus view that is in focus.
	 * @return the current Bus View
	 */
	public BusView getCurrentBusView() {
		SingleSelectionModel<Tab> selectionModel = mTabPane.getSelectionModel();
		Tab current = selectionModel.getSelectedItem();
		if (current.equals(mDistributedTab))
			return mDistributedBusView;
		if (current.equals(mLocalTab))
			return mLocalBusView;
		throw new RuntimeException("Unable to find view for tab '" + current.getText() + "'");
	}
	
	/**
	 * Sets the Distributed Bus view to the input
	 * 
	 * @param view BusView to set as Distributed view
	 */
	private void setDistributedBusView(BusView view) {
		mDistributedBusView = setBusView(mDistributedTab, view);
	}

	/**
	 * Sets the Distributed Bus view to the input
	 * 
	 * @param view BusView to set as Distributed view
	 */
	private void setLocalBusView(BusView view) {
		mLocalBusView = setBusView(mLocalTab, view);
	}

	/**
	 * Adds exclusive view to pane.s
	 * 
	 * @param pane Pane to put view in.
	 * @param view View to add to pane
	 */
	private BusView setBusView(Tab pane, BusView view) {
		if (view == null)
			throw new NullPointerException("MainView setting a bus view with a Null BusView");

		// Make sure the view is the only
		pane.setContent(view);
		return view;
	}

	@FXML
	void onAbout(ActionEvent event) {
		mViewHandler.onAbout();
	}

	@FXML
	void onClose(ActionEvent event) {
		mViewHandler.onClose();
	}

	@FXML
	void onDelete(ActionEvent event) {
	}

	@FXML
	void onRefresh(ActionEvent event) {
		mViewHandler.onRefresh();
	}

	@FXML
	void initialize() {
		assert mAboutMenuItem != null : "fx:id=\"mAboutMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
		assert mCloseMenuItem != null : "fx:id=\"mCloseMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
		assert mDeleteMenuItem != null : "fx:id=\"mDeleteMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
		assert mDistributedTab != null : "fx:id=\"mDistributedTab\" was not injected: check your FXML file 'MainView.fxml'.";
		assert mLocalTab != null : "fx:id=\"mLocalTab\" was not injected: check your FXML file 'MainView.fxml'.";
		assert mRefreshButton != null : "fx:id=\"mRefreshButton\" was not injected: check your FXML file 'MainView.fxml'.";
	}

	/**
	 * Interface that allows the 
	 * 
	 * @author mhotan
	 */
	public interface MainViewInterface {

		public void onAbout();
		
		public void onClose();
		
		public void onRefresh();
		
		/**
		 * 
		 * @param newView View that is focused on.
		 */
		public void onBusViewChanged(BusView newView);
	}

}
