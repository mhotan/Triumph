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

/**
 * A base class that represents an argument that represents a NumberArgument type.
 * 
 * @author mhotan
 *
 * @param <T> Number type associated with this argument.
 */
public abstract class NumberArgument<T extends Number> extends Argument<T> {

	/**
	 * Flag to show 
	 */
	private final boolean isUnsigned;
	
	/**
	 * Standard error representing number should be positive.
	 */
	protected static final String NEGATIVE_ERROR = "Error: Must be Non Negative";
	
	NumberArgument(String name, DIRECTION direction, boolean isUnsigned) {
		super(name, direction);
		this.isUnsigned = isUnsigned;
	}
	
	/**
	 * Creates a number argument
	 * 
	 * @param node 
	 */
	NumberArgument(org.w3c.dom.Node node, boolean isUnsigned, DIRECTION defaultDir) {
		super(node, defaultDir);
		this.isUnsigned = isUnsigned;
	}

	/**
	 * @return If value is Unsigned.
	 */
	public boolean isUnsigned() {
		return isUnsigned;
	}
}
