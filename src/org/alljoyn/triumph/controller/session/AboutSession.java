package org.alljoyn.triumph.controller.session;

import org.alljoyn.about.AboutService;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.Status;
import org.alljoyn.services.common.ServiceAvailabilityListener;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.EndPoint.SERVICE_TYPE;

/**
 * A session that that is created from an About Client that has
 * found an about service.
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
class AboutSession extends Session implements ServiceAvailabilityListener {

    /**
     * The EndPoint associate with this session.
     */
    private final EndPoint mEndPoint;
    
    /**
     * About Service to use to connect and create client.
     */
    private final AboutService mAboutService;
    
    /**
     * Port number to use.
     */
    private final short mPort;
    
    /**
     * Mutable client reference.
     * if mClient != null, then it is connected,
     * if null then this session is not connected.
     */
    private AboutClient mClient;
    
    /**
     * Creates the about session using About service
     * 
     * @param service AboutService that is used to create AboutClient
     * @param endPointName String peer/service name to connect to
     * @param port Port to connect with.
     * @param bus BusAttachment to use
     * @param listener Listener to list for connection lost.
     */
    AboutSession(AboutService service, 
            String endPointName, short port, 
            BusAttachment bus, TriumphSessionListener listener) {
        super(bus, listener);
        mAboutService = service;
        mPort = port;
        // The Client reveals the 
        mEndPoint = new EndPoint(endPointName, SERVICE_TYPE.REMOTE);
    }

    @Override
    public EndPoint getEndPoint() {
        return mEndPoint;
    }

    @Override
    public Status disConnect() {
        if (mClient == null) return Status.ALLJOYN_LEAVESESSION_REPLY_NO_SESSION;
        mClient.disconnect();
        return Status.OK;
    }

    @Override
    protected int protectedConnect() {
        try {
            // use the about service to connect to the about client.
            mClient = mAboutService.createAboutClient(mEndPoint.getName(), this, mPort);
            return mClient.getSessionId();
        } catch (Exception e) {
            LOG.warning("Unable to connect to the About remote end " + mEndPoint.getName());
        }
        return -1;
    }

    @Override
    public void connectionLost() {
        sessionLost(getSessionId(), SessionListener.ALLJOYN_SESSIONLOST_INVALID);
    }
    
}
