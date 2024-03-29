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

import org.alljoyn.triumph.model.components.EndPoint;

/**
 * Listener to listen for the selection of EndPoints.
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public interface EndPointListener {

    /**
     * Notifies clients that EndPoint was selected 
     * 
     * @param ep EndPoint that was selected;
     */
    public void onEndPointSelected(EndPoint ep);
 
    /**
     * Notifies any clients that any view related to this endpoint should be removed.
     * 
     * @param eps EndPoints that relate to view to be removed.
     */
    public void onEndPointRemoved(EndPoint ep);
    
}
