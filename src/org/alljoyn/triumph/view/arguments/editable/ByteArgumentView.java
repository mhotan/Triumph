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

package org.alljoyn.triumph.view.arguments.editable;

import org.alljoyn.triumph.model.components.arguments.Argument;

/**
 * Argument View that represents a Byte Argument.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class ByteArgumentView extends SimpleArgumentView<Byte> {
	
	/**
	 * Creates a new Byte argument that represents a single byte
	 * @param argument Argument to assign to this view.
	 */
	public ByteArgumentView(Argument<Byte> argument) {
		super(argument);
	}

	@Override
	protected boolean setArgument(String raw, StringBuffer buffer) {
		buffer.setLength(0);
		
		// This should really never happen
		if (raw == null) {
			buffer.append("Can't have null input");
			return false;
		}
		
		try {
			// Attempt to parse the Byte value of the argument.
			Byte value = Byte.valueOf(raw);
			setValue(value);
			buffer.append(value);
			return true;
		} catch (NumberFormatException e) {
			// Unable to 
			buffer.append("Not a Byte");
			return false;
		}
	}
}
