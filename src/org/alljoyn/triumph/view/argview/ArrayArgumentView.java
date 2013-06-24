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

package org.alljoyn.triumph.view.argview;

import org.alljoyn.triumph.model.components.arguments.ArrayArgument;

/**
 * JavaFX View that represents an Array Argument.
 * 
 * @author mhotan
 *
 * @param <T> Generic array type, like byte[], int[], or Object[]
 */
public abstract class ArrayArgumentView<T> extends MultiElementArgumentView<T> {

	private final ArrayArgument<T> mArrayArg;
	
	/**
	 * @param arg ArrayArgument to associate to.
	 */
	protected ArrayArgumentView(ArrayArgument<T> arg) {
		super(arg);
		mArrayArg = arg;
	}

	@Override
	protected boolean isEditable() {
		// Only allow the ability to add more elements
		// if it is a constructed 
		return isInputArg();
	}
	

}
