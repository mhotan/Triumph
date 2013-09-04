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
import org.alljoyn.bus.InterfaceDescriptionBuilder;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.XMLInterfaceDescriptionBuilder;
import org.alljoyn.bus.ifaces.Introspectable;
import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.model.Destroyable;
import org.alljoyn.triumph.model.components.EndPoint;
import org.w3c.dom.Node;

/**
 * Class that manages the Sessions available to the application
 * 
 * @author mhotan
 */
public class SessionManager implements Destroyable, TriumphSessionListener {

	/**
	 * Mapping of sessions that currently exist between
	 */
	private final Map<EndPoint, Session> mSessions;

	/**
	 * Connection to virtual bus.
	 */
	private final BusAttachment mBus;

	public SessionManager(BusAttachment busAttachment) {
		mSessions = new HashMap<EndPoint, Session>();

		if (!busAttachment.isConnected())
			throw new IllegalArgumentException("Bus attachment is not connected");
		
		// Create a bus attachment to handle the creation of sessions
		mBus = busAttachment;
	}

	/**
	 * gets a connected alljoyn session.
	 * 
	 * @param name Well known name (of service or endpoint) to connect to.
	 * @param portNum Port number to connect to
	 * @return Connected session on success, null on failure
	 */
	public Session getSession(EndPoint ep) {
		return mSessions.get(ep);
	} 

	/**
	 * Saves the current session.  If session is null then
	 * it is ignored.
	 * 
	 * @param toSave Session to save
	 */
	private void saveSession(Session toSave) {
		mSessions.put(toSave.getEndPoint(), toSave);
	}

	/**
	 * Attempts to create a new session 
	 * 
	 * @param ep name of the Service or advertised well known name to connect to.
	 * @param portNum Port number to use to connect
	 * @return null on fail, else session with object
	 * @throws TriumphException Unable to create a session with endpoint
	 */
	public Session createNewSession(EndPoint ep, short portNum) {
		Session session = new Session(portNum, ep, mBus, this);
		if (session.join() != Status.OK) {
		    return null;
		}
		// Save the session for later use.
        saveSession(session);
		return session;
	}

	/**
	 * Helper method to attempt to get introspection
	 * 
	 * handles both introspecting with local objects and 
	 * alljoyn distributed objects.
	 * 
	 * @param service Service to find object for
	 * @param obj Object path with in the service to find
	 * @return Introspectable Introspectable definition of the object found at service
	 * @throws TriumphException Exception occurred while getting introspection
	 */
	public String getInstrospection(EndPoint ep, String obj, short portNum) 
			throws TriumphException {

		/*
		 * Currently we are creating a session between remote objects and 
		 * this client.
		 * 
		 * Local objects do not require a session therefore we
		 * can just invocate whatever we want.
		 */

		// Attempt to find or create session with the service name.
		// This Signifies that we have come across an alljoyn
		// Try to receive a session with the service if we have already established one
		Session session = getSession(ep);
		if (session == null) {
			// Create a session if it does not exists
			session = createNewSession(ep, portNum);
			
		}
		// If we are unable to retrieve or create a session then it 
		// is a purely exceptional case
		if (session == null) {
			throw new TriumphException("Unable to establish session with service " 
					+ ep + " with port number " + portNum);
		}
	
		// Get the proxy bus object that can handle the interfaces we are looking for
		ProxyBusObject proxy = session.getProxy(obj, new Class<?>[] {Introspectable.class});
		
		// Attempt to get the instrospection
		// if the proxy is local.
		Introspectable ret = null;
		try {
			ret = proxy.getInterface(Introspectable.class);
		} catch (Exception e) {
			// The Proxy object cannot be cast to the correct type
			throw new TriumphException("Proxy object does not implement " + Introspectable.class.getSimpleName());
		}

		// 
		if (ret == null)
			throw new TriumphException("Error occured while attempting to contact " + ep);

		// Not a local object
		String intData = null;
		try {
			intData = ret.Introspect();
		} catch (BusException e) {

			// Attempt to extract the introspection data here
			try {
				intData = session.getIntrospection(obj);
			} catch (BusException e1) {
				throw new TriumphException("Unable to Introspect alljoyn bus object: " + e1.getMessage());
			}
		}

		if (intData == null) {
			throw new TriumphException("Unable to resolve introspection data");
		}
		return intData;
	}

	/**
	 * Node with tag of node or interface to register with the bus.
	 * @param node Node to register.
	 */
	public Status registerInterface(Node node) {
		InterfaceDescriptionBuilder builder = new XMLInterfaceDescriptionBuilder(node);
		return builder.build(mBus);
	}
	
	@Override
	public void destroy() {
		mBus.unregisterSignalHandlers(this);
		mBus.disconnect();
	}

	@Override
	public void sessionLost(String name, int sessionId) {
		// check if we currently are in a session with this service
		Session session = mSessions.get(name);
		if (session == null) return;

		// Remove the session 
		mSessions.remove(name);
	}
}
