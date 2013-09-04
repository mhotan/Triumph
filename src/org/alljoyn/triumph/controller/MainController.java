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

package org.alljoyn.triumph.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.alljoyn.triumph.model.TransactionLogger.MethodTransaction;
import org.alljoyn.triumph.model.TransactionLogger.PropertyTransaction;
import org.alljoyn.triumph.model.TransactionLogger.SignalTransaction;
import org.alljoyn.triumph.model.TriumphModel;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.Signal;
import org.alljoyn.triumph.model.components.SignalContext;
import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.util.ArgumentStorage;
import org.alljoyn.triumph.util.ArgumentStorage.SaveListener;
import org.alljoyn.triumph.util.loaders.ImageLoader;
import org.alljoyn.triumph.view.BusView;
import org.alljoyn.triumph.view.MainView;
import org.alljoyn.triumph.view.ServicesView;
import org.alljoyn.triumph.view.TriumphViewable;
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
    
    /**
     * Views to show the different types of buses
     */
    private final BusView mDistributedBusView, mLocalBusView;

    /**
     * Main view.
     */
    private final MainView mMain;
    
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

        primaryStage.setWidth(800);
        primaryStage.setHeight(600);

        // Create the two base view
        mDistributedBusView = new BusView();
        mLocalBusView = new BusView();

        // Create the main view and set up the JavaFX Scene.
        mMain = new MainView(this, mDistributedBusView, mLocalBusView);
        Scene scene = new Scene(mMain);
        mPrimaryStage.setScene(scene);
        mPrimaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                mModel.destroy();
                Platform.exit();
            }
        });
        
        // Add this class as a save listener.
        ArgumentStorage.getInstance().addListener(mDistributedBusView);
        ArgumentStorage.getInstance().addListener(mLocalBusView);

        // Establish a quick reference to the current BusView.
        mCurrentView = mMain.getCurrentBusView();
        mPrimaryStage.show();
    }

    //////////////////////////////////////////////////////////////////
    ////  Method needed for TriumphViewable.
    //////////////////////////////////////////////////////////////////

    @Override
    public void update() {

        // this represents a pull model.  Everytime the controller 
        // Needs to update the state of itself it pulls the data from the model.
        
        // Update the Distributed bus views
        List<EndPoint> distributed = mModel.getDistributedServices();
        mDistributedBusView.updateState(distributed);	

        // Update the list of local services.
        List<EndPoint> locals = mModel.getLocalServices();
        mLocalBusView.updateState(locals);
        
        // etc... add more services.
        ServicesView serView = mMain.getServicesView(); 
        if (serView == null) return;
        serView.updateState(distributed, locals);
    }

    @Override
    public void showError(String message) {
        if (message == null) { // hide the error
            mDistributedBusView.hideError();
            mLocalBusView.hideError();
        } else 
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
    public void showSignalReceived(SignalContext signalReceived) {
        mMain.showNewSignalReceived(signalReceived);
    }
    
    @Override
    public void onAbout() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClose() {
        mPrimaryStage.close();
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

    @Override
    public void onMethodTransactionSelected(MethodTransaction methodTrans) {
        showMethod(methodTrans.mMethod);
    }

    @Override
    public void onSignalTransactionSelected(SignalTransaction signalTrans) {
        showSignal(signalTrans.mSignal);
    }

    @Override
    public void onPropertyTransactionSelected(PropertyTransaction propTrans) {
        showProperty(propTrans.mProperty);
    }
    
}
