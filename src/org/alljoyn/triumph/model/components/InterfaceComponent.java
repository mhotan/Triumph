package org.alljoyn.triumph.model.components;

import org.w3c.dom.Node;

/**
 * A standard class that represents a component 
 * @author mhotan
 */
public abstract class InterfaceComponent extends AllJoynComponent {

    /**
     * The owning interface.
     */
    private final Interface mInterface;
    
    /**
     * Creates empty interface component.
     * 
     * @param type Type of this component
     * @param iface Interface to have this component belong to.
     */
    protected InterfaceComponent(TYPE type, Interface iface) {
        super(type);
        mInterface = iface;
    }

    /**
     * Creates a Interface component that belongs to Interface identified with iface.
     * prefills component features with values from the node.
     * 
     * @param node Node node to use to prefill.
     * @param type Type of this component
     * @param iface Interface to have this component belong to.
     */
    protected InterfaceComponent(Node node, TYPE type, Interface iface) {
        super(node, type);
        mInterface = iface;
    }
    
    /**
     * @return returns the owning interface.
     */
    public Interface getInterface() {
        return mInterface;
    }
    
    public boolean belongsToEndPoint(String name) {
        if (name == null) return false;
        return mInterface.getObject().getOwner().getName().equals(name);
    }
    
    /**
     * Returns whether this interface component belongs to inputed Endpoint.
     * 
     * @param name Name to check with
     * @return true if this belongs to the inputed object.
     */
    public boolean belongsToEndPoint(EndPoint endpoint) {
        if (endpoint == null) return false;
        return belongsToEndPoint(endpoint.getName());
    }

    /**
     * Returns whether this interface component belongs to inputed object.
     * 
     * @param name Name to check with
     * @return true if this belongs to the inputed object.
     */
    public boolean belongsToObject(String name) {
        if (name == null) return false;
        return mInterface.getObject().getName().equals(name);
    }
    
    /**
     * Returns whether this interface component belongs to inputed object.
     * 
     * @param object AJObject to check with
     * @return true if this belongs to the inputed object.
     */
    public boolean belongsToObject(AJObject object) {
        if (object == null) return false;
        return belongsToObject(object.getName());
    }
    
    /**
     * Returns whether this interface component belongs to this interface.
     * 
     * @param name name of interface to check with
     * @return true if this belongs to the inputted iface.
     */
    public boolean belongsToInterface(String name) {
        if (name == null) return false;
        return mInterface.getName().equals(name);
    }
    
    /**
     * Returns whether this interface component belongs to this interface.
     * 
     * @param iface interface to check with
     * @return true if this belongs to the inputted iface.
     */
    public boolean belongsToInterface(Interface iface) {
        if (iface == null) return false;
        return belongsToInterface(iface.getName());
    }
}
