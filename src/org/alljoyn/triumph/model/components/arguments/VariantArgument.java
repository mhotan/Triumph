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

import org.alljoyn.bus.Variant;
import org.alljoyn.triumph.util.AJConstant;
import org.alljoyn.triumph.view.argview.ArgumentView;
import org.alljoyn.triumph.view.argview.VariantArgumentView;
import org.w3c.dom.Node;

/**
 * 
 * @author mhotan
 */
public class VariantArgument extends Argument<Variant> {

	VariantArgument(Node node, DIRECTION defaultDirection) {
		super(node, defaultDirection);
	}
	
	VariantArgument(String name, DIRECTION isInput) {
		super(name, isInput);
	}

	@Override
	protected ArgumentView<Variant> createJavaFXNode() {
		// TODO Auto-generated method stub
		return new VariantArgumentView(this);
	}

	@Override
	public String getSignature() {
		return "Variant " + getName();
	}

	@Override
	protected String getAJSignature() {
		return "" + AJConstant.ALLJOYN_VARIANT;
	}

}
