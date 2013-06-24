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
import org.alljoyn.triumph.view.argview.ArgumentView;
import org.alljoyn.triumph.view.argview.ByteArrayArgumentView;
import org.w3c.dom.Node;

/**
 * Argument that represents a Byte Array
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class ByteArrayArgument extends NumberArrayArgument<byte[]> {

	/**
	 * Creates an empty array argument.
	 * 
	 * @param name Name of this argument
	 * @param isInput Whether this argument is input or output argument
	 * @param elementSignature Signature of 
	 */
	ByteArrayArgument(String name, DIRECTION direction) {
		super(name, direction, "" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_BYTE);
	}
	
	ByteArrayArgument(Node node, DIRECTION direction) {
		super(node, direction);
	}

	@Override
	protected ArgumentView<byte[]> createJavaFXNode() {
		return new ByteArrayArgumentView(this); 
	}

	@Override
	protected String getAJSignature() {
		return "" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_BYTE;
	}
	
}
