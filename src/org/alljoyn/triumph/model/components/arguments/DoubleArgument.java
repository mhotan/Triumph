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
import org.alljoyn.triumph.view.argview.DoubleArgumentView;
import org.w3c.dom.Node;

public class DoubleArgument extends NumberArgument<Double> {

	DoubleArgument(String name, DIRECTION direction, boolean isUnsigned) {
		super(name, direction, isUnsigned);
	}
	
	DoubleArgument(Node node, boolean isUnsigned, DIRECTION direction) {
		super(node, isUnsigned, direction);
	}

	@Override
	protected ArgumentView<Double> createJavaFXNode() {
		return new DoubleArgumentView(this);
	}

	@Override
	public String getSignature() {
		return "double " + getName();
	}

	@Override
	protected String getAJSignature() {
		return "" + AJConstant.ALLJOYN_DOUBLE;
	}

}
