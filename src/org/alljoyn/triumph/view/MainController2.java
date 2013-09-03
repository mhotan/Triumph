package org.alljoyn.triumph.view;

import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.SetChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.alljoyn.triumph.controller.EndPointListener;
import org.alljoyn.triumph.model.TriumphModel;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.Signal;
import org.alljoyn.triumph.model.components.SignalContext;
import org.alljoyn.triumph.util.ViewCache;

public class MainController2 implements EndPointListener, TriumphViewable {

    /**
     * Logger for the stats.
     */
    private static final Logger LOG = Logger.getLogger(MainController2.class.getSimpleName());

    
    private final Stage mStage;
    
    /**
     * This is strictly a container for more intricate view controllers.
     * This just structures the views appropiately.
     */
    private final MainBorderView mMainView;
    
    /**
     * This presents the current state of the distributed bus.
     */
    private final ServicesView mEndPointsView;
    
    /**
     * Error dialog
     */
    private final ErrorDialog mErrorDialog;
    
    /**
     * Reference to model instance.
     */
    private final TriumphModel mModel;
    
    private final ViewCache<EndPoint, Node> mSingleEndPointViews;
    
    /**
     * Creates a Controller that creates and manages internal views.
     * 
     * @param primaryStage Stage to build view over.
     */
    public MainController2(Stage primaryStage) {
        mStage = primaryStage;
        mMainView = new MainBorderView();
        mEndPointsView = new ServicesView();
        mMainView.setTopPane(mEndPointsView);
        
        mErrorDialog = new ErrorDialog("Error", "Unknown error");
        mModel = TriumphModel.getInstance();
        mEndPointsView.addListener(this);
        
        // Compose the structure of this view
        Scene scene = new Scene(mMainView);
        mStage.setScene(scene);
        mStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                mModel.destroy();
                Platform.exit();
            }
        });
        
        mSingleEndPointViews = new ViewCache<EndPoint, Node>();
        
        mStage.show();
    }
    
    @Override
    public void onEndPointSelected(EndPoint ep) {
        LOG.info("Selected EndPoint: " + ep);
        if (ep == null) return;
        Node node = mSingleEndPointViews.getViewForElement(ep);
        if (node == null) {
            node = new EndPointView(ep);
            mSingleEndPointViews.addView(ep, node);
        }
        mMainView.setCenterPane((EndPointView)node);
    }

    @Override
    public void update() {
        // this represents a pull model.  Everytime the controller 
        // Needs to update the state of itself it pulls the data from the model.
        
        // Update the Distributed bus views
        List<EndPoint> distributed = mModel.getDistributedServices();
        // Update the list of local services.
        List<EndPoint> locals = mModel.getLocalServices();
        mEndPointsView.updateState(distributed, locals);
    }

    @Override
    public void showError(String message) {
        mErrorDialog.setText("Error", message);
        mErrorDialog.show();
    }

    @Override
    public void showMethod(Method method) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void showSignal(Signal signal) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void showProperty(Property property) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void showSignalReceived(SignalContext signalReceived) {
        // TODO Auto-generated method stub
        
    }

}
