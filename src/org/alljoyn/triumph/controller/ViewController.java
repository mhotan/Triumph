package org.alljoyn.triumph.controller;

import java.util.List;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.alljoyn.triumph.model.TransactionLogger.Transaction;
import org.alljoyn.triumph.model.TransactionLogger.Transaction.TYPE;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.InterfaceComponent;
import org.alljoyn.triumph.model.components.SignalContext;
import org.alljoyn.triumph.util.ViewCache;
import org.alljoyn.triumph.view.CustomVBox;
import org.alljoyn.triumph.view.EndPointView;
import org.alljoyn.triumph.view.ErrorDialog;
import org.alljoyn.triumph.view.LogView;
import org.alljoyn.triumph.view.LogView.OnClickListener;
import org.alljoyn.triumph.view.MessagePane;
import org.alljoyn.triumph.view.ServicesView;
import org.alljoyn.triumph.view.SignalsReceivedView;
import org.alljoyn.triumph.view.SignalsReceivedView.SignalReceivedListener;
import org.alljoyn.triumph.view.TabbedSupportView;
import org.alljoyn.triumph.view.TriumphViewable;

public class ViewController implements EndPointListener, TriumphViewable, OnClickListener, SignalReceivedListener {

//    /**
//     * Logger for the stats.
//     */
//    private static final Logger LOG = Logger.getLogger(ViewController.class.getSimpleName());

    private final Stage mStage;

    /**
     * This is strictly a container for more intricate view controllers.
     * This just structures the views appropiately.
     */
    private final CustomVBox mMainView;

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
    private final TriumphController mModel;

    /**
     * log view.
     */
    private final LogView mLogView;

    /**
     * Signal received view.
     */
    private final SignalsReceivedView mSignalsReceivedView;

    private final TabbedSupportView mTabbedView;

    /**
     * Private cache for this endpoint
     */
    private final ViewCache<EndPoint, EndPointView> mEndPointViewCache;

    /**
     * Creates a Controller that creates and manages internal views.
     * 
     * @param primaryStage Stage to build view over.
     */
    public ViewController(Stage primaryStage) {
        mStage = primaryStage;
        mModel = TriumphController.getInstance();

        // Create a view that presesents endpoints/services as they come in.
        mEndPointsView = new ServicesView();
        mEndPointsView.addListener(this);

        // Default error dialog.
        mErrorDialog = new ErrorDialog("Error", "Unknown error");

        // Create a cache for the EndPoint
        mEndPointViewCache = new ViewCache<EndPoint, EndPointView>();

        // Log view.
        mLogView = new LogView();
        mLogView.addListener(this);
        mSignalsReceivedView = new SignalsReceivedView(this);
        mTabbedView = new TabbedSupportView();
        mTabbedView.setLogView(mLogView);
        mTabbedView.setSignalReceivedView(mSignalsReceivedView);

        // Compose the structure of this view
        mMainView = buildMainView();
        Scene scene = new Scene(mMainView);
        mStage.setScene(scene);
        mStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                mModel.destroy();
                Platform.exit();
            }
        });
        mStage.show();
    }
    
    private CustomVBox buildMainView() {
        // Create the view that holds the rest of the views.
        CustomVBox view = new CustomVBox(mEndPointsView, mTabbedView);
        view.setMinSize(800, 600);
        return view;
    }

    /**
     * 
     * @param pane
     */
    private void setContentPane(Pane pane) {
        mMainView.setCenterPane(pane);
    }
    
    /**
     * Attempts to remove pane.
     * @param pane Pane to remove.
     * @return true if successfully removed, false otherwise
     */
    private boolean removeContentPane(Pane pane) {
        return mMainView.removeCenterPane(pane);
    }
    
    @Override
    public void onEndPointSelected(EndPoint ep) {
        if (ep == null) return;
        
        // Attempt to build endpoint
        if (!TriumphController.getInstance().buildService(ep)) {
            setContentPane(new MessagePane("Unable to connect to " + ep, "Please check that you are using the correct port number"));
            return;
        }
        
        EndPointView node = mEndPointViewCache.getViewForElement(ep);
        if (node == null) {
            node = new EndPointView(ep);
            mEndPointViewCache.addView(ep, node);
        }
        setContentPane((EndPointView)node);
    }

    @Override
    public void onEndPointRemoved(EndPoint ep) {
        EndPointView view = mEndPointViewCache.getViewForElement(ep);
        
        // Remove the endpoint from the mapping.
        mEndPointViewCache.removeView(ep);
        // No view found
        if (view == null) {
            return;
        }
        // Attempt to remove the endpoint view if it is currently being displayed
        removeContentPane(view);
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
        showError("Error", message);
    }

    private void showError(String title, String message) {
        mErrorDialog.setText(title, message);
        mErrorDialog.show();
    }

    @Override
    public void showSignalReceived(SignalContext signalReceived) {
        mSignalsReceivedView.addSignal(signalReceived);
    }

    @Override
    public void onSignalContextSelected(SignalContext context) {
        if (context == null) return;
        attemptToShow(context.getSignal());
    }

    @Override
    public void onTransactionClicked(Transaction transaction) {
        TYPE type = transaction.getType();
        if (type == TYPE.METHOD_INVOKE || type == TYPE.SIGNAL_EMIT 
                || type == TYPE.PROPERTY_GET || type == TYPE.PROPERTY_SET) {
            attemptToShow(transaction.getInterfaceComponent());
        }
    }
    
    /**
     * Attempt to show the interface component.
     * @param component
     */
    private void attemptToShow(InterfaceComponent component) {
        EndPoint ep = component.getInterface().getObject().getOwner();
        Node node = mEndPointViewCache.getViewForElement(ep);
        if (node == null) {
            showError("EndPoint not available", "The session has been lost for the endpoint " + ep.getName());
            return;
        }
        EndPointView epView = (EndPointView)node;
        // place the endpoint vview insid
        setContentPane(epView);
        epView.showViewForComponent(component);
    }

    

}
