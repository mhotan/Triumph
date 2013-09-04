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

package org.alljoyn.triumph.model.components;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.model.session.Session;

/**
 * A wrapper class that pertains to an endpoint or service
 * that can be seen by an established well known name.
 * 
 * @author Michael Hotan mhotan@quicinc.com 
 */
public class EndPoint extends AllJoynComponent {

    private static final Logger LOG = Logger.getLogger(EndPoint.class.getSimpleName());

    /**
     * A flag that labels whether this endpoint is built or not.
     */
    private boolean isBuilt;
    
    /**
     * List of Object that this endpoint contains.
     */
    private final List<AJObject> mObjects;

    public enum SERVICE_TYPE {
        LOCAL, REMOTE
    }

    private final SERVICE_TYPE mServiceType;

    /**
     * Creates an empty Service with no objects
     * with this name.  This creates the beginning of an endpoint
     * 
     * @param name Name of this service.
     */
    public EndPoint(String name, SERVICE_TYPE type) {
        this(name, null, type);
        isBuilt = false; // Is built but empty
    }

    /**
     * Create an AllJoyn service with name and nested objects. 
     * 
     * @param name Name of the service.
     * @param objects Objects contained in this service.
     */
    public EndPoint(String name, List<AJObject> objects, SERVICE_TYPE type) {
        super(TYPE.SERVICE);
        if (name == null || name.isEmpty()) 
            throw new IllegalArgumentException("AllJoynService, Illegal name: " + name);
        setName(name);

        // Assign the service type.
        mServiceType = type;

        // if the input is null it just creates a new empty list
        // of objects.  No big deal.... I think
        mObjects = objects == null ? new ArrayList<AJObject>() 
                : new ArrayList<AJObject>(objects);

        // If there are objects then this is built.
        isBuilt = objects != null;
    }

    /**
     * Attempts to build this endpoint and extract all of its objects.
     * 
     * @param manager Manager to use to build the endpoint
     * @return true on success, false on failure
     */
    public boolean build(Session session) {
        if (isBuilt) return true;
        try {
            
            // If fails throw exception
            ProxyBusObject proxy = session.getProxy("org/alljoyn/Bus/Peer");
            saveBusPeerProxy(proxy);

            TriumphAJParser parser = new TriumphAJParser(session);
            parser.parseIntrospectData();
            isBuilt = true;
        } catch (TriumphException e) {
            LOG.warning("Failed to build EndPoint " + getName() + " Exception: " + e.getMessage());
        }
        return isBuilt;
    }

    public SERVICE_TYPE getServiceType() {
        return mServiceType;
    }

    /**
     * Add all the objects to this service.
     * @param objects Object to add to this service.
     */
    public void addAll(List<AJObject> objects) {
        // Adds all the objects to this service.
        mObjects.addAll(objects);

        // Sets the owning service to this instance of this object
        for (AJObject obj: mObjects) {
            obj.setService(this);
        }
    }

    /**
     * Attempts to get the object that has the object
     * path inputted.
     * 
     * @param objectPath Complete Object path of the object in search for.
     * @return AllJoynObject with same object path if it exist, else returns null on none found.
     */
    public AJObject getObject(String objectPath) {
        for (AJObject object: mObjects) {
            if (object.getName().equals(objectPath))
                return object;
        }
        return null;
    }

    /**
     * @return Whether there exists any objects in this service
     */
    public boolean isEmpty() {
        return mObjects.isEmpty();
    }

    @Override
    public String getString() {
        return getName();
    }

    @Override
    public String toString() {
        return "Service: " + getName();
    }

    public List<AJObject> getObjects() {
        return new ArrayList<AJObject>(mObjects);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!o.getClass().equals(getClass())) return false;
        EndPoint s = (EndPoint) o;
        return s.getName().equals(getName()) && s.mServiceType == this.mServiceType;
    }

    @Override
    public int hashCode() {
        return getName().hashCode() + 11 * mServiceType.hashCode();
    }

    private ProxyBusObject mStandardProxy;

    /**
     * Saves Proxy bus object for standard object.
     * @param proxy Proxy to save
     */
    public void saveBusPeerProxy(ProxyBusObject proxy) {
        if (!proxy.getObjPath().equals("org/alljoyn/Bus/Peer")) {
            throw new IllegalArgumentException("saveBusPeerProxy() proxy object path not org/alljoyn/Bus/Peer");
        }
        mStandardProxy = proxy;
    }

    public ProxyBusObject getBusPeerProxy() {
        return mStandardProxy;
    }
}