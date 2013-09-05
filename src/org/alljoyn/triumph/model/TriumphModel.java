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

package org.alljoyn.triumph.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusAttachment.RemoteMessage;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.SignalEmitter.GlobalBroadcast;
import org.alljoyn.bus.Status;
import org.alljoyn.triumph.MainApplication;
import org.alljoyn.triumph.TriumphCPPAdapter;
import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.model.BusObserver.BusObserverListener;
import org.alljoyn.triumph.model.components.AJObject;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.EndPoint.SERVICE_TYPE;
import org.alljoyn.triumph.model.components.Interface;
import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.Signal;
import org.alljoyn.triumph.model.components.SignalContext;
import org.alljoyn.triumph.model.components.SignalHandler.SignalListener;
import org.alljoyn.triumph.model.components.SignalHandlerManager;
import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.model.session.Session;
import org.alljoyn.triumph.model.session.SessionManager;
import org.alljoyn.triumph.util.SessionPortStorage;
import org.alljoyn.triumph.view.TriumphViewable;

/**
 * Object the represents the abstraction layer that separates all the components
 * of the Model of this application away from the View and Controller.
 * 
 * In Particular it provides the top layer to the Model for this application.
 * 
 * *----------------------------------------------------*
 * *		View 			| 		Controller(s)		*
 * *----------------------------------------------------*
 * *					TriumphModel					*
 * *----------------------------------------------------*
 * * Bus Observer * Session Manager *					*
 * *----------------------------------------------------*
 * 
 * @author Michael Hotan mhotan@quicinc.com
 */
public class TriumphModel implements BusObserverListener, SignalListener, Destroyable {

    private static final Logger LOG = MainApplication.getLogger();

    /**
     * 1s for to update the view.
     */
    private static final int PERIOD = 1; 
    
    /**
     * Application name to register for bus attachment
     */
    private static final String BUSATTACHMENTNAME = "org.alljoyn.triumph";

    /**
     * Reference to an instance of this class
     * Note: Follows singleton pattern to enforce that there 
     * only exists one model 
     */
    private static TriumphModel mModel;

    /**
     * List of destroyable object that 
     * this model needs to keep track of.
     * 
     * Destroyables are intended to be a subcomponent of the model
     */
    private final List<Destroyable> mDestroyables;

    /**
     * Views that this instance is aware of to notify of updates
     * 
     * Intended to be an abstract interface for being able to 
     * adjust and update the view.
     */
    private final List<TriumphViewable> mViewables;

    /**
     * All locally available Service.
     */
    private final Set<EndPoint> mDistributedServices, mLocalServices;

    /**
     * Bus observer
     */
    private final BusObserver mBusObserver;

    /**
     * Manages independent calls to 
     */
    private final SessionManager mSessionManager;

    /**
     * Bus attachment for this application
     */
    private final BusAttachment mBus;

    /**
     * Our Standalone bus object for sending out signals.
     */
    private final SignalSource mSignalBusObject;

    /**
     * Manages all the signal handlers
     */
    private final SignalHandlerManager mSignalHandlerManager;
    
    /**
     * 
     */
    private final RecievedSignalBroadcaster mSignalBroadcaster;

    /**
     * Returns an instance of the model. 
     * 
     * Note: Only one instance may exist 
     * 
     * @return Single instance of the model
     */
    public static TriumphModel getInstance() {
        if (mModel == null) {
            mModel = new TriumphModel();
        }
        return mModel;
    }

