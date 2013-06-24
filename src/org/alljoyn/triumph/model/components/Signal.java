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

import org.alljoyn.triumph.model.components.arguments.Argument.DIRECTION;
import org.w3c.dom.Node;

public class Signal extends Member {

	public static final String SIGNAL_LABEL = "signal";

	/*
	 * These members are helper method for 
	 * JNI and retrieving the correct name.
	 */
	@SuppressWarnings("unused")
	private final String mInterfaceName, mObjectName;
	
	/**
	 * Node to create a signal object from
	 * 
	 * <b>This signal must be built from an XML and have 
	 * a representation such as...
	 *  
	 * <b><signal name="Changed">
	 * <b>	<arg name="new_value" type="b"/>
	 * <b><\/signal>
	 * 
	 * @param node Node to build signal from.
	 * @param iface owning interface
	 */
	Signal(Node node, AllJoynInterface iface) {
		super(node, iface, AllJoynComponent.TYPE.SIGNAL, DIRECTION.OUT);
		
		mInterfaceName = iface.getName();
		mObjectName = iface.getObject().getName();
		
		if (!isSignal(node))
			throw new IllegalArgumentException("Illegal node for Signal Constructor.  " +
					"Node must have name \"signal\"");
	}
	
	/**
	 * Returns whether this node is considered
	 * to represent a Signal.
	 * @param node Speculated Node
	 * @return whether node represents a signal or not.
	 */
	public static boolean isSignal(Node node) {
		String name = node.getNodeName();
		return name != null && name.equals(SIGNAL_LABEL);
	}

	@Override
	public String getString() {
		return this.toString();
	}

}
