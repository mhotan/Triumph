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
import org.alljoyn.triumph.view.argview.StringArgumentView;

/**
 * Argument that represents a string.
 * 
 * @author mhotan
 */
public class StringArgument extends Argument<String> {
	
	StringArgument(String name, DIRECTION direction) {
		super(name, direction);
	}
	
	/**
	 * String argument Constructor
	 * 
	 * @param node Node that represents an argument with a String Variable.
	 */
	StringArgument(org.w3c.dom.Node node, DIRECTION defaultDir) {
		super(node, defaultDir);
	}

	@Override
	protected ArgumentView<String> createJavaFXNode() {
		return new StringArgumentView(this);
	}

	@Override
	public String getSignature() {
		return String.class.getSimpleName() + " " + getName();
	}

	@Override
	protected String getAJSignature() {
		return "" + AJConstant.ALLJOYN_STRING;
	}

}
