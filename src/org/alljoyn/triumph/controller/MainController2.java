package org.alljoyn.triumph.controller;

import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.alljoyn.triumph.model.TransactionLogger.MethodTransaction;
import org.alljoyn.triumph.model.TransactionLogger.PropertyTransaction;
import org.alljoyn.triumph.model.TransactionLogger.SignalTransaction;
import org.alljoyn.triumph.model.TransactionLogger.Transaction;
import org.alljoyn.triumph.model.TransactionLogger.Transaction.TYPE;
import org.alljoyn.triumph.model.TriumphModel;
import org.alljoyn.triumph.model.components.AJObject;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.Interface;
import org.alljoyn.triumph.model.components.InterfaceComponent;
import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.Signal;
import org.alljoyn.triumph.model.components.SignalContext;
import org.alljoyn.triumph.util.ViewCache;
import org.alljoyn.triumph.view.EndPointView;
import org.alljoyn.triumph.view.ErrorDialog;
import org.alljoyn.triumph.view.LogView;
import org.alljoyn.triumph.view.MainBorderView;
import org.alljoyn.triumph.view.MessagePane;
import org.alljoyn.triumph.view.ServicesView;
import org.alljoyn.triumph.view.SignalReceivedView;
import org.alljoyn.triumph.view.TabbedSupportView;
import org.alljoyn.triumph.view.TriumphViewable;
import org.alljoyn.triumph.view.LogView.OnClickListener;
import org.alljoyn.triumph.view.SignalReceivedView.SignalReceivedListener;

public class MainController2 implements EndPointListener, TriumphViewable, OnClickListener, SignalReceivedListener {

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

    /**
     * log view.
     */
    private final LogView mLogView;

    /**
     * Signal received view.
     */
    private final SignalReceivedView mSignalsReceivedView;

    private final TabbedSupportView mTabbedView;

    /**
     * Private cache for this endpoint
     */
    private final ViewCache<EndPoint, Node> mEndPointViewCache;

    /**
     * Creates a Controller that creates and manages internal views.
     * 
     * @param primaryStage Stage to build view over.
     */
    public MainController2(Stage primaryStage) {
        mStage = primaryStage;
        mModel = TriumphModel.getInstance();

        // Create the view that holds the rest of the views.
        mMainView = new MainBorderView();

        // Create a view that presesents endpoints/services as they come in.
        mEndPointsView = new ServicesView();
        mMainView.setTopPane(mEndPointsView);
        mEndPointsView.addListener(this);

        // Error dialog to present.
        mErrorDialog = new ErrorDialog("Error", "Unknown error");

        // Create a cache for the EndPoint
        mEndPointViewCache = new ViewCache<EndPoint, Node>();
        mMainView.setCenterPane(null); // Disable the center pane.
        // This prevents unecessary empty space.

        // Log view.
        mLogView = new LogView();
        mLogView.addListener(this);
        mSignalsReceivedView = new SignalReceivedView(this);
        mTabbedView = new TabbedSupportView();
        mTabbedView.setLogView(mLogView);
        mTabbedView.setSignalReceivedView(mSignalsReceivedView);

        // Set the bottom view.
        mMainView.setBottomPane(mTabbedView);
        
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
        mStage.show();
    }

    @Override
    public void onEndPointSelected(EndPoint ep) {
        if (ep == null) return;
        
        // Attempt to build endpoint
        if (!TriumphModel.getInstance().buildService(ep)) {
            mMainView.setCenterPane(new MessagePane("Unable to connect to " + ep, "Please check that you are using the correct port number"));
            return;
        }
        
        Node node = mEndPointViewCache.getViewForElement(ep);
        if (node == null) {
            node = new EndPointView(ep);
            mEndPointViewCache.addView(ep, node);
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
        showError("Error", message);
    }

    private void showError(String title, String message) {
        mErrorDialog.setText(title, message);
        mErrorDialog.show();
    }
    
    @Override
    public void showMethod(Method method) {
        // probably do nothing because it is taken care of in EndPointView.
    }

    @Override
    public void showSignal(Signal signal) {
        // probably do nothing because it is taken care of in EndPointView.
    }

    @Override
    public void showProperty(Property property) {
        // probably do nothing because it is taken care of in EndPointView.
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
        mMainView.setCenterPane(epView);
        epView.showViewForComponent(component);
    }

}
