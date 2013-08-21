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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alljoyn.triumph.MainApplication;
import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.model.components.arguments.DictionaryArgument;
import org.alljoyn.triumph.model.components.arguments.DictionaryEntryArgument;

/**
 * Creates a view for a Dictionary Argument.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class DictionaryArgumentView extends MultiElementArgumentView<Map<?, ?>> {

    private final DictionaryArgument mDictArg;

    public DictionaryArgumentView(DictionaryArgument arg) {
        super(arg);
        mDictArg = arg;

        // Check for any current values.
        Map<?, ?> map = getValue();
        if (map == null) return;

        // Now that we have current values.
        // Populate the current view.
        try {
            List<Map.Entry<?, ?>> entryList = new ArrayList<Map.Entry<?,?>>(map.entrySet());
            int size = entryList.size();
            ArgumentView<?>[] views = new ArgumentView<?>[size];

            for (int i = 0; i < size; ++i) {
                // Populate the array of dictionary entry arguments.
                DictionaryEntryArgument entryArg = (DictionaryEntryArgument) ArgumentFactory.getArgument(
                        getInternalArgumentName(i+1), 
                        arg.getInnerElementType(), entryList.get(i));
                entryArg.setValue(entryList.get(i));
                views[i] = entryArg.getView();
            }
            // If there any current values then populate the view.
            for (int i = 0; i < size; ++i) {
                addNewElem(views[i]);
            }
        } catch (TriumphException e) {
            showError("Unable to unpack " + map.getClass().getSimpleName() 
                    + " because of " + e.getMessage());
        }
    }

    @Override
    protected ArgumentView<?> getBlankElement() {
        return mDictArg.getNewEntry().getView();
    }

    @Override
    protected boolean isEditable() {
        return isInputArg();
    }

    @Override
    public Map<?, ?> getCurrentElements(StringBuffer buf) {
        List<ArgumentView<?>> currentViews = getArgViews();
        // NOTE: Be carefule changing the dynamic type 
        // of this map.  HashMap is serializable allowing it to 
        // to be stored and saved.
        Map<Object, Object> map = new HashMap<Object,Object>();

        // For every one of the views cast it into a 
        // byte argument to extract the value.
        for (ArgumentView<?> view: currentViews) {

            // Extract the dictionary element view.
            DictionaryElemArgumentView dView = (DictionaryElemArgumentView) view;
            Map.Entry<?, ?> argument = dView.getValue();

            // Null check
            if (argument == null) {
                MainApplication.getLogger().severe("DictionaryElemArgumentView returned null Map entry");
                if (buf != null)
                    buf.append("Null Argument");
                return null;
            }

            // Extract the key and value
            Object key = argument.getKey();
            Object value = argument.getValue();

            if (map.containsKey(key)) {
                MainApplication.getLogger().warning("Duplicate DictionaryElemArgumentView keys");
                if (buf != null)
                    buf.append("Duplicate key " + key);
                return null;
            }

            map.put(key, value);
        }
        return map;
    }

    @Override
    protected String getInternalArgumentName(int position) {
        return "Entry"; 
    }

}
