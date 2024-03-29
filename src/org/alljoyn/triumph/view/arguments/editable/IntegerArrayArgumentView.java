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

import java.util.List;

import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.model.components.arguments.IntegerArgument;
import org.alljoyn.triumph.model.components.arguments.IntegerArrayArgument;
import org.alljoyn.triumph.util.AJConstant;


public class IntegerArrayArgumentView extends ArrayArgumentView<int[]> {

    private final IntegerArrayArgument mIntArg;

    public IntegerArrayArgumentView(IntegerArrayArgument arg) {
        super(arg);
        mIntArg = arg;

        // Check for any current values.
        int[] values = getValue();
        if (values == null) return;

        // Now that we have current values.
        // Populate the current view.
        try {
            ArgumentView<?>[] views = new ArgumentView<?>[values.length];
            for (int i = 0; i < values.length; ++i) {
                IntegerArgument innerInt = (IntegerArgument) ArgumentFactory.getArgument(
                        getInternalArgumentName(i+1), 
                        arg.getInnerElementType(), values[i]);
                views[i] = EditableArgumentViewFactory.produceView(innerInt);
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
        char sig = mIntArg.isUnsigned() ? AJConstant.ALLJOYN_UINT32: AJConstant.ALLJOYN_INT32;
        IntegerArgument arg = (IntegerArgument) ArgumentFactory.getArgument(
                "" + sig, "",  getArgDirection());
        IntegerArgumentView view = new IntegerArgumentView(arg);
        return view;
    }

    @Override
    public int[] getCurrentElements(StringBuffer buf) {
        List<ArgumentView<?>> currentViews = getArgViews();
        int[] array = new int[currentViews.size()];

        // For every one of the views cast it into a byte argument to extract the value.
        for (int i = 0; i < currentViews.size(); ++i) {

            // Cast down to the correct argument view.
            IntegerArgumentView bView = (IntegerArgumentView) currentViews.get(i);

            // Extract the value if it exists.
            Integer val =  bView.getValue();

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
