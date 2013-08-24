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

public class SignatureArgument extends StringArgument {

	/**
     * Serialization ID for persisting objects
     */
    private static final long serialVersionUID = 1201865963115602658L;

    SignatureArgument(String name, DIRECTION dir) {
		super(name, dir);
	}
	
	SignatureArgument(Node node, DIRECTION defaultDir) {
		super(node, defaultDir);
	}
	
	@Override
	public String getSignature() {
		return "Signature " + getName();
	}

	@Override
	protected String getAJSignature() {
		return "" + AJConstant.ALLJOYN_SIGNATURE;
	}

}
