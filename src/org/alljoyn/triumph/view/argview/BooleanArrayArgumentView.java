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
import org.alljoyn.triumph.model.components.arguments.BooleanArgument;
import org.alljoyn.triumph.model.components.arguments.BooleanArrayArgument;
import org.alljoyn.triumph.util.AJConstant;

/**
 * View that presents an array of boolean values.
 * @author mhotan@quicinc.com
 */
public class BooleanArrayArgumentView extends ArrayArgumentView<boolean[]> {

	/**
	 * Creates a view that presents a Boolean Array argument
	 * @param arg 
	 */
	public BooleanArrayArgumentView(BooleanArrayArgument arg) {
		super(arg);
	}

	@Override
	protected ArgumentView<?> getBlankElement() {
		// The way this is designed this casting to byte argument
		// is the only unstable process.
		BooleanArgument arg = (BooleanArgument) ArgumentFactory.getArgument(
				"" + AJConstant.ALLJOYN_BOOLEAN, "",  getArgDirection());
		BooleanArgumentView view = new BooleanArgumentView(arg);
		view.hideSaveOption();
		return view;
	}

	@Override
	public boolean[] getCurrentElements(StringBuffer buf) {
		List<ArgumentView<?>> currentViews = getArgViews();
		boolean[] array = new boolean[currentViews.size()];

		// For every one of the views cast it into a byte argument to extract the value.
		for (int i = 0; i < currentViews.size(); ++i) {

			// Cast down to the correct argument view.
			BooleanArgumentView bView = (BooleanArgumentView) currentViews.get(i);

			// Extract the value if it exists.
			Boolean val =  bView.getValue();

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
