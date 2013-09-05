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

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.EndPoint.SERVICE_TYPE;

/**
 * Class that represents the established session with this application
 * and the well known name advertised locally or distributively.  Therefore a specific session
 * is exclusive to a specific advertised well known name.
 * 
 * @author mhotan
 */
class BasicSessionImpl extends Session {

    /**
     * Session Port number for connecting to specific Alljoyn Bus endpoint
     */
    private final short mPortNum;

    /**
     * Session name to connect to.
     */
    private final EndPoint mEndPoint; 

    /**
     * Attempts to create a Session for name
     * with port number port.
     * 
     * @param port Applicaiton specific port number
     * @param ep Name of the Service or Bus well known name on the distributed bus to connect to.
     * @param bus BusAttachment to use to attach.
     */
    BasicSessionImpl(short port, EndPoint ep, BusAttachment bus, TriumphSessionListener listener) {
        super(bus, listener);
        if (ep == null)
            throw new IllegalArgumentException("Name is null");
        if (port == 0) 
            throw new IllegalArgumentException("Port cannot be 0");
        mPortNum = port;
        mEndPoint = ep;
    }

    @Override
    public EndPoint getEndPoint() {
        return mEndPoint;
    }

    @Override
    public Status disConnect() {
        if (getSessionId() == -1) {
            return Status.OK;
        }
        return mBus.leaveSession(getSessionId());
    }

    @Override
    protected int protectedConnect() {
        // Check if the client is a local session.  If so attempt to get the introspection
        // data and there fore be able to make following network calls.
        if (mEndPoint.getServiceType() == SERVICE_TYPE.LOCAL) {
            return 0;
        }

        // Join a session using the bus attachment.
        SessionOpts sessionOpts = new SessionOpts();
        Mutable.IntegerValue sessionId = new Mutable.IntegerValue();
        Status status = mBus.joinSession(mEndPoint.getName(), mPortNum, sessionId, sessionOpts, this);
        if (status != Status.OK) {
            LOG.warning("Unable to join session with " + mEndPoint.getName());
            return -1;
        }
        return sessionId.value;
    }
}
