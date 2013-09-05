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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.Status;
import org.alljoyn.triumph.controller.Destroyable;
import org.alljoyn.triumph.model.components.SignalHandler.SignalListener;

/**
 * Class that manages the signal handler 
 * 
 * @author mhotan@quicinc.com, Michael Hotan 
 */
public class SignalHandlerManager implements Destroyable {

    private final Map<Signal, SignalHandler> mMap;
    
    private final BusAttachment mBus;
    
    private SignalListener mListener;
    
    private static final String SESSIONLESS_MATCH = "sessionless='t'";
    
    public SignalHandlerManager(BusAttachment bus) {
        if (bus == null)
            throw new NullPointerException(getClass().getSimpleName() + "<init>, Null BusAttachment");
        mBus = bus;
        mMap = new HashMap<Signal, SignalHandler>();
    }
    
    /**
     * Sets the single listener for all current Signal Handlers
     * @param list Listener to use.
     */
    public void setListener(SignalListener list) {
        mListener = list;
        for (SignalHandler handler: mMap.values()) {
            handler.setListener(mListener);
        }
    }
    
    /**
     * Remove the listener for all current signal handler.
     */
    public void removeListener() {
        mListener = null;
        for (SignalHandler handler: mMap.values()) {
            handler.removeListener();
        }
    }
    
    /**
     * Sets the sessionless state of this signal.  If sessionless is true, then 
     * the bus is set to receive sessionless signals.  If false then it will only receive
     * signals that have sessions.
     * 
     * @param sessionless flag to set bus with
     */
    public void setSessionlessSignal(boolean sessionless) {
        if (sessionless) {
            mBus.addMatch(SESSIONLESS_MATCH);
        } else {
            mBus.removeMatch(SESSIONLESS_MATCH);
        }
    }
    
    /**
     * Returns the signal handler for the given signal if it exists.
     * 
     * @param signal Signal to get 
     * @return null if not signal exists, Or Signal handler
     */
    public SignalHandler getSignalHandler(Signal signal) {
        return mMap.get(signal);
    }
    
    /**
     * Checks for a SignalHandler for this Signal.  That means there is
     * an active object that is listening for this signal.
     * 
     * @param signal Signal to check for
     * @return whether there exist a SignalHandler for this Signal
     */
    public boolean hasSignalHandler(Signal signal) {
        return mMap.get(signal) != null;
    }
    
    /**
     * Creates a handler for a specific signal.
     * 
     * @param signal Signal to create a handler for
     * @return Signal handler for this signal
     */
    public Status addSignalHandler(Signal signal) {
        if (mMap.containsKey(signal)) // we are already handling 
            return Status.OK;
        
        // create the handler add
        SignalHandler handler = new SignalHandler(signal);
        handler.setListener(mListener);
        
        // Create the interface name 
        String ifaceName = signal.getInterface().getName();
        String signalName = signal.getName();
        Status status = mBus.registerSignalHandler(ifaceName, signalName, handler, handler.getHandleMethod());
        
        // If we were able to register the Signal handler
        if (status == Status.OK) {
            mMap.put(signal, handler);
        }
        return status;
    }
    
    /**
     * Unregisters the signal handler for this signal. 
     * @param signal signal to stop listening for.
     */
    public void removeSignalHandler(Signal signal) {
        SignalHandler handler = getSignalHandler(signal);
        
        // If we dont have a handler then it is ok.
        if (handler == null) return; 
        
        // Unregister the handler and the method contained in it.
        mBus.unregisterSignalHandler(handler, handler.getHandleMethod());
        mMap.remove(signal);
    }

    @Override
    public void destroy() {
        Set<Signal> toRemove = new HashSet<Signal>();
        for (Signal signal : mMap.keySet()) 
            toRemove.add(signal);
        
        for (Signal signal : toRemove) 
            removeSignalHandler(signal);
    }
    
}
