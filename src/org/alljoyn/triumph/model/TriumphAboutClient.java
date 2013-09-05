package org.alljoyn.triumph.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.alljoyn.about.AboutService;
import org.alljoyn.about.AboutServiceImpl;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.AnnouncementHandler;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.services.common.ServiceAvailabilityListener;

/**
 * Client that handles the incoming messages from an about service.
 * This helps resolve about names. 
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class TriumphAboutClient implements Destroyable {

    private static final Logger LOG = Logger.getLogger(TriumphAboutClient.class.getSimpleName());
    
    private final BusAttachment mBus;

    private final List<AboutClientWrapper> mClients;

    private final AboutService mAboutClient;

    /**
     * Create a Triumph About client.
     * 
     * @param bus Bus to creat client with
     * @throws Exception Unable to start about client
     */
    public TriumphAboutClient(BusAttachment bus) throws Exception {
        mBus = bus;
        if (!mBus.isConnected())
            throw new IllegalStateException("Bus is not connected.");

        // Get the default about service instance
        mAboutClient = AboutServiceImpl.getInstance();
        mAboutClient.startAboutClient(mBus);

        // Create the announcement handler and register it with the service
        AnnouncementHandler announceHandler = new TriumphAnnouncementHandler();
        mAboutClient.addAnnouncementHandler(announceHandler);
        mBus.addMatch("sessionless='t',type='error'");

        mClients = new ArrayList<TriumphAboutClient.AboutClientWrapper>();
    }

    private class TriumphAnnouncementHandler implements AnnouncementHandler {

        @Override
        public void onAnnouncement(String serviceName, short port, 
                BusObjectDescription[] objectDescriptions, Map<String, Variant> aboutData) {
           try {
            addClient(new AboutClientWrapper(serviceName, port, aboutData));
        } catch (Exception e) {
            LOG.warning("Unable to create About Client for " + serviceName);
        }
        }

        @Override
        public void onDeviceLost(String serviceName) {
            removeClient(getClient(serviceName));
        }

    }
    
    private synchronized void addClient(AboutClientWrapper client) {
        if (client == null) return;
        mClients.add(client);
    }
    
    private synchronized boolean removeClient(AboutClientWrapper client) {
        return mClients.remove(client);
    }
    
    private synchronized AboutClientWrapper getClient(String name) {
        for (AboutClientWrapper wrapper: mClients) {
            if (wrapper.getClient().getPeerName().equals(name))
                return wrapper;
        }
        return null;
    }

    /**
     * 
     * @author Michael Hotan, mhotan@quicinc.com
     */
    public class AboutClientWrapper implements ServiceAvailabilityListener {

        private final AboutClient mClient;

        private final Map<String, Variant> aboutData;

        private final short mPort;
        
        public AboutClientWrapper(String serviceName, short port, Map<String, Variant> data) throws Exception {
            mClient = mAboutClient.createAboutClient(serviceName, this, port);
            aboutData = data;
            mPort = port;
        }
        
        public AboutClient getClient() {
            return mClient;
        }
 
        public Map<String, Variant> getAboutData() {
            return aboutData;
        }
        
        public short getPort() {
            return mPort;
        }
        
        @Override
        public void connectionLost() {
            removeClient(this);
        }
    }

    @Override
    public void destroy() {
        try {
            mAboutClient.stopAboutClient();
        } catch (Exception e) {
            // We tried so do nothing.
        }
    }

}