    /**
     * Creates and initialize a model for 
     */
    private TriumphModel() {

        // Instantiate members for tracking variables

        // Set to track the name for all the services
        mDistributedServices = new HashSet<EndPoint>();
        mLocalServices = new HashSet<EndPoint>();

        // Destroyable list
        mDestroyables = new ArrayList<Destroyable>();
        mViewables = new ArrayList<TriumphViewable>();

        mBus =  new BusAttachment(BUSATTACHMENTNAME, RemoteMessage.Receive);
        attemptConnect(mBus);

        mSignalBusObject = new SignalSource();
        String objectPath = "/signal/emitter";
        Status status = mBus.registerBusObject(mSignalBusObject, objectPath);
        if (status != Status.OK) {
            throw new IllegalStateException("Unable to register signal Emitter " + objectPath);
        }

        mBusObserver = new BusObserver(mBus, this, true);
        mSessionManager = new SessionManager(mBus, this);
        mSignalHandlerManager = new SignalHandlerManager(mBus);
        mSignalHandlerManager.setListener(this);
        // TODO Add more Components

        mDestroyables.add(mBusObserver);
        mDestroyables.add(mSessionManager);
        mDestroyables.add(mSignalHandlerManager);
        
        mSignalBroadcaster = new RecievedSignalBroadcaster();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(PERIOD), mSignalBroadcaster));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Attempt to connect
     * @return
     */
    private static boolean attemptConnect(BusAttachment bus) {
        if (bus.isConnected())
            return true;
        // Connect Bus attachment to the Virtual Distributed Bus
        org.alljoyn.bus.Status status = bus.connect();
        if (status != org.alljoyn.bus.Status.OK) {
            LOG.log(Level.SEVERE, "SessionManager, Unable to connect to bus");
            return false;
        }
        return true;
    }

    @Override
    public void destroy() {
        for (Destroyable d: mDestroyables) 
            d.destroy();
    }

    /* ********************************************************* */
    /* 	Getters													 */	  
    /* ********************************************************* */

    public synchronized List<EndPoint> getDistributedServices() {
        List<EndPoint> services = new ArrayList<EndPoint>(mDistributedServices);
        return services;
    }

    public synchronized List<EndPoint> getLocalServices() {
        // Extract all the names of the local services
        List<EndPoint> services = new ArrayList<EndPoint>(mLocalServices);
        return services;
    }

    /**
     * Synchronizes the state of allE its components 
     * and then notifies all the views of the latest state 
     * as of time of method invokation.
     * @param string 
     */
    public void sync() {
        mBusObserver.sync();
        //TODO Add more components
    }

    /* ********************************************************* */
    /* 	Synchronous Callback									 */	  
    /* ********************************************************* */

    @Override
    public void onDistributedNameFound(Collection<String> names) {
        for (String name : names) {
            mDistributedServices.add(new EndPoint(name, SERVICE_TYPE.REMOTE));
        }
        broadcastUpdate();
    }

    @Override
    public void onLocalNameFound(Collection<String> names) {
        for (String name: names) {
            mLocalServices.add(new EndPoint(name, SERVICE_TYPE.LOCAL));
        }
        broadcastUpdate();
    }

    @Override
    public void onNameLost(Collection<String> names) {
        // Remove all the endpoints that have the same name
        Collection<EndPoint> toRemove = new HashSet<EndPoint>();
        for (EndPoint ep : mDistributedServices) {
            if (names.contains(ep.getName()));
                toRemove.add(ep);
        }
        mDistributedServices.removeAll(toRemove);
        toRemove.clear();
        
        // Remove all the Endpoints that have the same name
        for (EndPoint ep : mLocalServices) {
            if (names.contains(ep.getName()));
                toRemove.add(ep);
        }
        mLocalServices.removeAll(toRemove);
        
        broadcastUpdate();
    }

    /**
     * Attempts to build service.
     * 
     * @param service Service to attempt to build 
     * @return true on success, false otherwise
     */
    public boolean buildService(EndPoint service) {
        return buildService(service, SessionPortStorage.getPort(service.getName()));
    }

    /**
     * Calls remote method for this specific method instance.
     * 
     * @param method Method to invoke
     * @param arguments indexed argument list
     * @return Unmarshaled result
     * @throws BusException Error
     */
    public Argument<?> onMethodInvoked(Method method, List<Argument<?>> arguments) throws BusException {
        Argument<?>[] args = new Argument<?>[arguments.size()];
        for (int i = 0; i < args.length; ++i) {
            args[i] = arguments.get(i);
        }
        return onMethodInvoked(method, args);
    }

    /**
     * Calls remote method for this specific method instance.
     * 
     * @param method Method to invoke.
     * @param arguments Array of arguments the correlate to method input arguments
     * @return Unmarshaled result
     * @throws BusException Error
     */
    public Argument<?> onMethodInvoked(Method method, Argument<?>[] arguments) throws BusException {

        // Make sure that the number of input arguments
        // match the number of arguments we have
        int numInputArgs = method.getInputArguments().size();
        if (arguments.length != numInputArgs) {
            throw new RuntimeException("Number of input arguments do not match");
        }

        // Extract the chain of all the objects, interface, and services.
        Interface iface = method.getInterface();
        AJObject object = iface.getObject();
        EndPoint service = object.getOwner();

        // Get the ProxyBusObject to invoke the method.
        Session session = mSessionManager.getSession(service);
        ProxyBusObject proxy = session.getProxy(object.getName());

        // Extract all the values of the arguments 
        // in a sequential Object array that conforms
        // to the ProxyBusObject callMethod function.
        Object[] args = new Object[arguments.length];
        for (int i = 0; i < args.length; ++i) 
            args[i] = arguments[i].getValue();		

        String ifaceName = iface.getName();
        String methodName = method.getName();
        String inputSig = method.getInputSignature();	
        Object output = TriumphCPPAdapter.callMethod(mBus, proxy, ifaceName, methodName, inputSig, args);
        
     // Try to decipher the output argument name
        List<Argument<?>> outargs = method.getOutputArguments();
        String name = "Output";
        if (outargs.size() == 1) {
            String tmp = outargs.get(0).getName();
            name = tmp == null || tmp.isEmpty() ? name : tmp;
        }
        
        // Unmarshal based on the method output argument signature.
        Argument<?> outArg = ArgumentFactory.getArgument(name, method.getOutputSignature(), output);
        
        // Log the transaction
        TransactionLogger.getInstance().logMethodInvocation(method, arguments, outArg);
        return outArg;
    }

    /**
     * Emits a signal with specified arguments.
     * 
     * @param signal Signal to emit
     * @param arguments Arguments of the signal
     * @throws BusException Error occured
     */
    public void onEmitSignal(Signal signal, List<Argument<?>> arguments, boolean sessionless) throws BusException {
        Argument<?>[] args = new Argument<?>[arguments.size()];
        for (int i = 0; i < args.length; ++i) {
            args[i] = arguments.get(i);
        }
        onEmitSignal(signal, args, sessionless);
    }

    /**
     * Emits a signal with the correct arguments.  It is up to the client
     * to create an array of arguments that matches the signature of the signal.
     * 
     * @param signal Signal to emit
     * @param arguments Arguments of the Signal to emit
     */
    public void onEmitSignal(Signal signal, Argument<?>[] arguments, boolean sessionless) throws BusException {

        // Make sure that the number of input arguments
        // match the number of arguments we have
        int numInputArgs = signal.getOutputArguments().size();
        if (arguments.length != numInputArgs) {
            throw new RuntimeException("Number of input arguments do not match");
        }

        // Extract the chain of all the objects, interface, and services.
        Interface iface = signal.getInterface();
        AJObject object = iface.getObject();
        EndPoint service = object.getOwner();

        // Destination endpoint
        String destination = service.getName();
        String ifaceName = iface.getName();
        String signalName = signal.getName();
        String signature = signal.getOutputSignature();

        // Get the ProxyBusObject to invoke the method.
        Session session = mSessionManager.getSession(service);

        // Extract all the values of the arguments 
        // in a sequential Object array that conforms
        // to the ProxyBusObject callMethod function.
        Object[] args = new Object[arguments.length];
        for (int i = 0; i < args.length; ++i) 
            args[i] = arguments[i].getValue();

        // create an emitter that is capable of launching a signal
        SignalEmitter emitter;
        if (sessionless) {
            emitter = new SignalEmitter(mSignalBusObject, destination, 
                    0, GlobalBroadcast.Off);
            emitter.setSessionlessFlag(true);
        } else {
            emitter = new SignalEmitter(mSignalBusObject, destination, 
                    session.getSessionID(), GlobalBroadcast.Off);
        }
        TriumphCPPAdapter.emitSignal(emitter, ifaceName, signalName, signature, args);
        
        // Log the transaction
        TransactionLogger.getInstance().logSignalEmition(signal, arguments);
    }

    /**
     * Attempts to set this property with the value defined by arg.
     * <p>
     * the property can either be a remote or local property.
     * 
     * @param property Remote property
     * @param arg To set the property to.
     */
    public void setProperty(Property property, Argument<?> arg) {

        Interface iface = property.getInterface();
        AJObject object = iface.getObject();
        EndPoint service = object.getOwner();

        // Destination endpoint
        String ifaceName = iface.getName();
        String propertyName = property.getName();
        String signature = property.getSignature();

        // Get the ProxyBusObject to invoke the method.
        Session session = mSessionManager.getSession(service);
        ProxyBusObject proxy = session.getProxy(object.getName());

        TriumphCPPAdapter.setProperty(mBus, proxy, ifaceName, propertyName, signature, arg.getValue());
        
        // Log the transaction
        TransactionLogger.getInstance().logPropertySet(property, arg);
    }

    /**
     * Gets the remote or local property value.
     * 
     * @param property Property that contains the value to get.
     * @return Object value of the property
     * @throws BusException An error occured
     */
    public Argument<?> getProperty(Property property) throws BusException {

        Interface iface = property.getInterface();
        AJObject object = iface.getObject();
        EndPoint service = object.getOwner();

        // Destination endpoint
        String ifaceName = iface.getName();
        String propertyName = property.getName();

        // Get the ProxyBusObject to invoke the method.
        Session session = mSessionManager.getSession(service);
        ProxyBusObject proxy = session.getProxy(object.getName());
        
        Object propertyObj = TriumphCPPAdapter.getProperty(mBus, proxy, ifaceName, propertyName);
        Argument<?> output = ArgumentFactory.getArgument(property.getName(), property.getSignature(), propertyObj);
        
        // Log the transaction
        TransactionLogger.getInstance().logPropertyGet(property, output);
        return output;
    }

    /* ********************************************************* */
    /*  Components for receiving and sending signals             */
    /* ********************************************************* */

    /**
     * Class that represents a source of any kind of generic signal.
     * as of right now there are no other known interfaces it should implement.
     * 
     * @author mhotan
     */
    private static class SignalSource implements BusObject {

    }

    @Override
    public void onSignalReceived(Signal signal, Object[] objArgs) {
        List<Argument<?>> outargs = signal.getOutputArguments();
        if (objArgs.length != outargs.size()) {
            LOG.warning("Incorrect signal amount received for " + signal + " Expected: " + outargs.size() + " Actual " + objArgs.length);
            return;
        }
        try {
            Argument<?>[] args = new Argument<?>[objArgs.length];
            for (int i = 0; i < args.length; ++i) {
                Argument<?> outArg = outargs.get(i);
                String name = outArg.getName();
                String sig = outArg.getDBusSignature();
                args[i] = ArgumentFactory.getArgument(name, sig, objArgs[i]);
            }
            mSignalBroadcaster.addSignalContext(new SignalContext(signal, args));
        } catch (TriumphException e) {
            LOG.warning("Exception caught when when receiving signal " + signal + " Exception: " + e.getMessage());
        }
    }

    /**
     * Returns the Signal Handler Manager that is in charge of Signal Handlers.
     * 
     * @return The manager for signal handlers
     */
    public SignalHandlerManager getSignalHandlerManager() {
        return mSignalHandlerManager;
    }

    /* ********************************************************* */
    /* 	Broadcast Change to all listeners						 */	  
    /* ********************************************************* */

    // Note we can add more features to the views
    // By adding to TriumphViewable Interface
    // and then implement it in ViewManager.

    /**
     * Broadcast the update to all the known views that need it
     */
    private void broadcastUpdate() {
        for (TriumphViewable view: mViewables)
            view.update();
    }

    /**
     * For a given received signal, broadcast to all the views to show this signalContext.
     * 
     * @param signalReceived Context of the received signal.
     */
    private void broadcastSignalReceived(SignalContext signalReceived) {
        assert signalReceived != null : "Signal Context cannot be null"; 
        for (TriumphViewable view: mViewables)
            view.showSignalReceived(signalReceived);
    }

    /**
     * Notifies all listeners there is was an error
     * 
     * @param message message to notify all viewers with, null signifying that views should hide messages
     */
    private void broadCastError(String message) {
        for (TriumphViewable view: mViewables)
            view.showError(message);
    }

    /**
     * Broadcast to all the views to hide error message
     */
    public void hideError() {
        broadCastError(null);
    }

    /* ********************************************************* 		*/
    /* 	Methods to add and remove view that this instance is aware of 	*/
    /* ********************************************************* 		*/	

    /**
     * 
     * @param view View to add to track
     */
    public void addView(TriumphViewable view) {
        if (mViewables.contains(view)) return;
        mViewables.add(view);
    }

    /**
     * @param view View to stop tracking
     */
    public void removeView(TriumphViewable view) {
        mViewables.remove(view);
    }

    /* ********************************************************* */
    /* 	Helper Methods											 */	  
    /* ********************************************************* */

    /**
     * Build the service and all its components via introspection.
     * 
     * @param service Service to build
     * @param sessionPort Session port to use to connect to endpoint
     * @return true on success, false if unable to connect with specific port number.
     */
    private boolean buildService(EndPoint service, short sessionPort) {
        Session session = mSessionManager.getSession(service);
        if (session == null) 
            session = mSessionManager.createNewSession(service, sessionPort);
        if (session == null)
            return false;
        
        return service.build(session);
    }

    private class RecievedSignalBroadcaster implements EventHandler<ActionEvent> {

        private final List<SignalContext> mReceivedSignals;
        
        RecievedSignalBroadcaster() {
            mReceivedSignals = new ArrayList<SignalContext>();
        }
        
        /**
         * Safely adds the signal context to later broadcast
         * @param context Signal Context to add
         */
        synchronized void addSignalContext(SignalContext context) {
            if (context == null) return;
            mReceivedSignals.add(context);
        }
        
        /**
         * Broadcast all the received signals.
         */
        private synchronized void broadCast() {
            if (mReceivedSignals.isEmpty()) return;
            
            for (SignalContext context: mReceivedSignals) {
                broadcastSignalReceived(context);
            }
            mReceivedSignals.clear();
        }

        @Override
        public void handle(ActionEvent event) {
            broadCast();
        }
        
    }

}