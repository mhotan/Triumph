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

/**
 * 
 * NOTE: I know its another wrapper around a very common 
 * data type, the "method".  But this Method is strictly specific
 * for parsing Introspect and eventual invoking.
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class Method extends Member {

	private static final String LABEL = "method";
	
	
	/*
	 * These members are helper method for 
	 * JNI and retrieving the correct name.
	 */
	@SuppressWarnings("unused")
	private final String mInterfaceName, mObjectName;
	
	@Override
	public String getString() {
		// Now we are doing the easy less writing route.
		return this.toString();
	}

	/**
	 * Creates a method instance that is able to parse a node
	 * that represents a method
	 * 
	 * node must have a name "method"
	 * 
	 * @param node node that represents the method.
	 */
	Method(Node node, Interface iface) {
		super(node, iface, TYPE.METHOD, DIRECTION.IN);
		
		mInterfaceName = iface.getName();
		mObjectName = iface.getObject().getName();
		
		// Method must be of type method
		if (!isMethod(node)) 
			throw new IllegalArgumentException("Illegal node for " 
					+ getClass().getSimpleName() + " Constructor.  " +
					"Node must have name \"" + LABEL + "\"");
	}
	
	/**
	 * Returns whether this node is considered
	 * to represents a method.
	 * @param node Speculated Node
	 * @return whether node represents a signal or not.
	 */
	public static boolean isMethod(Node node) {
		String name = node.getNodeName();
		return name != null && name.equals(LABEL);
	}
}
