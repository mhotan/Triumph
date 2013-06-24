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
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.alljoyn.triumph.model.components.arguments.Argument;

/**
 * An JavaFXML element that shows a particular multi element argument.
 * IE this could be the base class for 
 * 
 * @author mhotan
 *
 * @param <T> Type of multi view argument.
 */
public abstract class MultiElementArgumentView<T> extends ArgumentView<T> {

	@FXML Button mAddElem;
	@FXML VBox mCenter;

	/**
	 * Internal list of ArgumentViews
	 */
	private final List<ArgumentView<?>> mArgViews;

	/**
	 * Creates an empty multielement argument view.
	 * @param arg 
	 */
	protected MultiElementArgumentView(Argument<T> arg) {
		super(arg);

		mArgViews = new ArrayList<ArgumentView<?>>();

		// FXML load the fxml layer.
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().
				getResource("MultiElementView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Label the Argument to present
		setLabel(arg.getSignature());
		hideError();

		// If the argument is an input argument 
		// we allow the uesr to continue to alter its state
		if (isInputArg())
			return;

		// Hide the irrelavent components
		hideSaveOption();
		
		mAddElem.setVisible(false);
	}

	@FXML
	void onSave() {
		onSaveCurrentValue();
	}

	/**
	 * Adds an empty element to the end of this multiview element
	 */
	@FXML void onAddNewElem() {
		// The type of this multi element argument 
		// has the ability to produce a empty type to add this view.
		addNewElem(getBlankElement());
	}

	/**
	 * Adds a pre constructed view to this argument.
	 * If the view is null then nothing is added.
	 * @param view inner view to add to this view
	 */
	protected void addNewElem(ArgumentView<?> view) {
		if (view == null)
			return; //  unable to create new view.
		
		// Current using two seperate data structures to track
		// the new element.  mArgViews is an internal use data structure 
		// to be used for saving and manipulating data.
		// While mCenter is the actual UI element containing 
		mArgViews.add(view);

		// If the structure is editable then wrap the contents in
		// a removable container.
		Node toAdd;
		if (isEditable())
			toAdd = new RemovableContainer(view, mCenter);
		else 
			toAdd = view;	
		mCenter.getChildren().add(toAdd);
	}
	
	/**
	 * Returns the current list of ArgumentViews.
	 * It is safe to case the elements of the ArgumentView elements
	 * to the same dynamic type of the value returned from getBlankElement
	 * 
	 * @return a list of all the current Argument Views
	 */
	protected List<ArgumentView<?>> getArgViews() {
		return new ArrayList<ArgumentView<?>>(mArgViews);
	}
	
	@Override
	public void onSaveCurrentValue() {
		for (ArgumentView<?> argView: mArgViews) {
			argView.onSaveCurrentValue();
		}
		
		// Now we after updating all the internal elements we will 
		// try to extract all the values.
		// check if they are valid.
		StringBuffer buf = new StringBuffer();
		T currentValue = getCurrentElements(buf);
		if (currentValue == null)
			showError(buf.length() == 0 ? "Error" : buf.toString());
		setValue(currentValue);
	}
	
	/**
	 * Attempts to extract the current state of the argument.
	 * If there is an error null will be returned and the error 
	 * message will be populated 
	 * 
	 * @return The current array of values in its current state, null on failure
	 */
	public abstract T getCurrentElements(StringBuffer buf);

	/**
	 * Method for subclass use.  Remove all UI elements to 
	 * add new elements to this view.
	 */
	protected void hideAddElementButton() {
		if (mAddElem == null) return;
		mAddElem.setVisible(false);
	}
	
	/**
	 * Method for subclass use.  Remove all UI elements to 
	 * add new elements to this view.
	 */
	protected void unHideAddElementButton() {
		if (mAddElem == null) return;
		mAddElem.setVisible(true);
	}
	
	/**
	 * Container object to help contain the any kind of payload.
	 * @author mhotan
	 */
	private final class RemovableContainer extends BorderPane {

		@FXML Pane mPayload;
		@FXML Button mRemove;

		private final Pane mParent;

		RemovableContainer(Node payload, Pane parent) {
			super();

			// FXML load the fxml layer.
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().
					getResource("RemovableContainer.fxml"));
			fxmlLoader.setRoot(this);
			fxmlLoader.setController(this);

			try {
				fxmlLoader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			// Add the content inside this container.
			mPayload.getChildren().add(payload);
			mParent = parent;
		}

		@FXML
		void onRemove() {
			// Have to remove the ArgumentView<?> element from the list containing it
			mArgViews.remove(mPayload);
			// Remove the UI element.
			mParent.getChildren().remove(this);
		}

	}
	
	/**
	 * Provides an blank argument view that corresponds to the right
	 * type of the contained argument's type.
	 * @return no value element that corresponds to this specific type.
	 */
	protected abstract ArgumentView<?> getBlankElement();

	/**
	 * Each multi element structure has the ability to determine if a 
	 * @return whether this argument is editable
	 */
	protected abstract boolean isEditable();
}
