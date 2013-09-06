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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import org.alljoyn.about.AboutService;
import org.alljoyn.about.AboutServiceImpl;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.AnnouncementHandler;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.controller.BusObserver.BusObserverListener;
import org.alljoyn.triumph.controller.Destroyable;
import org.alljoyn.triumph.model.SessionPortStorage;
import org.alljoyn.triumph.model.components.EndPoint;

/**
 * Class that manages the Sessions available to the application.
 * Maintains a background service 
 * 
 * @author mhotan
 */
public class SessionManager implements Destroyable, TriumphSessionListener, AnnouncementHandler {

    private static final Logger LOG = Logger.getLogger(SecurityManager.class.getSimpleName());

    /**
     * Number of seconds to wait between calls to flush.
     */
    private static final int SECONDS_PER_FLUSH = 3;

    /**
     * Mapping of sessions that currently exist between
     */
    private final Map<EndPoint, Session> mSessions;

    /**
     * Connection to virtual bus.
     */
    private final BusAttachment mBus;

    /**
     * Bus observer listener.
     */
    private final BusObserverListener mBusListener;

    /**
     * Collection of lost endpoints
     */
    private final Collection<String> mLostEPs;

    /**
     * About service used to create clients.
     */
    private final AboutService mAboutService;

    /**
     * Creates a Bus 
     * @param busAttachment
     * @param listener
     */
    public SessionManager(BusAttachment busAttachment, BusObserverListener listener) {
        mBusListener = listener;
        mSessions = new HashMap<EndPoint, Session>();

        if (!busAttachment.isConnected())
            throw new IllegalArgumentException("Bus attachment is not connected");

        // Create a bus attachment to handle the creation of sessions
        mBus = busAttachment;

        mLostEPs = new HashSet<String>();

        // Attempt to start about service
        mAboutService = getAboutService();

        // Start a timeline to flush out lost names.
        Timeline threeSecondCacheFlush = new Timeline(
                new KeyFrame(Duration.seconds(SECONDS_PER_FLUSH), new EventHandler<ActionEvent>(){

                    @Override
                    public void handle(ActionEvent event) {
                        // Flush the cache on the UI thread
                        flushLostEPs();
                    }

                }));
        threeSecondCacheFlush.setCycleCount(Timeline.INDEFINITE);
        threeSecondCacheFlush.play();
    }

    /**
     * gets a connected alljoyn session.
     * 
     * @param name Well known name (of service or endpoint) to connect to.
     * @param portNum Port number to connect to
     * @return Connected session on success, null on failure
     */
    public synchronized Session getSession(EndPoint ep) {
        Session session = mSessions.get(ep);
        if (session != null) {
            // Attempt to connect if not already connect.
            session.connect();
        }
        return session;
    } 

    /**
     * Saves the current session.  If session is null then
     * it is ignored.
     * 
     * @param session Session to save
     */
    private synchronized void saveSession(Session session) {
        if (mSessions.containsKey(session.getEndPoint())) return;
        mSessions.put(session.getEndPoint(), session);
    }

    /**
     * Save the about session.
     * 
     * @param session About session to save
     */
    private synchronized void saveSession(AboutSession session) {
        // Override any old version of the session.
        mSessions.put(session.getEndPoint(), session);
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
        BasicSessionImpl session = new BasicSessionImpl(portNum, ep, mBus, this);
        if (session.connect() != Status.OK) {
            // TODO Log unable to create session.
            return null;
        }
        // Save the session for later use.
        saveSession(session);
        return session;
    }

    @Override
    public void destroy() {

        // Disconnect all the sessions.
        for (Session s: mSessions.values())
            s.disConnect();

        mBus.unregisterSignalHandlers(this);
        mBus.disconnect();
    }

    @Override
    public void sessionLost(Session session, int sessionId) {
        // Remove the session 
        if (mSessions.remove(session.getEndPoint()) != null)
            session.disConnect();

        // Add lost endpoint
        addLostEP(session.getEndPointName());
    }

    private synchronized void addLostEP(String nameLost) {
        mLostEPs.add(nameLost);
    }

    private synchronized void flushLostEPs() {
        if (mBusListener == null || mLostEPs.isEmpty()) {
            return;
        }
        mBusListener.onNameLost(mLostEPs);
        mLostEPs.clear();
    }

    //////////////////////////////////////////////////////////////
    ////    Handle receiving and loosing About Service
    /////////////////////////////////////////////////////////////

    /**
     * Attempts to start and return about service
     * @return AboutService on success, null on failure.
     */
    private AboutService getAboutService() {
        try {
            AboutService service = AboutServiceImpl.getInstance();
            service.startAboutClient(mBus);
            service.addAnnouncementHandler(this);
            mBus.addMatch("sessionless='t',type='error'");
            return service;
        } catch (Exception e) {
            LOG.warning("Unable to start About service " + e);
            return null;
        }

    }

    @Override
    public void onAnnouncement(String uniqueName, short port, 
            BusObjectDescription[] objectDescriptions, Map<String, Variant> aboutData) {
        if (mAboutService == null) return;
        // AllJoyn thread.
        // Create an About Client and About session
        AboutSession session = new AboutSession(mAboutService, uniqueName, port, mBus, this);
        SessionPortStorage.savePort(uniqueName, port);
        
        // Create a thread to add About session 
        Thread t = new Thread(new AboutSessionSaver(session));
        t.start();
        saveSession(session);
    }

    @Override
    public void onDeviceLost(String serviceName) {
        addLostEP(serviceName);
    }

    /**
     * Class that is dedicated to.
     * @author Michael Hotan, mhotan@quicinc.com
     */
    private class AboutSessionSaver implements Runnable {

        private final AboutSession mSession;
        
        AboutSessionSaver(AboutSession session) {
            mSession = session;
        }
        
        @Override
        public void run() {
            if (mSession.connect() != Status.OK)
                return;
            
            EndPoint ep = mSession.getEndPoint();
            if (!ep.build(mSession)) {
                LOG.warning("Unable to build Endpoint " + ep.getName());
                return;
            }
            saveSession(mSession);
        }
    }
    
    private static class SessionBusListener extends BusListener {
        
        @Override
        public void nameOwnerChanged(
                String busName, String previousOwner, String newOwner) {
            super.nameOwnerChanged(busName, previousOwner, newOwner);
            if (newOwner == null) return; // Ignore null new owner names
        }
        
    }
    
}
