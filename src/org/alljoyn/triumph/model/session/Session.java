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

package org.alljoyn.triumph.model.session;

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.ifaces.Introspectable;
import org.alljoyn.bus.ifaces.Peer;
import org.alljoyn.bus.ifaces.Properties;

/**
 * Class that represents the established session with this application
 * and the well known name advertised locally or distributively.  Therefore a specific session
 * is exclusive to a specific advertised well known name.
 * 
 * @author mhotan
 */
public class Session extends SessionListener {

	/**
	 * Session Port number for connecting to specific Alljoyn Bus endpoint
	 */
	private final short mPortNum;

	/**
	 * Session name to connect to.
	 */
	private final String mWellKnownName; 

	/**
	 * Listener for session listeners
	 */
	private final TriumphSessionListener mListener;

	/**
	 * Bus attachment 
	 */
	private final BusAttachment mBus;

	/**
	 * The established ID number for this session.
	 */
	private int sessionId;

	/**
	 * Hold an array of standard interfaces that we know all 
	 * advertised bus object and bus attachments implement
	 */
	private static final Class<?>[] STANDARD_INTERFACES = {Introspectable.class, Peer.class, Properties.class};
	
	/**
	 * Mapping of objects found in this service to
	 * their corresponding proxy bus objects.
	 */
	private Map<String, ProxyBusObject> mProxies;

	/**
	 * Attempts to create a Session for name
	 * with port number port.
	 * 
	 * @param port Applicaiton specific port number
	 * @param name Name of the Service or Bus well known name on the distributed bus to connect to.
	 * @param bus BusAttachment to use to attach.
	 */
	Session(short port, String name, BusAttachment bus, TriumphSessionListener listener) {
		if (name == null)
			throw new IllegalArgumentException("Name is null");
		if (listener == null)
			throw new IllegalArgumentException("Listener cannot be null");
		if (bus == null) 
			throw new IllegalArgumentException("Bus cannot be null");
		mPortNum = port;
		mWellKnownName = name;
		mListener = listener;
		sessionId = -1;
		mBus = bus;
		mProxies = new HashMap<String, ProxyBusObject>();

		// Check if the client is a local session.  If so attempt to get the introspection
		// data and there fore be able to make following network calls.
		if (mPortNum == 0) {
			ProxyBusObject proxy = getProxy("/");
			Introspectable i = proxy.getInterface(Introspectable.class);
			try {
				// Attempt to call basic function to check 
				// if we have a logical connect 
				i.Introspect();
				return; // Success we got introspection
			} catch (BusException e) {
				throw new IllegalStateException("Unable to connect to '" + name + "'. Check your port number");
			}
		}
		
		// joining with a REMOTE advertised name
		// Attempt to join a session if there is a valid port number to do so.
		Status status = join();
		if (status != Status.OK)
			throw new IllegalStateException("Unable to join session with: '" + name 
			        + "' Status:" + status + ". Check your port number");
	}

	/**
	 * Connect to the object using the provided bus attachment.
	 * Also immediately attempts to connect to a session with the
	 * provided port number.
	 * 
	 * @param bus Bus to attach session to.
	 * @return Whether a bus attachment was made
	 */
	public Status join() {
		SessionOpts sessionOpts = new SessionOpts();
		Mutable.IntegerValue sessionId = new Mutable.IntegerValue();
		Status status = mBus.joinSession(mWellKnownName, mPortNum, sessionId, sessionOpts, this);
		if (status == Status.OK) {
			this.sessionId = sessionId.value;
		}
		return status;
	}

	/**
	 * Returns the session ID associated with this session
	 * @return The session ID associated to this endpoint
	 */
	public int getSessionID() {
		return sessionId == -1 ? BusAttachment.SESSION_ID_ANY : sessionId;
	}
	
	/**
	 * @return The name of the owning service
	 */
	public String getServiceName() {
		return mWellKnownName;
	}

	/**
	 * Close the connection for this session
	 * @return Status of the close request.
	 */
	public Status closeConnection() {
		if (this.sessionId == -1) 
			return Status.OK;
		Status status = mBus.leaveSession(sessionId);
		sessionId = -1;
		return status;
	}

	/**
	 * Returns the introspect data of that object path.
	 * 
	 * @param objectPath Object path to search for.
	 * @return String representation of Introspection of that object
	 * @throws BusException Unable to get Introspect data.
	 */
	public String getIntrospection(String objectPath) throws BusException {
		// Gets a cache Proxy or creates a new one.	
		ProxyBusObject proxy = getProxy(objectPath, STANDARD_INTERFACES);
		Introspectable i = proxy.getInterface(Introspectable.class);
		return i.Introspect();
	}

	@Override
	public void sessionLost(int sessionId) {
		super.sessionLost(sessionId);
		mListener.sessionLost(mWellKnownName, sessionId);
	}

	/**
	 * Returns a proxy object for the Object found under this well known name.
	 * The proxy object returned does not guarantee that it implements
	 * any specific interfaces. Therefore getInterface() can throw a class cast exception.
	 * 
	 * @param objPath The objectPath of the object under the well known name of this session
	 * @return ProxyBusObject of the object found under object path
	 */
	public ProxyBusObject getProxy(String objPath) {
		// Load the default barebones proxy Bus object.
		// All proxy bus objects implement Introspectable.class
		return getProxy(objPath, STANDARD_INTERFACES);
	}
	
	/**
	 * Returns a proxy object for the Object found under this well known name.
	 * The proxy object returned implements all interfaces listed in classes
	 * 
	 * @param objPath Object path within the service.
	 * @param ifaceClasses Interface .class files
	 * @return ProxyBusObject for the following class
	 */
	public ProxyBusObject getProxy(String objPath, Class<?>[] ifaceClasses) {
		// Check if we had a proxy object already established
		ProxyBusObject proxy = mProxies.get(objPath);

		boolean createNew = proxy == null; 
		if (!createNew) {
			// If there already exist a proxy then check 
			// if all the classes request are found
			for (Class<?> clazz: ifaceClasses) {
				if (proxy.getInterface(clazz) == null)
					createNew = true;
			}
		}

		// Create a new proxy object.
		if (createNew) {
			proxy = mBus.getProxyBusObject(
					mWellKnownName, 
					objPath, 
					sessionId, 
					ifaceClasses);
			mProxies.put(objPath, proxy);
		}
		return proxy;
	}

	// Possibly use this later to save the session data
//	/**
//	 * Details of the session port listener.
//	 */
//	private static class SessionDetails {
//	    
//	    public short mPortNum;
//	    public String mEndPointName;
//	    
//	    public SessionDetails() {
//	        mPortNum = 0;
//	        mEndPointName = null;
//	    }
//	    
//	    /**
//	     * 
//	     * @param endpoint Endpoint 
//	     * @param port Port to associate 
//	     */
//	    public SessionDetails(String endpoint, short port) {
//	        mPortNum = port;
//	        mEndPointName = endpoint;
//	    }
//	}
	
}
