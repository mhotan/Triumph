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

/**
 * Class that represents the established session with this application
 * and the well known name advertised locally or distributively.  Therefore a specific session
 * is exclusive to a specific advertised well known name.
 * 
 * @author mhotan
 */
public class Session extends SessionListener {

	/**
	 * Session Port number fir connecting to specific Alljoyn Bus 
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
	 * A reference to hold introspect data once retrieved.
	 * if this is null then introspect data has yet to be retrieved sucessfully.
	 * else then introspect data is already retrieved.
	 */
	//	private Map<String, String> mIntrospectData;
	//	
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

		// Attempt to join a session if there is a valid port number to do so.
		if (port != 0 && join() != Status.OK)
			throw new IllegalStateException("Unable to join session with the bus");

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
		ProxyBusObject proxy = getProxy(objectPath, new Class<?>[] { Introspectable.class});
		Introspectable i = proxy.getInterface(Introspectable.class);
		return i.Introspect();
		
		//		if (mIntrospectData.containsKey(objectPath))
		/*//			return mIntrospectData.get(objectPath);

		if (objectPath == null) {
			throw new IllegalArgumentException("Null objectPath in get Introspect");
		}
		ProxyBusObject proxy =  mBus.getProxyBusObject(mWellKnownName,
				objectPath,
				sessionId,
				new Class<?>[] { Introspectable.class});
		Introspectable i = proxy.getInterface(Introspectable.class);
		String retValue = i.Introspect();

		mProxies.put(objectPath, proxy);
		return retValue;*/
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
		return getProxy(objPath, new Class<?>[] {Introspectable.class});
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

}
