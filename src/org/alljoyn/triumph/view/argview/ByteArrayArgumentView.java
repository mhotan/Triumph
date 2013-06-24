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

import java.util.List;

import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.model.components.arguments.ByteArgument;
import org.alljoyn.triumph.model.components.arguments.ByteArrayArgument;
import org.alljoyn.triumph.util.AJConstant;

/**
 * View that presents an array of bytes.
 * @author mhotan@quicinc.com
 */
public class ByteArrayArgumentView extends ArrayArgumentView<byte[]> {

	/**
	 * Creates an argument view.
	 * @param arg Byte argument to associate to this view.
	 */
	public ByteArrayArgumentView(ByteArrayArgument arg) {
		super(arg);
	}

	@Override
	protected ArgumentView<?> getBlankElement() {
		// The way this is designed this casting to byte argument
		// is the only unstable process.
		ByteArgument arg = (ByteArgument) ArgumentFactory.getArgument(
				"" + AJConstant.ALLJOYN_BYTE, "",  getArgDirection());
		ByteArgumentView view = new ByteArgumentView(arg);
		view.hideSaveOption();
		return view;
	}

	@Override
	public byte[] getCurrentElements(StringBuffer buf) {
		List<ArgumentView<?>> currentViews = getArgViews();
		byte[] array = new byte[currentViews.size()];
		
		// For every one of the views cast it into a byte argument to extract the value.
		for (int i = 0; i < currentViews.size(); ++i) {
			
			// Cast down to the correct argument view.
			ByteArgumentView bView = (ByteArgumentView) currentViews.get(i);
			
			// Extract the value if it exists.
			Byte val =  bView.getValue();
			
			// if it doesn't exists notify the error.
			if (val == null) {
				if (buf != null)
					buf.append("Null value at index " + i);
				return null;
			}
			
			array[i] = val;
		}
		return array;
	}
}
