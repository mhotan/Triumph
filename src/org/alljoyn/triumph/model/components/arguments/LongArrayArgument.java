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
import org.alljoyn.triumph.view.argview.LongArrayArgumentView;
import org.w3c.dom.Node;

/**
 * Argument that represents a Integer Array, has the capability to support unsigned and signed arrays.
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class LongArrayArgument extends NumberArrayArgument<long[]> {

	private static final String SIGNED_SIGNATURE = "" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_INT64;
	private static final String UNSIGNED_SIGNATURE = "" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_UINT64;
	
	public LongArrayArgument(String name, DIRECTION direction, boolean isUnsigned) {
		super(name, direction, isUnsigned ? UNSIGNED_SIGNATURE : SIGNED_SIGNATURE);
	}

	public LongArrayArgument(Node node, DIRECTION defaultDir) {
		super(node, defaultDir);
	}

	@Override
	protected ArgumentView<long[]> createJavaFXNode() {
		return new LongArrayArgumentView(this);
	}

	@Override
	protected String getAJSignature() {
		if (isUnsigned())
			return "" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_UINT64;
		else 
			return "" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_INT64;
	}

}
