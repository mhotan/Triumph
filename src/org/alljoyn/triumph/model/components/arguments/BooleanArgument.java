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
import org.alljoyn.triumph.view.argview.BooleanArgumentView;
import org.w3c.dom.Node;

/**
 * An argument that represent the an argument that is a single boolean.
 * @author mhotan
 */
public class BooleanArgument extends Argument<Boolean> {

	/**
	 * 
	 * @param name
	 * @param direction
	 */
	BooleanArgument(String name, DIRECTION direction) {
		super(name, direction);
	}

	/**
	 * 
	 * @param node Node that contains data about 
	 * @param defaultDirection default direction if not specified in introspection data
	 */
	BooleanArgument(Node node, DIRECTION defaultDirection) {
		super(node, defaultDirection);
	}

	@Override
	public ArgumentView<Boolean> createJavaFXNode() {
		return new BooleanArgumentView(this);
	}

	@Override
	public String getSignature() {
		return Boolean.class.getSimpleName() + " " + getName();
	}

	@Override
	protected String getAJSignature() {
		return "" + AJConstant.ALLJOYN_BOOLEAN;
	}

}
