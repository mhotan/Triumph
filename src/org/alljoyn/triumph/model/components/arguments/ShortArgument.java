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

/**
 * Argument class that represents a short argument.
 * 
 * @author mhotan@quicinc.com
 */
public class ShortArgument extends NumberArgument<Short> {

	/**
     * 
     */
    private static final long serialVersionUID = 8444279590324346584L;

    ShortArgument(String name, DIRECTION direction, boolean isUnsigned) {
        super(name, isUnsigned ? "" + AJConstant.ALLJOYN_UINT16 : "" 
                + AJConstant.ALLJOYN_INT16, direction, isUnsigned);
	}

	ShortArgument(org.w3c.dom.Node node, boolean isUnsigned, DIRECTION direction) {
		super(node, isUnsigned, direction);
	}

	@Override
	public String getSignature() {
		String sig = isUnsigned() ? "Unsigned " : "";
		return sig + "short " + getName();
	}

	@Override
	protected String getAJSignature() {
		if (isUnsigned())
			return "" + AJConstant.ALLJOYN_UINT16;
		else 
			return "" + AJConstant.ALLJOYN_INT16;
	}

}
