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

package org.alljoyn.triumph.util;

import org.alljoyn.bus.BasicInterfaceDescriptionBuilder;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.InterfaceDescription;
import org.alljoyn.bus.InterfaceDescriptionBuilder;
import org.alljoyn.triumph.model.components.AllJoynInterface;

/**
 * A general parser to help extract an AllJoyn interface description
 * from A triumph interface.  Triumph interfaces are found through introspection
 *  and there is no InterfaceDescription that comes with the introspection.
 *  Therefore we have to create it ourselves.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public final class InterfaceDescriptionParser {

	/**
	 * Can't instantiate 
	 */
	private InterfaceDescriptionParser() {}
	
	/**
	 * Utility helper to translate a Triumph AllJoyn interface to an AllJoyn Interface
	 * that can be interpretted on the bus.
	 * 
	 * @param bus BusAttachment 
	 * @param iface
	 * @return
	 */
	public static InterfaceDescription parse(BusAttachment bus, AllJoynInterface iface) {
		
		// Initialize the Interface Description builder to associate to this interface
		InterfaceDescriptionBuilder builder = new BasicInterfaceDescriptionBuilder(iface.getName());
		
		return null;
	}
}
