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

import java.text.DateFormat;
import java.util.Date;

import org.alljoyn.triumph.model.components.arguments.Argument;

/**
 * Wrapper class that encapsulates the reception of a signal and all the
 * relevant facts of the received signal.
 * 
 * @author mhotan
 */
public class SignalContext {

    private final Signal mSignal;
    
    private final Argument<?>[] mArgs;
    
    private final Date mTimeRecieved;
    
    private String mDescription;
    
    /**
     * Creates a signal context using the current time as the time received.
     * 
     * @param s Signal to assign to this context
     * @param args Arguments that correspond to the values received in this signal.
     */
    public SignalContext(Signal s, Argument<?>[] args) {
        this(s, args, new Date());
    }
    
    /**
     * Creates a signal context using the specified time 
     * @param s Signal to assign to this context
     * @param args Arguments that correspond to the values received in this signal.
     * @param timeRecieved Time received
     */
    public SignalContext(Signal s, Argument<?>[] args, Date timeRecieved) {
        mSignal = s;
        mArgs = new Argument<?>[args.length];
        for (int i = 0; i < args.length; ++i)
            mArgs[i] = args[i];
        mTimeRecieved = timeRecieved;
        
        StringBuffer buf = new StringBuffer();
        buf.append(DateFormat.getDateInstance(DateFormat.FULL).format(mTimeRecieved));
        buf.append(" Source: ");
        buf.append(mSignal.getInterface().getObject().getName());
        buf.append(" Data received: ");
        for (Argument<?> arg: mArgs) {
            buf.append(arg.getValue());
            buf.append(" ");
        }
        
        mDescription = buf.toString().trim();
    }
    
    public Signal getSignal() {
        return mSignal;
    }
    
    public Argument<?>[] getArgs() {
        return mArgs;
    }
    
    public Date getTimeReceived() {
        return mTimeRecieved;
    }
    
    public String getDescription() {
        return mDescription;
    }
}
