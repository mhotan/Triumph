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

import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.alljoyn.triumph.model.TriumphModel;
import org.alljoyn.triumph.model.components.AllJoynService;
import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.Signal;
import org.alljoyn.triumph.view.MainView.MainViewInterface;

/**
 * A class the manages the interaction with the model and 
 * the interaction between the seperate view.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class MainController implements TriumphViewable, MainViewInterface {

	/**
	 * Logger for the stats.
	 */
	private static final Logger LOGGER = Logger.getLogger(MainController.class.getSimpleName());

	/**
	 * The primary stage to show the application views.
	 */
	private final Stage mPrimaryStage;

	/**
	 * The current busview the user is looking at
	 */
	private BusView mCurrentView;
	private final BusView mDistributedBusView, mLocalBusView;
	
	/**
	 * Easy reference to the underlying model instance.
	 */
	private final TriumphModel mModel;

	/**
	 * 
	 * @param primaryStage
	 */
	public MainController(Stage primaryStage) {
		mModel = TriumphModel.getInstance();
		mPrimaryStage = primaryStage;

		// Expand the window to maximum screen.
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();
		primaryStage.setX(bounds.getMinX());
		primaryStage.setY(bounds.getMinY());
		primaryStage.setWidth(bounds.getWidth());
		primaryStage.setHeight(bounds.getHeight());

		// Create the two base view
		mDistributedBusView = new BusView();
		mLocalBusView = new BusView();
		
		// Create the main view and set up the JavaFX Scene.
		MainView mMain = new MainView(this, mDistributedBusView, mLocalBusView);
		Scene scene = new Scene(mMain);
		mPrimaryStage.setScene(scene);
		mPrimaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				mModel.destroy();
				Platform.exit();
			}
		});
		
		// Establish a quick reference to the current BusView.
		mCurrentView = mMain.getCurrentBusView();
		mPrimaryStage.show();
	}

	//////////////////////////////////////////////////////////////////
	////  Method needed for TriumphViewable.
	//////////////////////////////////////////////////////////////////

	@Override
	public void update() {
		// TODO Place Logic for what to show in the partiuclar Views.
		List<AllJoynService> distributed = mModel.getDistributedServices();
		mDistributedBusView.updateState(distributed);	

		List<AllJoynService> locals = mModel.getLocalServices();
		mLocalBusView.updateState(locals);
	}

	@Override
	public void showError(String message) {
		LOGGER.severe("Triumph Error: " + message);
	}

	@Override
	public void showMethod(Method method) {
		mCurrentView.showMethod(method);
	}

	@Override
	public void showSignal(Signal signal) {
		mCurrentView.showSignal(signal);
	}

	@Override
	public void showProperty(Property property) {
		mCurrentView.showProperty(property);
	}

	@Override
	public void onAbout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRefresh() {
		// Pull the latest services.
		update();
	}

	@Override
	public void onBusViewChanged(BusView newView) {
		mCurrentView = newView;
	}
}
