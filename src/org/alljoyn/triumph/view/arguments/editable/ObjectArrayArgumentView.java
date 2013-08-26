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

import java.util.ArrayList;
import java.util.List;

import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.model.components.arguments.ObjectArrayArgument;
import org.alljoyn.triumph.util.AJConstant;

/**
 * A view that is used to show any kind of Object Array
 */
public class ObjectArrayArgumentView extends ArrayArgumentView<Object[]> {

    private final ObjectArrayArgument mArg;

    /**
     * Creates a view to present an array of objects.
     * 
     * @param arg ObjectArrayArgument to base view off of
     */
    public ObjectArrayArgumentView(ObjectArrayArgument arg) {
        super(arg);
        mArg = arg;

        // Check for any current values.
        Object[] values = getValue();
        if (values == null) return;

        // Now that we have current values.
        // Populate the current view.
        try {
            ArgumentView<?>[] views = new ArgumentView<?>[values.length];
            for (int i = 0; i < values.length; ++i) {
                Argument<?> innerArg = ArgumentFactory.getArgument(
                        getInternalArgumentName(i+1), 
                        arg.getInnerElementType(), values[i]);
                views[i] = EditableArgumentViewFactory.produceView(innerArg);
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
        String sig = mArg.getInnerElementType();
        char id = sig.charAt(0);
        switch (id) {
        case AJConstant.ALLJOYN_STRING:
        case AJConstant.ALLJOYN_SIGNATURE:
        case AJConstant.ALLJOYN_OBJECT_PATH:
        case AJConstant.ALLJOYN_ARRAY:
        case AJConstant.ALLJOYN_STRUCT_OPEN:
        case AJConstant.ALLJOYN_VARIANT:
        case AJConstant.ALLJOYN_DICT_ENTRY_OPEN:
            Argument<?> arg = ArgumentFactory.getArgument(sig, "",  getArgDirection());
            return EditableArgumentViewFactory.produceView(arg);
        } 
        throw new RuntimeException("Unsupported object array signature a" + sig);
    }

    @Override
    public Object[] getCurrentElements(StringBuffer buf) {
        List<ArgumentView<?>> currentViews = getArgViews();
        List<Object> fields = new ArrayList<Object>(currentViews.size());

        // Go through all the views in order and extract the
        // Value and put into array form.
        for (ArgumentView<?> view: currentViews) {

            Object field = view.getValue();
            if (field == null) {
                buf.append("NUll value for " + view);
                return null;
            }
            fields.add(field);
        }

        return fields.toArray();
    }
}
