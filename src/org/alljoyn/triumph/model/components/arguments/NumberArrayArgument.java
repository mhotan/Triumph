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

package org.alljoyn.triumph.model.components.arguments;

import org.alljoyn.triumph.util.AJConstant;
import org.w3c.dom.Node;

/**
 * An Argument that represents a Number Array Argument.  Any type that subclasses this
 * array should be able to support an Number Array type.
 * @author mhotan
 *
 * @param <NUMBER_ARRAY_TYPE> primitive array type of the number.
 */
public abstract class NumberArrayArgument<NUMBER_ARRAY_TYPE> extends ArrayArgument<NUMBER_ARRAY_TYPE> {

	protected final boolean isUnsigned;
	
	/**
	 * Create a number argument using the signature defined as an argument.
	 * Therefore the signature must be exactly 2 characters long and precede with
	 * AJCONSTANT.ALLJOYN_ARRAY and concatonate with an Alljoyn number type.
	 * 
	 * @param name Name of the argument
	 * @param isInput Whether this argument is an input
	 * @param signature Complete singature IE 'ai'
	 */
	NumberArrayArgument(String name, DIRECTION direction, String signature) {
		super(name, direction, signature);
		
		// extract the innertype of the array.
		String elementType = getInnerElementType();
		if (elementType.length() != 1)
			throw new IllegalArgumentException(
					"Invalid type signature for NumberArrayArgument '" + elementType + "'");
	
		char id = elementType.charAt(0);
		isUnsigned = id == AJConstant.ALLJOYN_UINT16 
				|| id == AJConstant.ALLJOYN_UINT32 
				|| id == AJConstant.ALLJOYN_UINT64;
	}
	
	/**
	 * Creates a node that is an array.
	 * @param node Node that represents an array.
	 */
	NumberArrayArgument(Node node, DIRECTION defaultDirection) {
		super(node, defaultDirection);
		
		String elementType = getInnerElementType();
		if (elementType.length() != 1)
			throw new IllegalArgumentException(
					"Invalid type signature for NumberArrayArgument '" + elementType + "'");
	
		char id = elementType.charAt(0);
		isUnsigned = id == AJConstant.ALLJOYN_UINT16 
				|| id == AJConstant.ALLJOYN_UINT32 
				|| id == AJConstant.ALLJOYN_UINT64;
	}
	
	/**
	 * @return whether this argument is unsigned
	 */
	public boolean isUnsigned() {
		return isUnsigned;
	}

}
