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

package org.alljoyn.triumph.controller.session;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.InterfaceDescriptionBuilder;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.XMLInterfaceDescriptionBuilder;
import org.alljoyn.bus.ifaces.Introspectable;
import org.alljoyn.bus.ifaces.Peer;
import org.alljoyn.bus.ifaces.Properties;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.TriumphProxyBusObject;
import org.w3c.dom.Node;

/**
 * Base class that represents a session created via alljoyn.
 * This class is meant to be inherited and represetnt the various 
 * different ways that a session can be created.
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public abstract class Session extends SessionListener {

    protected static final Logger LOG = Logger.getLogger(Session.class.getSimpleName());
    
    /**
     * Bus Attachment
     */
    protected final BusAttachment mBus;
    
    /**
     * Listener for sending session lost message.
     */
    private final TriumphSessionListener mListener;
    
    /**
     * Mapping of known proxy objects.
     */
    private final Map<String, ProxyBusObject> mProxies;
    
    /**
     * Session ID to associate to this session.
     */
    private int mSessionId;
    
    /**
     * Boolean flag that maintains the connection status.
     */
    private boolean mIsConnected;
    
    /**
     * Hold an array of standard interfaces that we know all 
     * advertised bus object and bus attachments implement
     */
    private static final Class<?>[] STANDARD_INTERFACES = {Introspectable.class, Peer.class, Properties.class};

    
    /**
     * Creates a basic session associating the bus attachment and 
     * Session listener listening for sending messages back when session is lost.
     * 
     * @param bus BusAttachment to associate to this session
     * @param listener Listener to send message when session is lost.
     */
    protected Session(BusAttachment bus, TriumphSessionListener listener) {
        if (bus == null)
            throw new NullPointerException("Null BusAttachment");
        mBus = bus;
        mListener = listener;
        mProxies = new HashMap<String, ProxyBusObject>();
        mSessionId = -1;
        mIsConnected = false;
    }
    
    /**
     * @return the session ID, -1 if not connected.
     */
    public int getSessionId() {
        return mSessionId;
    }
    
    /**
     * Returns the endpoint associated with this session.
     * <br> Never returns null
     * @return returns EndPoint associated with this session.
     */
    public abstract EndPoint getEndPoint();
    
    /**
     * @return The port used to establish a connection.
     */
    public abstract int getPort();
    
    /**
     * A quick access method to get teh EndPoint Name.
     * 
     * @return the EndPoint name associate to this service
     */
    public String getEndPointName() {
        return getEndPoint().getName();
    }
    
    /**
     * Registers the interface based on the XML DOM node that represents the 
     * description of the interface.
     * 
     * @param node Node of XML interface to register.
     */
    public Status registerInterface(Node node) {
        InterfaceDescriptionBuilder builder = new XMLInterfaceDescriptionBuilder(node);
        return builder.build(mBus);
    }
    
    @Override
    public void sessionLost(int sessionId, int reason) {
        super.sessionLost(sessionId, reason);
        mIsConnected = false;
        if (mListener == null) return;
        mListener.sessionLost(this, sessionId);
    }
    
    /**
     * Attempt to disconnect the service.
     * @return Status of disconnection.
     */
    public abstract Status disConnect();
    
    /**
     * Attempt to connect to the endpoint.
     * 
     * @return true if connection was successful, false failed to connect. 
     */
    public Status connect() {
        if (isConnected()) return Status.OK;
        
        int sessionId = protectedConnect();
        if (sessionId != -1) {
            setSessionId(sessionId);
            mIsConnected = true;
            return Status.OK;
        }
        mIsConnected = false;
        return Status.ALLJOYN_JOINSESSION_REPLY_CONNECT_FAILED;
    }

    /**
     * Check whether this session is connected.
     * @return Whether this session is connected or not.
     */
    public boolean isConnected() {
        return mIsConnected;
    }
    
    /**
     * Attempts to connect this session to its remote endpoint
     * @return -1 on failure else return the session ID;
     */
    protected abstract int protectedConnect();
    
    /**
     * Sets the session id for this session
     * @param id ID to associate to this session
     */
    private void setSessionId(int id) {
        mSessionId = id;
    }
    
    /**
     * Saves the proxy bus object associated with a specific object path. 
     * 
     * @param objectPath Object path to associate to proxy bus object
     * @param proxy Proxy object to save for later use.
     */
    private void addProxy(String objectPath, ProxyBusObject proxy) {
        if (objectPath == null || proxy == null) return;
        mProxies.put(objectPath, proxy);
    }
    
    /**
     * Returns a proxy bus object for the Object identified and the object path
     * and at the pre specified EndPoint
     * 
     * @param objectPath Object path of the proxybusobject to obtain
     * @return ProxyBusObject on success, null otherwise
     */
    public ProxyBusObject getProxy(String objectPath) {
        return getProxy(objectPath, STANDARD_INTERFACES);
    }
    
    /**
     * Returns a proxy object for the Object found under this well known name.
     * The proxy object returned implements all interfaces listed in classes
     * 
     * @param objPath Object path within the service.
     * @param ifaceClasses Interface .class files
     * @return ProxyBusObject for the following class
     */
    private ProxyBusObject getProxy(String objPath, Class<?>[] ifaceClasses) {
        // Check if we had a proxy object already established
        ProxyBusObject proxy = mProxies.get(objPath);

        // Create a new proxy bus object based 
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
            proxy = new TriumphProxyBusObject(mBus,
                    getEndPointName(), 
                    objPath, 
                    getSessionId(), 
                    ifaceClasses);
            addProxy(objPath, proxy);
        }
        return proxy;
    }
}
