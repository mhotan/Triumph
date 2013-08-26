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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.alljoyn.bus.BusException;
import org.alljoyn.triumph.model.components.Member;
import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.util.loaders.ViewLoader;
import org.alljoyn.triumph.view.arguments.editable.ArgumentView;
import org.alljoyn.triumph.view.arguments.editable.EditableArgumentViewFactory;

/**
 * Base view class that is used to present a distinct member instance.
 * Signals and Methods are an example.  Both have input arguments. 
 * And both are used to present a simple input signature.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public abstract class MemberView extends VBox {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    protected HBox mButtonBar;

    @FXML
    protected VBox mCompletePane;

    @FXML
    protected Label mErrorMessage;

    @FXML
    protected VBox mInputArgBox;

    @FXML
    protected Button mInvokeButton;

    @FXML
    protected VBox mOutputArgBox;

    @FXML
    protected ProgressIndicator mProgressIndicator;

    @FXML
    protected HBox mTitlePane;

    @FXML
    protected VBox mInputArgPane, mOutputArgPane;

    private final MemberTitleView mTitleView;

    /**
     * Internal List that we use to track the state of the independent arguments.
     */
    protected final List<ArgumentView<?>> mInputArgs, mOutputArgs;

    private BooleanProperty shiftPressedProperty;

    /**
     * Constructs a basic Member View.  This intializes the actual view elements
     * and checks to make sure all the components are accessible.  
     * 
     * @param member The Member to intialize view with.
     */
    protected MemberView(Member member) {
        // Loads MemberView.fxml
        ViewLoader.loadView("MemberView.fxml", this);
        mErrorMessage.managedProperty().bind(mErrorMessage.visibleProperty());
        mInputArgPane.managedProperty().bind(mInputArgPane.visibleProperty());
        mOutputArgPane.managedProperty().bind(mOutputArgPane.visibleProperty());
        
        // Load the title view for this member.
        mTitleView = new MemberTitleView(member);
        mTitlePane.getChildren().clear();
        HBox.setHgrow(mTitleView, Priority.ALWAYS);
        mTitlePane.getChildren().add(mTitleView);

        // Handle the variable amount of input and output arguments
        List<Argument<?>> inputArgs = member.getInputArguments();
        List<Argument<?>> outputArgs = member.getOutputArguments();

        mInputArgs = new ArrayList<ArgumentView<?>>(inputArgs.size());
        mOutputArgs = new ArrayList<ArgumentView<?>>(outputArgs.size());

        // For each input and out argument,
        // Create the view for each
        // Add that view to a list that we use for tracking state
        // and add it to the actual view for the user to view.
        setInputArguments(inputArgs);
        setOutputArguments(outputArgs);

        shiftPressedProperty = new SimpleBooleanProperty(false);

        // Hide an error message until there is an actual error.
        hideError();
    }

    /**
     * Helper method for setting the argument to display.
     * 
     * @param newargs The new list of arguments to add.
     * @param trackingList Internal use list for tracking Argument views
     * @param view View to add argument views to.
     */
    private static void setArguments(List<Argument<?>> newargs, 
            List<ArgumentView<?>> trackingList, VBox view) {
        assert trackingList != null: "Internal list of argument view cannot be null";
        assert view != null: "View cannot be null";

        // Remove any previous views.
        view.getChildren().clear();
        trackingList.clear();

        // Add the new argument views.
        for (Argument<?> arg: newargs) {
            ArgumentView<?> argView = EditableArgumentViewFactory.produceView(arg);
            trackingList.add(argView);
            view.getChildren().add(argView);
        }
    }

    /**
     * Sets an internal list of views editbality
     * 
     * @param trackingList Internal list of views
     * @param editable whether the arguments are editable
     */
    private static void setArgumentEditability(List<ArgumentView<?>> trackingList, boolean editable) {
        for (ArgumentView<?> view: trackingList) {
            view.setEditable(editable);
        }
    }

    /**
     * Presents the list of argument as the input arguments
     * @param inargs Input arguments.
     */
    protected void setInputArguments(List<Argument<?>> inargs) {
        if (inargs == null || inargs.isEmpty())
            mInputArgPane.setVisible(false);
        setArguments(inargs, mInputArgs, mInputArgBox);
    }

    /**
     * @param editable true if input arguments wish to be editable, false if not
     */
    protected void setIntputArgumentEditability(boolean editable) {
        setArgumentEditability(mInputArgs, editable);
    }

    /**
     * Presents the list of arguments as the output arguments of this member.
     * @param outargs Output Arguments to use
     */
    protected void setOutputArguments(List<Argument<?>> outargs) {
        if (outargs == null || outargs.isEmpty())
            mOutputArgPane.setVisible(false);
        setArguments(outargs, mOutputArgs, mOutputArgBox);
    }

    /**
     * @param editable true if output arguments wish to be editable, false if not 
     */
    protected void setOutputArgumentEditability(boolean editable) {
        setArgumentEditability(mOutputArgs, editable);
    }

    /**
     * private helper method to handle different ways
     * of invokation request.
     */
    private void handleInvokationRequest() {
        hideError();

        // Hide any pending errors before the invocation
        try {
            invoke();
        }  catch (BusException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    void onInvokeButtonPressed(ActionEvent event) {
        // Explicit request to invoke button pressed
        handleInvokationRequest();
    }

    @FXML
    void onKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
        case SHIFT:
            shiftPressedProperty.set(true);
            break;
        case ENTER:
            // If the Shift key is pressed down  
            // and enter pressed then attempt to invoke the method.
            if (shiftPressedProperty.get()) {
                handleInvokationRequest();
            }
            break;
        default:
            // TODO handle more input keys
        }
    }

    @FXML
    void onKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
        case SHIFT:
            shiftPressedProperty.set(false);
            break;
        default:
        }
    }

    /**
     * Calls to the subclasses to invoke its feature 
     */
    protected abstract void invoke() throws BusException;

    protected void showError(String message) {
        mErrorMessage.setVisible(true);
        mErrorMessage.setText(message);
    }

    protected void hideError() {
        mErrorMessage.setVisible(false);
    }

    @FXML
    void initialize() {
        assert mButtonBar != null : "fx:id=\"mButtonBar\" was not injected: check your FXML file 'MemberView.fxml'.";
        assert mCompletePane != null : "fx:id=\"mCompletePane\" was not injected: check your FXML file 'MemberView.fxml'.";
        assert mErrorMessage != null : "fx:id=\"mErrorMessage\" was not injected: check your FXML file 'MemberView.fxml'.";
        assert mInputArgBox != null : "fx:id=\"mInputArgBox\" was not injected: check your FXML file 'MemberView.fxml'.";
        assert mInputArgPane != null : "fx:id=\"mInputArgPane\" was not injected: check your FXML file 'MemberView.fxml'.";
        assert mInvokeButton != null : "fx:id=\"mInvokeButton\" was not injected: check your FXML file 'MemberView.fxml'.";
        assert mOutputArgBox != null : "fx:id=\"mOutputArgBox\" was not injected: check your FXML file 'MemberView.fxml'.";
        assert mOutputArgPane != null : "fx:id=\"mOutputArgPane\" was not injected: check your FXML file 'MemberView.fxml'.";
        assert mTitlePane != null : "fx:id=\"mTitlePane\" was not injected: check your FXML file 'MemberView.fxml'.";
    }

}
