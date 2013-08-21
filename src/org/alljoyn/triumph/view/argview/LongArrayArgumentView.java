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
import org.alljoyn.triumph.model.components.arguments.LongArgument;
import org.alljoyn.triumph.model.components.arguments.LongArrayArgument;
import org.alljoyn.triumph.util.AJConstant;

public class LongArrayArgumentView extends ArrayArgumentView<long[]> {

    private final LongArrayArgument mLongArrArg;

    public LongArrayArgumentView(LongArrayArgument arg) {
        super(arg);
        mLongArrArg = arg;

        // Check for any current values.
        long[] values = getValue();
        if (values == null) return;

        // Now that we have current values.
        // Populate the current view.
        try {
            ArgumentView<?>[] views = new ArgumentView<?>[values.length];
            for (int i = 0; i < values.length; ++i) {
                views[i] = ArgumentFactory.getArgument(
                        getInternalArgumentName(i+1), 
                        arg.getInnerElementType(), values[i]).getView();
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
        char sig = mLongArrArg.isUnsigned() ? AJConstant.ALLJOYN_UINT64: AJConstant.ALLJOYN_INT64;
        LongArgument arg = (LongArgument) ArgumentFactory.getArgument(
                "" + sig, "",  getArgDirection());
        LongArgumentView view = new LongArgumentView(arg);
        return view;
    }

    @Override
    public long[] getCurrentElements(StringBuffer buf) {
        List<ArgumentView<?>> currentViews = getArgViews();
        long[] array = new long[currentViews.size()];

        // For every one of the views cast it into a byte argument to extract the value.
        for (int i = 0; i < currentViews.size(); ++i) {

            // Cast down to the correct argument view.
            LongArgumentView bView = (LongArgumentView) currentViews.get(i);

            // Extract the value if it exists.
            Long val =  bView.getValue();

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
