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

import org.alljoyn.triumph.model.components.arguments.NumberArgument;
import org.alljoyn.triumph.view.argview.SimpleArgumentView;

public abstract class NumberArgumentView<T extends Number> extends SimpleArgumentView<T> {

	protected final NumberArgument<T> mNumArg;
	
	NumberArgumentView(NumberArgument<T> argument) {
		super(argument);
		mNumArg = (NumberArgument<T>) argument;
	}

}

