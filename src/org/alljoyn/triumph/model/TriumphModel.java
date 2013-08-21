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

import javafx.scene.control.TreeItem;

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
import org.alljoyn.triumph.model.components.AllJoynComponent;
import org.alljoyn.triumph.model.components.AllJoynInterface;
import org.alljoyn.triumph.model.components.AllJoynObject;
import org.alljoyn.triumph.model.components.AllJoynService;
import org.alljoyn.triumph.model.components.AllJoynService.SERVICE_TYPE;
import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.Signal;
import org.alljoyn.triumph.model.components.TriumphAJParser;
import org.alljoyn.triumph.model.components.arguments.Argument;
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
public class TriumphModel implements BusObserverListener, Destroyable {

	private static final Logger LOGGER = MainApplication.getLogger();

	/**
	 * Application name to register for bus attachment
	 */
	private static final String BUSATTACHMENTNAME = "org.alljoyn.triumph.sessions";


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
	private final Set<String> mDistributedServices, mLocalServices;

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
		mDistributedServices = new HashSet<String>();
		mLocalServices = new HashSet<String>();

		// Destroyable list
		mDestroyables = new ArrayList<Destroyable>();
		// List of Views
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
		mSessionManager = new SessionManager(mBus);
		// TODO Add more Components

		mDestroyables.add(mBusObserver);
		mDestroyables.add(mSessionManager);
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
			LOGGER.log(Level.SEVERE, "SessionManager, Unable to connect to bus");
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

	public synchronized List<AllJoynService> getDistributedServices() {
		List<String> servicesStr = new ArrayList<String>(mDistributedServices);
		List<AllJoynService> services = new ArrayList<AllJoynService>(servicesStr.size());
		for (String service: servicesStr) {
		    AllJoynService ser = new AllJoynService(service);
		    ser.setServiceType(SERVICE_TYPE.REMOTE);
			services.add(ser);
		}
		return services;
	}

