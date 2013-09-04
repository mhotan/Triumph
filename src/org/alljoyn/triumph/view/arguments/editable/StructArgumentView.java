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

import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.model.components.arguments.StructArgument;

/**
 * Argument View that presents a struct to the user.
 * @author mhotan@quicinc.com
 */
public class StructArgumentView extends MultiElementArgumentView<Object[]> {

	/**
	 * Reference to internal Struct
	 */
	private final StructArgument mStructArg;
	

	/**
	 * Creates a view based off the inputted struct argument.
	 * 
	 * @param arg StructArgument to create view from.
	 */
	public StructArgumentView(StructArgument arg) {
		super(arg);
		
		mStructArg = arg;
		// Populate the view with all the internal struct members.
		Argument<?>[] arguments = mStructArg.getInternalMembers();
		for (Argument<?> a: arguments) {
			addNewElem(EditableArgumentViewFactory.produceView(a));
		}
		
		hideAddElementButton();
	}

	@Override
	protected ArgumentView<?> getBlankElement() {
		// TODO Later make more generic where users can define their own structs.
		throw new RuntimeException("Struct cannot add new elements");
	}

	@Override
	protected boolean isEditable() {
		return false; // Should never be able to edit a struct once it is defined.
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
	
	@Override
    public void setEditable(boolean editable) {
	    super.setEditable(editable);
	    hideAddElementButton();
	}

}
