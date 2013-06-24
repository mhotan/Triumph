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

package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import org.alljoyn.bus.BusException;
import org.alljoyn.triumph.model.components.Member;
import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.util.ViewLoader;
import org.alljoyn.triumph.view.argview.ArgumentCell;

/**
 * Base view class that is used to present a distinct member instance.
 * Signals and Methods are an example.  Both have input arguments. 
 * And both are used to present a simple input signature.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public abstract class MemberView extends HBox {

	@FXML
	protected ResourceBundle resources;

	@FXML
	protected URL location;

	@FXML
	protected HBox mButtonBar;

	@FXML
	protected VBox mCompletePane;

	@FXML
	protected Label mErrorMessage;

	@FXML
	protected Label mErrorTitle;

	@FXML
	protected ListView<Argument<?>> mInputArgumentPane;

	@FXML
	protected ListView<Argument<?>> mOutputArgumentPane;
	
	@FXML
	protected Button mInvokeButton;

	@FXML
	protected ProgressIndicator mProgressIndicator;

	@FXML
	protected BorderPane mTitlePane;

	/**
	 * Title View of this member
	 */
	private final MemberTitleView mTitleView;

	/**
	 * Call back that determines how to draw the argument
	 */
	private final Callback<ListView<Argument<?>>, ListCell<Argument<?>>> mCellFactory;
	
	/**
	 * Argument list of input and output.
	 */
	private final ObservableList<Argument<?>> mInputArgs, mOutputArgs;
	
	/**
	 * Constructs a basic Member View.  This intializes the actual view elements
	 * and checks to make sure all the components are accessible.  
	 * 
	 * @param member The Member to intialize view with.
	 */
	protected MemberView(Member member) {
		// Loads MemberView.fxml
		ViewLoader.loadView("MemberView.fxml", this);

		// Load the title view for this member.
		mTitleView = new MemberTitleView(member);
		mTitlePane.setCenter(mTitleView);

		// Handle the variable amount of input and output arguments
		List<Argument<?>> inputArgs = member.getInputArguments();
		List<Argument<?>> outputArgs = member.getOutputArguments();

		// Establish a cell factory to handle the production of the Arguments.
		mCellFactory = new Callback<ListView<Argument<?>>, ListCell<Argument<?>>>() {
			
			@Override
			public ListCell<Argument<?>> call(ListView<Argument<?>> param) {
				return new ArgumentCell();
			}
		};
		
		// If there are not any input arguments remove the unecesary pane
		mInputArgs = addOrRemove(mInputArgumentPane, inputArgs);
		mOutputArgs = addOrRemove(mOutputArgumentPane, outputArgs);
		
		// Hide an error message until there is an actual error.
		hideError();
	}

	/**
	 * Gets all the output arguments
	 * @return Array of output arguments
	 */
	protected Argument<?>[] getOutputArguments() {
		return toArray(mOutputArgs);
	}
	
	/**
	 * Gets all the input arguments of this member.
	 * @return Array of input arguments.
	 */
	protected Argument<?>[] getInputArguments() {
		return toArray(mInputArgs);
	}
	
	/**
	 * returns an array of the argument in the same order of the list.
	 * If list is null then an empty array is returned.
	 * 
	 * @param list the list of argument to turn into an array
	 * @return Array of all the arguments
	 */
	private static Argument<?>[] toArray(ObservableList<Argument<?>> list) {
		if (list == null) {
			return new Argument<?>[0];
		}
		Argument<?>[] args = new Argument[list.size()];
		list.toArray(args);
		return args;
	} 
	
	/**
	 * Attempts to add the list of argument 
	 * 
	 * @param viewList List of arguments to present in fun views
	 * @param arguments Arguments to add to list view.
	 */
	private ObservableList<Argument<?>> addOrRemove(ListView<Argument<?>> viewList, List<Argument<?>> arguments) {
		// If there are not any input arguments remove the unecesary pane
		if (arguments.isEmpty()) {
			mCompletePane.getChildren().remove(viewList);
			return null;
		} else {
			// Set the appropiate cell factory for this list
			viewList.setCellFactory(mCellFactory);
			
			// Create an observable list of all the arguments
			ObservableList<Argument<?>> mList = FXCollections.observableArrayList(arguments);
			viewList.setItems(mList);
			return mList;
		}
	}

	/**
	 * In order to construct the view there has to be a way to label
	 * what type of member we are looking at.
	 * IE Signal, Method, etc...
	 * 
	 * @return String representation of the member type.
	 */
	protected abstract String getMemberTypeName();

	/**
	 * Abstract method that notifies whatever sublass that
	 * the wish to invoke the method with the current state of the arguments
	 */
	@FXML protected void onInvokeButtonPressed() {
		try {
			hideError();
			invoke();
		} catch (BusException e) {
			showError(e.getMessage());
		}
	}
	
	/**
	 * Calls to the subclasses to invoke its feature 
	 */
	protected abstract void invoke() throws BusException;

	private void showError(String message) {
		mErrorMessage.setVisible(true);
		mErrorTitle.setVisible(true);
		mErrorMessage.setText(message);
	}
	
	private void hideError() {
		mErrorMessage.setVisible(false);
		mErrorTitle.setVisible(false);
	}
	
	@FXML
	void initialize() {
		assert mButtonBar != null : "fx:id=\"mButtonBar\" was not injected: check your FXML file 'MemberView.fxml'.";
		assert mCompletePane != null : "fx:id=\"mCompletePane\" was not injected: check your FXML file 'MemberView.fxml'.";
		assert mErrorMessage != null : "fx:id=\"mErrorMessage\" was not injected: check your FXML file 'MemberView.fxml'.";
		assert mErrorTitle != null : "fx:id=\"mErrorTitle\" was not injected: check your FXML file 'MemberView.fxml'.";
		assert mInputArgumentPane != null : "fx:id=\"mInputArgumentPane\" was not injected: check your FXML file 'MemberView.fxml'.";
		assert mInvokeButton != null : "fx:id=\"mInvokeButton\" was not injected: check your FXML file 'MemberView.fxml'.";
		assert mOutputArgumentPane != null : "fx:id=\"mOutputArgs\" was not injected: check your FXML file 'MemberView.fxml'.";
		assert mProgressIndicator != null : "fx:id=\"mProgressIndicator\" was not injected: check your FXML file 'MemberView.fxml'.";
		assert mTitlePane != null : "fx:id=\"mTitlePane\" was not injected: check your FXML file 'MemberView.fxml'.";
	}

}
