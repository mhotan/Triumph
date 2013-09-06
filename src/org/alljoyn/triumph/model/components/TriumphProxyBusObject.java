package org.alljoyn.triumph.model.components;

import org.alljoyn.bus.AnnotationBusException;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Status;

public class TriumphProxyBusObject extends ProxyBusObject {

    /**
     * Construct a Triumph related ProxyBusObject.
     *
     * @param busAttachment  The connection the remote object is on.
     * @param busName        Well-known or unique bus name of remote object.
     * @param objPath        Object path of remote object.
     * @param sessionId      The session ID corresponding to the connection to the object.
     * @param busInterfaces  A list of BusInterfaces that this proxy should respond to.
     */
    public TriumphProxyBusObject(BusAttachment busAttachment,
            String busName, String objPath, int sessionId, Class<?>[] busInterfaces) {
        super(busAttachment, busName, objPath, sessionId, busInterfaces);
    }

    /**
     * Construct a Triumph related ProxyBusObject.
     *
     * @param busAttachment  The connection the remote object is on.
     * @param busName        Well-known or unique bus name of remote object.
     * @param objPath        Object path of remote object.
     * @param sessionId      The session ID corresponding to the connection to the object.
     * @param busInterfaces  A list of BusInterfaces that this proxy should respond to.
     * @param secure         the security mode for the remote object
     */
    public TriumphProxyBusObject(BusAttachment busAttachment,
            String busName, String objPath, int sessionId, Class<?>[] busInterfaces, boolean secure) {
        super(busAttachment, busName, objPath, sessionId, busInterfaces, secure);
    }
    
    /** Called by native code to lazily add an interface when a proxy method is invoked. */
    protected int addInterface(String name) throws AnnotationBusException {
        return Status.OK.getErrorCode();
    }
}
