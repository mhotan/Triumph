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

import java.io.IOException;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import org.alljoyn.triumph.model.components.arguments.DictionaryEntryArgument;
import org.alljoyn.triumph.util.DictionaryEntry;

/**
 * View for a specifc Dicitonary element.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class DictionaryElemArgumentView extends ArgumentView<Map.Entry<?, ?>> {

	/**
	 * FX elements that will hold the ket and val
	 */
	@FXML protected AnchorPane mKeyContainer, mValContainer;

	/**
	 * View of the key and value properties
	 */
	private final ArgumentView<?> mKeyArgView, mValArgView;
	
	/**
	 * The argument that will determine the view of this argument.
	 */
	private final DictionaryEntryArgument mArg;

	/**
	 * Creates a dictionary element view to hold a single dicitionary element
	 * @param arg Argument to associate to this Dictionary element view
	 */
	public DictionaryElemArgumentView(DictionaryEntryArgument arg) {
		super(arg);
		mArg = arg;
/*
		// Label the Argument to present
		setLabel(arg.getSignature());
		hideError();*/
		// Extract the view for all 
		mKeyArgView = mArg.getKey().getView();
		mValArgView = mArg.getVal().getView();
		
		mKeyArgView.hideSaveButton();
		mValArgView.hideSaveButton();
		
		// Add the key and value to the containers.
		mKeyContainer.getChildren().add(mKeyArgView);
		mValContainer.getChildren().add(mValArgView);
		
		mKeyArgView.hideError();
		mValArgView.hideError();
		
		// If the argument is an input argument 
		// we allow the uesr to continue to alter its state
		if (isInputArg())
			return;
	}
	
	@Override
    protected String getFXMLFileName() {
        return "DictionaryEntry.fxml";
    }

	@Override
	@FXML
	public String onSaveCurrentValue() {
		StringBuffer buf = new StringBuffer();
		String temp;
		temp = mKeyArgView.onSaveCurrentValue();
		if (temp != null)
			buf.append("Key " + temp);
		
		temp = mValArgView.onSaveCurrentValue();
		if (temp != null) 
			buf.append("Value " + temp);
		
		Object key = mKeyArgView.getValue();
		Object val = mValArgView.getValue();
		
/*		if (key == null) {
			showError("Invalid key " + key);
			return;
		}
		if (val == null) {
			showError("Invalid value " + val);
			return;
		}*/
		
		// TODO Check if this type unsafe behavior will work.
		// This is a tough situation where we don't enforce 
		Map.Entry<?, ?> entry = new DictionaryEntry<Object, Object>(
				key, val);
		setValue(entry);
		return buf.length() == 0 ? null : buf.toString();
	}

    @Override
    public void setEditable(boolean editable) {
        mKeyArgView.setEditable(editable);
        mValArgView.setEditable(editable);
    }

    @Override
    protected boolean isEditable() {
        return mKeyArgView.isEditable();
    }
}
