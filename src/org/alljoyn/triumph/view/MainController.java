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

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.alljoyn.triumph.model.TriumphModel;
import org.alljoyn.triumph.model.components.AllJoynService;
import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.Signal;
import org.alljoyn.triumph.util.loaders.ImageLoader;
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
    private static final Logger LOG = Logger.getLogger(MainController.class.getSimpleName());

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
     * Creates a Controller that creates and manages internal views.
     * 
     * @param primaryStage Stage to build view over.
     */
    public MainController(Stage primaryStage) {
        mModel = TriumphModel.getInstance();
        mPrimaryStage = primaryStage;

        try {
            // Attempt to set the icon of this application
            mPrimaryStage.getIcons().add(ImageLoader.loadImage("img-alljoyn-logo.png"));
        } catch (IOException e) {
            LOG.warning("Unable to load Icon, Exception caught: " + e.getMessage());
        }

        // Expand the window to maximum screen.
        /*Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();
		primaryStage.setX(bounds.getMinX());
		primaryStage.setY(bounds.getMinY());*/
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);

        // Create the two base view
        mDistributedBusView = new BusView();
        mLocalBusView = new BusView();

//        view2 = new MainView2(mPrimaryStage);

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

    MainView2 view2;

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
        LOG.severe("Triumph Error: " + message);
        mCurrentView.showError(message);
    }

    @Override
    public void showMethod(Method method) {
        mCurrentView.hideError();
        mCurrentView.showMethod(method);
    }

    @Override
    public void showSignal(Signal signal) {
        mCurrentView.hideError();
        mCurrentView.showSignal(signal);
    }

    @Override
    public void showProperty(Property property) {
        mCurrentView.hideError();
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
