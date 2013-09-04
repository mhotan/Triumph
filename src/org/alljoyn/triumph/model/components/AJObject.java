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

import org.alljoyn.triumph.MainApplication;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A Quick description (non inclusive to the actual alljoyn object)
 * Version that includes an object and its interfaces.
 * 
 * @author mhotan
 */
public class AJObject extends AllJoynComponent {

    public static final String LABEL = "node";

    /**
     * Holds a list of all interfaces that this object implements
     */
    private final List<Interface> mInterfaces;

    /**
     * List of all the names (prefix excluded)
     * of the subjobjects of this object.
     */
    private final List<String> mSubObjects;

    /**
     * Reference to parent service.
     */
    private EndPoint mService;

    /**
     * Creates an all joyn object with the COMPLETE path
     * argument objectPath
     * @param objectPath Complete path name
     */
    public AJObject(String objectPath) {
        super(AllJoynComponent.TYPE.OBJECT);
        setName(objectPath);
        mInterfaces = new ArrayList<Interface>();
        mSubObjects = new ArrayList<String>();
        mService = null;
    }

    /**
     * Creates an alljoyn object from a DOM node of introspected data
     * @param node Node to parse
     */
    AJObject(Node node, String objectPath) {
        super(node, TYPE.OBJECT);
        mService = null;

        // Node is not an object
        if (!isObject(node))
            throw new IllegalArgumentException("Illegal node for " 
                    + getClass().getSimpleName() + " Constructor.  " +
                    "Node must have name \"" + LABEL + "\"");

        // Check for valid name.
        if (objectPath == null || objectPath.isEmpty()) {
            throw new IllegalArgumentException("AllJoynObject, Illegal name: " + objectPath);
        }
        setName(objectPath);

        mInterfaces = new ArrayList<Interface>();
        mSubObjects = new ArrayList<String>();

        NodeList children = node.getChildNodes();
        int numChildren = children.getLength();
        for (int i = 0; i < numChildren; ++i) {
            Node child = children.item(i);
            String name = TriumphAJParser.findName(child);

            if (name == null) {
                MainApplication.getLogger().warning("Null child name returned for object " + getName());
                continue;
            } 

            // if we have a potential object
            if (LABEL.equals(child.getNodeName())) {
                mSubObjects.add(name);
            } else if (Interface.isInterface(child)) {
                mInterfaces.add(new Interface(child, this));
            }
        }
    }

    /**
     * Returns a list of interfaces that this object supports,
     * @return List of interfaces this object supports
     */
    public List<Interface> getInterfaces() {
        return new ArrayList<Interface>(mInterfaces);
    }

    /**
     * Checks whether there is an interface with name such as the argument.
     * 
     * @param ifaceName Name of the interface to find
     * @return true if such an interface exists, false otherwise.
     */
    public boolean hasInterface(String ifaceName) {
        return getInterface(ifaceName) != null;
    }

    /**
     * Returns the interface with the name of the inputed argument.
     * 
     * @param ifaceName Interface name to find.
     * @return AllJoynInterface with associated name ifaceName. Null if no interface is found.
     */
    public Interface getInterface(String ifaceName) {
        if (ifaceName == null)
            return null;

        for (Interface iface: mInterfaces) {
            if (iface.getName().equals(ifaceName))
                return iface;
        }
        return null;
    }

    /**
     * @return Returns the owning service.
     */
    public EndPoint getOwner() {
        return mService;
    }

    /**
     * Sets the alljoyn service of this object.
     * 
     * @param owner Owner to assign
     */
    void setService(EndPoint owner) {
        if (mService != null && mService.equals(owner)) {
            return; // Already set correctly
        }

        /*
         * Hacky way of setting the service.
         * TODO: make it so the constructor of the object takes a service
         * to ensure single assignment
         */
        if (mService != null && !mService.equals(owner))
            throw new IllegalStateException("An object can only be assigned one Service as an owner.  " 
                    + "Current owner: " + mService + " Requested owner: " + owner);

        if (owner == null) {
            MainApplication.getLogger().warning("Attempted to assign null Service owner to object " + this);
            return;
        }
        mService = owner;
    }

    /**
     * Returns whether the node in question is an Alljoyn object
     * returned from XML parse of instrospected data.
     * @param node root of Potential object
     * @return if this object represents an Alljoyn object.
     */
    static boolean isObject(Node node) {
        // The node must be a non null node
        if (node == null || !LABEL.equals(node.getNodeName())) 
            return false;

        NodeList children = node.getChildNodes();
        int length = children.getLength();

        // A node with no children is still an object
        // Weird edge case though.
        if (length == 0) {
            return true;
        }

        // This an alljoyn object if it
        // implements an interface.
        boolean hasInterface = false;
        for (int i = 0; i < length; ++i) {
            hasInterface |= Interface.isInterface(children.item(i));
        }
        return hasInterface;
    }

    /**
     * Returns a list of potential sub objects.
     * <b>IE if this object path was /org
     * <b>then potentail sub objects would be 
     * <b>	/org/alljoyn
     * <b>	/org/foo
     * <b>	/org/bar
     * @return list of potential sub objects.
     */
    List<String> getSubObjects() {
        return new ArrayList<String>(mSubObjects);
    }

    @Override
    public String getString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!o.getClass().equals(getClass())) return false;
        AJObject s = (AJObject) o;
        return s.getName().equals(getName()) && s.getOwner().equals(getOwner());
    }

    @Override
    public int hashCode() {
        return getName().hashCode() * getOwner().hashCode();
    }

}
