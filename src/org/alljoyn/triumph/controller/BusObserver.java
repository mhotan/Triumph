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

package org.alljoyn.triumph.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.ifaces.DBusProxyObj;


/**
 * A "client" end point that attempts to extract all the
 * services available on the bus.  It will attempt to get the introspection
 * of all Bus Attachments on the alljoyn bus.
 * 
 * Current Implementation is a Singleton Pattern because we
 * just would like one controlling mechanism to observe and keep track of the last pattern
 * 
 * 
 * TODO: Will attempt to grab all exposed Well Known Names.  What about bus attachments that don't
 * have an object that hasn't exposed a well known name.
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class BusObserver implements Destroyable {

    private static final Logger LOG = Logger.getLogger(BusObserver.class.getSimpleName());

    /**
     * Attempt to request any name that is visible on the bus.
     */
    private static final String WILDCARD_PREFIX = "";

    /**
     * Number of seconds to wait between calls to flush.
     */
    private static final int SECONDS_PER_FLUSH = 3;

    /**
     * Connection to virtual bus.
     */
    private final BusAttachment mBus;

    /**
     * Triumph Listener  
     */
    private final TriumphBusListener mBusListener;

    public BusObserver(final BusAttachment bus, final BusObserverListener list) {
        this(bus, list, false);
    }

    /**
     * Initializes the Bus Observer.  Effectively 
     * does all the pre-processing work to allow this
     * instance to be able to start looking at the bus.
     * 
     * @param list Listener to keep track of 
     * @param dbusCompatible Set if Dbus names are desired.
     */
    public BusObserver(final BusAttachment bus, final BusObserverListener list, boolean dbusCompatible) {

        // Create a new context to start your bus attachment instance.
        mBus = bus;
        if (dbusCompatible)
            mBusListener = new TriumphBusListener(list, mBus.getDBusProxyObj());
        else 
            mBusListener = new TriumphBusListener(list, null);	
        mBus.registerBusListener(mBusListener);

        // Connect Bus attachment to the Virtual Distributed Bus
        attemptConnect();

        // Attempt to find all the the advertised bus names on
        // the distributed bus network.
        
        Status status = mBus.findAdvertisedName(WILDCARD_PREFIX);
        if (status != org.alljoyn.bus.Status.OK) {
            LOG.severe("Unable to initiate call to query to find all names");
            return;
        }
        LOG.info("Bus Observer Initialized");
    }

    /**
     * Attempt to connect
     * @return
     */
    private  boolean attemptConnect() {
        if (mBus.isConnected())
            return true;
        // Connect Bus attachment to the Virtual Distributed Bus
        org.alljoyn.bus.Status status = mBus.connect();
        if (status != org.alljoyn.bus.Status.OK) {
            LOG.log(Level.SEVERE, "SessionManager, Unable to connect to bus");
            return false;
        }
        return true;
    }

    /**
     * This method causes the synchronization of any pending buffers
     */
    public void sync() {
        // Here is where we would flush any cache that is pending some names
        mBusListener.flush();
    }

    @Override
    public void destroy() {
        mBus.unregisterSignalHandlers(this);
        mBus.disconnect();
    }

    /**
     * Interface that allows any class interested in discovery new
     * name on the alljoyn bus.
     * @author mhotan
     */
    public interface BusObserverListener {

        /**
         * Found a new well known name on the local
         * distributed bus. 
         * 
         * Note: as of right now it is picking up any Alljoyn services that
         * advertises itself.
         * 
         * @param name name that has not been previously seen.
         */
        void onDistributedNameFound(Collection<String> name);

        /**
         * Notifies the listener a local DBus service has 
         * been identified.
         * 
         * @param name Name of the service.
         */
        void onLocalNameFound(Collection<String> name);

        /**
         * Name that we were previously tracking was losted.
         * @param name Name that was lost 
         */
        void onNameLost(Collection<String> name);	
    }

    /**
     * Class that is dedicated to interpret found and lost advertised names 
     * and pass back to all listener
     * @author Michael Hotan, mhotan@quicinc.com
     */
    private static class TriumphBusListener extends BusListener {

        //		private long lastFlushTime;
        //		
        /**
         * two Buffers we use to keep track of 
         * all the names we find and loose
         */
        private final Set<String> mNameFoundBuffer, mNameLostBuffer;

        /**
         * Single instance of an application specific listener
         * that just cares about incoming available names.
         */
        private final BusObserverListener mListener;

        /**
         * DBus proxy object to handle getting Dbus names
         */
        private final DBusProxyObj mDBusProxy;

        /**
         * Creates an alljoyn specific listener for managing name discovery
         * 
         * @param list Listener that will receive found and lost name callbacks 
         */
        public TriumphBusListener(BusObserverListener list, DBusProxyObj dbusproxy) {
            assert list != null: "TriumphBusListener, Illlegal Null listener on creation!";
            this.mListener = list;
            mNameFoundBuffer = new HashSet<String>();
            mNameLostBuffer = new HashSet<String>();

            mDBusProxy = dbusproxy;

            Timeline threeSecondCacheFlush = new Timeline(
                    new KeyFrame(Duration.seconds(SECONDS_PER_FLUSH), new EventHandler<ActionEvent>(){

                        @Override
                        public void handle(ActionEvent event) {
                            // Flush the cache on the UI thread
                            flush();
                        }

                    }));
            threeSecondCacheFlush.setCycleCount(Timeline.INDEFINITE);
            threeSecondCacheFlush.play();
        }

        @Override
        public void nameOwnerChanged(
                String busName, String previousOwner, String newOwner) {
            super.nameOwnerChanged(busName, previousOwner, newOwner);
            // What is found is that when the new owner is found and the new owner is null,
            // that signifies that the name is lost.
            // Ignore any unique names.
            if (newOwner == null && !busName.startsWith(":")) {
                removeAdvertisedName(busName);
            }
        }

        @Override
        public synchronized void foundAdvertisedName(
                String name, short transport, String namePrefix) {
            if (name == null || name.length() == 0) {
                LOG.warning("Invalid advertised found name: " + name);
                return;
            }
            List<String> l = new ArrayList<String>();
            l.add(name);

            // throw the found name onto the buffer
            mNameFoundBuffer.add(name);
        }

        @Override
        public synchronized void lostAdvertisedName(
                String name, short transport, String namePrefix) {

            if (name == null || name.length() == 0) {
                // TODO Log invalid name
                return;
            }
            List<String> l = new ArrayList<String>();
            l.add(name);

            removeAdvertisedName(name);
        }

        private void removeAdvertisedName(String name) {
            // Make sure we remove the name if it is stored in the
            // buffer then throw it on the lost buffer to ensure 
            // that we notify the buffer is lost.
            mNameFoundBuffer.remove(name);
            mNameLostBuffer.add(name);
        }

        /**
         * Call to flush the current buffer of found and lost buffers.
         * 
         * JavaFX Thread
         */
        public synchronized void flush() {
            //			slastFlushTime = System.currentTimeMillis();
            List<String> distFound = new ArrayList<String>(mNameFoundBuffer);
            List<String> lost = new ArrayList<String>(mNameLostBuffer);
            mNameFoundBuffer.clear();
            mNameLostBuffer.clear();

            try {
                List<String> localFound = new ArrayList<String>();
                if (mDBusProxy != null) {
                    for (String name: mDBusProxy.ListNames()) {
                        if (!name.startsWith(":")) {
                            localFound.add(name);
                        }
                    }
                }
                mListener.onLocalNameFound(localFound);
            } catch (BusException e) {
                //				e.printStackTrace();
                LOG.warning("Dbus Proxy caught exception " + e);
            }

            // Sort the names
            Collections.sort(distFound);
            mListener.onDistributedNameFound(distFound);
            mListener.onNameLost(lost);
        }
    }


}
