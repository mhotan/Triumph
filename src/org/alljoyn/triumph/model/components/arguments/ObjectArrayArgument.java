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
import org.alljoyn.triumph.view.argview.ObjectArrayArgumentView;
import org.w3c.dom.Node;

/**
 * Argument that represents a Object Array.  This is a generalized class that accepts all kinds
 * of non primitive arrays.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class ObjectArrayArgument extends ArrayArgument<Object[]> {

	private final String mAJSignature;
	
	public ObjectArrayArgument(String name, DIRECTION direction, String signature) {
		super(name, direction, signature);
		checkRep();
		mAJSignature = signature;
	}

	public ObjectArrayArgument(Node node, DIRECTION defaultDir) {
		super(node, defaultDir);
		checkRep();
		mAJSignature = "" + AJConstant.ALLJOYN_ARRAY + getInnerElementType();
	}

	@Override
	protected ArgumentView<Object[]> createJavaFXNode() {
		return new ObjectArrayArgumentView(this);
	}

	/**
	 * Representation checker
	 */
	private void checkRep() {
		String innerElem = getInnerElementType();
		char id = innerElem.charAt(0);
		if (id == AJConstant.ALLJOYN_BYTE 
				|| id == AJConstant.ALLJOYN_BOOLEAN
				|| id == AJConstant.ALLJOYN_INT16
				|| id == AJConstant.ALLJOYN_UINT16
				|| id == AJConstant.ALLJOYN_INT32
				|| id == AJConstant.ALLJOYN_UINT32 
				|| id == AJConstant.ALLJOYN_INT64 
				|| id == AJConstant.ALLJOYN_UINT64 
				|| id == AJConstant.ALLJOYN_DOUBLE)
			throw new IllegalStateException("Illegal array element type '" + innerElem + "' for Object arrays");

	}

	@Override
	protected String getAJSignature() {
		return mAJSignature;
	}
}
