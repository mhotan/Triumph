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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alljoyn.triumph.MainApplication;
import org.alljoyn.triumph.model.components.arguments.DictionaryArgument;

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

}
