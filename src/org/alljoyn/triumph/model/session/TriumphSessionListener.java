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

package org.alljoyn.triumph.model.session;

/**
 * Listener that is notified when a specific session is losted.
 * @author mhotan
 */
public interface TriumphSessionListener {

	/**
	 * Notifies that the session has been lost and cannot be connected to
	 * 
	 * @param name Name of the session
	 * @param sessionId Session ID that was lost.
	 */
	public void sessionLost(Session name, int sessionId);
	
}