	public synchronized List<AllJoynService> getLocalServices() {
		// Extract all the names of the local services
		List<String> servicesStr = new ArrayList<String>(mLocalServices);
		List<AllJoynService> services = new ArrayList<AllJoynService>(servicesStr.size());
		for (String service: servicesStr) {
		    AllJoynService ser = new AllJoynService(service);
            ser.setServiceType(SERVICE_TYPE.LOCAL);
            services.add(ser);
		}
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
	public void onDistributedNameFound(Collection<String> name) {
		mDistributedServices.addAll(name);
		broadcastUpdate();
	}

	@Override
	public void onLocalNameFound(Collection<String> name) {
		mLocalServices.addAll(name);
		broadcastUpdate();
	}

	@Override
	public void onNameLost(Collection<String> names) {
		mDistributedServices.removeAll(names);
		mLocalServices.removeAll(names);
		for (String name: names) {
			LOGGER.info("Service lost: " + name);
		}
		broadcastUpdate();
	}

	/**
	 * This call is to signify that an event has happened and
	 * more detail about the requested service is desired. 
	 * 
	 * @param service name of service that is being requested
	 */
	public void onServiceSelected(AllJoynService service) {

		// If the service is not empty then return fast
		// There is no need to parse any data because 
		if (!service.isEmpty()) 
			return;
		
		try {
		    TreeItem<AllJoynComponent> root = buildTree(service, SessionPortStorage.getPort(service.getName()));
		    // Expand the tree showing all the objects.
		    root.setExpanded(true);
		} catch (TriumphException e) {
		    broadCastError(e.getMessage());
		}
	}
	
	/**
	 * Attempts to build service.
	 * 
	 * @param service Service to attempt to build 
	 * @return The complete built service
	 * @throws TriumphException Exception if could not communicate with the service
	 */
	public AllJoynService buildService(AllJoynService service) throws TriumphException {
	    return buildService(service, SessionPortStorage.getPort(service.getName()));
	}

	/**
	 * This method is to be invoked when 
	 * 
	 * @param method Method that is selected
	 */
	public void onMethodSelected(Method method) {
		// It is up to the view in case they need to handle the production of the correct view.
		if (method == null) {
			throw new NullPointerException("TriumphModel, onMethodSelected: argument method cannot be null");
		}
		broadcastMethod(method);
	}

	/**
	 * Notifies the data model that the signal wants to be focused on.
	 * @param signal Signal that was selected
	 */
	public void onSignalSelected(Signal signal) {
		broadcastSignal(signal);
	}

	/**
	 * Notifies the model that the property has been selected
	 * @param property Property that was selected
	 */
	public void onPropertySelected(Property property) {
		broadcastProperty(property);
	}
	
	/**
	 * Calls remote method for this specific method instance.
	 * 
	 * @param method Method to invoke
	 * @param arguments indexed argument list
	 * @return Unmarshaled result
	 * @throws BusException Error
	 */
	public Object onMethodInvoked(Method method, List<Argument<?>> arguments) throws BusException {
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
	public Object onMethodInvoked(Method method, Argument<?>[] arguments) throws BusException {

		// Make sure that the number of input arguments
		// match the number of arguments we have
		int numInputArgs = method.getInputArguments().size();
		if (arguments.length != numInputArgs) {
			throw new RuntimeException("Number of input arguments do not match");
		}

		// Extract the chain of all the objects, interface, and services.
		AllJoynInterface iface = method.getInterface();
		AllJoynObject object = iface.getObject();
		AllJoynService service = object.getOwner();

		// Get the ProxyBusObject to invoke the method.
		Session session = mSessionManager.getSession(service.getName());
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
		return TriumphCPPAdapter.callMethod(mBus, proxy, ifaceName, methodName, inputSig, args);
	}

	/**
	 * Emits a signal with specified arguments.
	 * 
	 * @param signal Signal to emit
	 * @param arguments Arguments of the signal
	 * @throws BusException Error occured
	 */
	public void onEmitSignal(Signal signal, List<Argument<?>> arguments) throws BusException {
	    Argument<?>[] args = new Argument<?>[arguments.size()];
        for (int i = 0; i < args.length; ++i) {
            args[i] = arguments.get(i);
        }
        onEmitSignal(signal, args);
	}
	
	/**
	 * Emits a signal with the correct arguments.  It is up to the client
	 * to create an array of arguments that matches the signature of the signal.
	 * 
	 * @param signal Signal to emit
	 * @param arguments Arguments of the Signal to emit
	 */
	public void onEmitSignal(Signal signal, Argument<?>[] arguments) throws BusException {

		// Make sure that the number of input arguments
		// match the number of arguments we have
		int numInputArgs = signal.getOutputArguments().size();
		if (arguments.length != numInputArgs) {
			throw new RuntimeException("Number of input arguments do not match");
		}

		// Extract the chain of all the objects, interface, and services.
		AllJoynInterface iface = signal.getInterface();
		AllJoynObject object = iface.getObject();
		AllJoynService service = object.getOwner();

		// Destination endpoint
		String destination = service.getName();
		String ifaceName = iface.getName();
		String signalName = signal.getName();
		String signature = signal.getOutputSignature();

		// Get the ProxyBusObject to invoke the method.
		Session session = mSessionManager.getSession(destination);

		// Extract all the values of the arguments 
		// in a sequential Object array that conforms
		// to the ProxyBusObject callMethod function.
		Object[] args = new Object[arguments.length];
		for (int i = 0; i < args.length; ++i) 
			args[i] = arguments[i].getValue();

		// create an emitter that is capable of launching a signal
		SignalEmitter emitter = new SignalEmitter(mSignalBusObject, destination, 
				session.getSessionID(), GlobalBroadcast.Off);
		TriumphCPPAdapter.emitSignal(emitter, ifaceName, signalName, signature, args);
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

		AllJoynInterface iface = property.getInterface();
		AllJoynObject object = iface.getObject();
		AllJoynService service = object.getOwner();

		// Destination endpoint
		String destination = service.getName();
		String ifaceName = iface.getName();
		String propertyName = property.getName();
		String signature = property.getSignature();

		// Get the ProxyBusObject to invoke the method.
		Session session = mSessionManager.getSession(destination);
		ProxyBusObject proxy = session.getProxy(object.getName());

		TriumphCPPAdapter.setProperty(mBus, proxy, ifaceName, propertyName, signature, arg.getValue());
	}
	
	/**
	 * Gets the remote or local property value.
	 * 
	 * @param property Property that contains the value to get.
	 * @return Object value of the property
	 * @throws BusException An error occured
	 */
	public Object getProperty(Property property) throws BusException {
		
		AllJoynInterface iface = property.getInterface();
		AllJoynObject object = iface.getObject();
		AllJoynService service = object.getOwner();

		// Destination endpoint
		String destination = service.getName();
		String ifaceName = iface.getName();
		String propertyName = property.getName();

		// Get the ProxyBusObject to invoke the method.
		Session session = mSessionManager.getSession(destination);
		ProxyBusObject proxy = session.getProxy(object.getName());
		
		return TriumphCPPAdapter.getProperty(mBus, proxy, ifaceName, propertyName);
	}

	/* ********************************************************* */
	/* 	Broadcast Change to all listeners						 */	  
	/* ********************************************************* */

	// TODO Note we can add more features to the views
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
	 * This method notifies all the views that there is a
	 * method that is desired to be shown.
	 * 
	 * @param method Method to present
	 */
	private void broadcastMethod(Method method) {
		assert method != null: "Method to show cannot be null";
		for (TriumphViewable view: mViewables)
			view.showMethod(method);
	}

	/**
	 * Notify all the views to portray this disinct
	 * 
	 * @param signal Signal to present in the UI
	 */
	private void broadcastSignal(Signal signal) {
		assert signal != null: "Signal to show cannot be null";
		for (TriumphViewable view: mViewables)
			view.showSignal(signal);
	}

	/**
	 * 
	 * @param property
	 */
	private void broadcastProperty(Property property) {
		assert property != null: "Signal to show cannot be null";
		for (TriumphViewable view: mViewables)
			view.showProperty(property);
	}

	/**
	 * Notifies all listeners there is was an error
	 * @param message message to notify all viewers with
	 */
	private void broadCastError(String message) {
		for (TriumphViewable view: mViewables)
			view.showError(message);
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
	 * @return The Alljoyn service to use.
	 * @throws TriumphException
	 */
	private AllJoynService buildService(AllJoynService service, short sessionPort) throws TriumphException {
	    LOGGER.fine("Building Service Via Introspection: " + service);
	    
	    // Attempt to create a session
	    Session session = mSessionManager.getSession(service.getName());
	    if (session == null) 
	        session = mSessionManager.createNewSession(service.getName(), sessionPort);
	    // If fails throw exception
	    ProxyBusObject proxy = session.getProxy("org/alljoyn/Bus/Peer");
	    service.saveBusPeerProxy(proxy);
	    
        TriumphAJParser parser = new TriumphAJParser(mSessionManager);
        return parser.parseIntrospectData(service, sessionPort);
	}

	/**
	 * For a given service return all the Objects that exists within 
	 * that service.
	 * 
	 * @param service Well known name of service
	 * @return List of all Objects associated with the service
	 * @throws Unable to get introspection
	 */
	private TreeItem<AllJoynComponent> buildTree(AllJoynService service, short sessionPort) 
	        throws TriumphException {
		return buildService(service, sessionPort).toTree();
	}

	/**
	 * Class that represents a source of any kind of generic signal.
	 * as of right now there are no other known interfaces it should implement.
	 * 
	 * @author mhotan
	 */
	private static class SignalSource implements BusObject {

	}


}
