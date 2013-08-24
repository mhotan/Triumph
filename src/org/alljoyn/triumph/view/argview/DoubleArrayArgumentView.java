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

import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.model.components.arguments.ArrayArgument;
import org.alljoyn.triumph.model.components.arguments.DoubleArgument;
import org.alljoyn.triumph.util.AJConstant;

public class DoubleArrayArgumentView extends ArrayArgumentView<double[]> {

	public DoubleArrayArgumentView(ArrayArgument<double[]> arg) {
		super(arg);
		
		// Check for any current values.
		double[] values = getValue();
        if (values == null) return;
        
        // Now that we have current values.
        // Populate the current view.
        try {
            ArgumentView<?>[] views = new ArgumentView<?>[values.length];
            for (int i = 0; i < values.length; ++i) {
                DoubleArgument innerBArg = (DoubleArgument) ArgumentFactory.getArgument(
                        getInternalArgumentName(i+1), 
                        arg.getInnerElementType(), values[i]);
                views[i] = EditableArgumentViewFactory.produceView(innerBArg);
            }
            // If there any current values then populate the view.
            for (int i = 0; i < values.length; ++i) {
                addNewElem(views[i]);
            }
        } catch (TriumphException e) {
            showError("Unable to unpack " + values.getClass().getSimpleName() 
                    + " because of " + e.getMessage());
        }
	}

	@Override
	protected ArgumentView<?> getBlankElement() {
		// The way this is designed this casting to byte argument
		// is the only unstable process.
		DoubleArgument arg = (DoubleArgument) ArgumentFactory.getArgument(
				"" + AJConstant.ALLJOYN_DOUBLE, "",  getArgDirection());
		DoubleArgumentView view = new DoubleArgumentView(arg);
		return view;
	}

	@Override
	public double[] getCurrentElements(StringBuffer buf) {
		List<ArgumentView<?>> currentViews = getArgViews();
		double[] array = new double[currentViews.size()];
		
		// For every one of the views cast it into a byte argument to extract the value.
		for (int i = 0; i < currentViews.size(); ++i) {
			
			// Cast down to the correct argument view.
			DoubleArgumentView bView = (DoubleArgumentView) currentViews.get(i);
			
			// Extract the value if it exists.
			Double val =  bView.getValue();
			
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
