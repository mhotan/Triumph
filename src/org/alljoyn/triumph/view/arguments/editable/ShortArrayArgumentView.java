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
import org.alljoyn.triumph.model.components.arguments.ShortArgument;
import org.alljoyn.triumph.model.components.arguments.ShortArrayArgument;
import org.alljoyn.triumph.util.AJConstant;

public class ShortArrayArgumentView extends ArrayArgumentView<short[]> {

    private final ShortArrayArgument mShortArg;

    public ShortArrayArgumentView(ShortArrayArgument arg) {
        super(arg);
        mShortArg = arg;

        // Check for any current values.
        short[] values = getValue();
        if (values == null) return;

        // Now that we have current values.
        // Populate the current view.
        try {
            ArgumentView<?>[] views = new ArgumentView<?>[values.length];
            for (int i = 0; i < values.length; ++i) {
                ShortArgument innerShort = (ShortArgument) ArgumentFactory.getArgument(
                        getInternalArgumentName(i+1), 
                        arg.getInnerElementType(), values[i]);
                views[i] = EditableArgumentViewFactory.produceView(innerShort);
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
        char sig = mShortArg.isUnsigned() ? AJConstant.ALLJOYN_UINT16: AJConstant.ALLJOYN_INT16;
        ShortArgument arg = (ShortArgument) ArgumentFactory.getArgument(
                "" + sig, "", getArgDirection());
        ShortArgumentView view = new ShortArgumentView(arg);
        return view;
    }

    @Override
    public short[] getCurrentElements(StringBuffer buf) {
        List<ArgumentView<?>> currentViews = getArgViews();
        short[] array = new short[currentViews.size()];

        // For every one of the views cast it into a byte argument to extract the value.
        for (int i = 0; i < currentViews.size(); ++i) {

            // Cast down to the correct argument view.
            ShortArgumentView bView = (ShortArgumentView) currentViews.get(i);

            // Extract the value if it exists.
            Short val =  bView.getValue();

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
