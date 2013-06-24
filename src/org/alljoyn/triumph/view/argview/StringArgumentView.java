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

import org.alljoyn.triumph.model.components.arguments.Argument;

/**
 * 
 * @author mhotan
 */
public class StringArgumentView extends SimpleArgumentView<String> {

	public StringArgumentView(Argument<String> argument) {
		super(argument);
	}

	@Override
	protected boolean setArgument(String raw, StringBuffer errorBuffer) {
		errorBuffer.setLength(0);
		
		// Allow the null input
		if (raw != null && raw.equals(Argument.NULL)) {
			raw = null;
		}
		setValue(raw);
		return true;
	}

}
