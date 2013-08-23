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
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.alljoyn.triumph.MainApplication;
import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.util.loaders.ViewLoader;

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

    private boolean isEditable;

    /**
     * Creates an empty multielement argument view.
     * @param arg 
     */
    protected MultiElementArgumentView(Argument<T> arg) {
        super(arg);
        //        ViewLoader.loadView("MultiElementView.fxml", this);

        // Set default to editable
        isEditable = true;
        mArgViews = new ArrayList<ArgumentView<?>>();

        // Label the Argument to present
        setLabel(arg.getSignature());
        hideError();

        // Check if the 

        // If the argument is an input argument 
        // we allow the uesr to continue to alter its state
        if (isInputArg())
            return;

        mAddElem.setVisible(false);
    }

    @Override
    protected String getFXMLFileName() {
        return "MultiElementView.fxml";
    }

    @Override
    public String onSetCurrentValue() {
        StringBuffer buf = new StringBuffer();

        for (ArgumentView<?> argView: mArgViews) {
            String tmp = argView.onSetCurrentValue();
            if (tmp != null) {
                if (buf.length() != 0) 
                    buf.append("\n");
                buf.append(tmp);
            }
        }

        if (buf.length() != 0) {
            MainApplication.getLogger().warning("Unable to save " + mArg.getName());
            return buf.toString();
        }

        // Now we after updating all the internal elements we will 
        // try to extract all the values.
        // check if they are valid.
        buf = new StringBuffer();
        T currentValue = getCurrentElements(buf);
        if (currentValue == null) {
            return buf.length() == 0 ? "Error" : buf.toString();
        }
        setValue(currentValue);
        return null;
    }

    @FXML void onSave() {
        onSetCurrentValue();
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

        // hide the save button
        view.hideSaveButton();
        
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

    /**
     * Attempts to extract the current state of the argument.
     * If there is an error null will be returned and the error 
     * message will be populated 
     * 
     * @return The current array of values in its current state, null on failure
     */
    public abstract T getCurrentElements(StringBuffer buf);

    @Override
    public void setEditable(boolean editable) {
        isEditable = editable;
        for (ArgumentView<?> arg : mArgViews) {
            arg.setEditable(isEditable);
        }

        if (isEditable) 
            unHideAddElementButton();
        else 
            hideAddElementButton();
    }

    /**
     * Return a general name when there is one present
     * 
     * @param position Position of the internal argument inside this argument view's argument
     * @return String name of the argument
     */
    protected String getInternalArgumentName(int position) {
        return "Field " + position + ".";
    }

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

    @Override
    protected boolean isEditable() {
        return isEditable;
    }

    /**
     * Provides an blank argument view that corresponds to the right
     * type of the contained argument's type.
     * @return no value element that corresponds to this specific type.
     */
    protected abstract ArgumentView<?> getBlankElement();

    /**
     * Container object to help contain the any kind of payload.
     * @author mhotan
     */
    private final class RemovableContainer extends BorderPane {

        @FXML Button mRemove;

        private final Pane mParent;
        private final ArgumentView<?> mPayload;

        RemovableContainer(ArgumentView<?> payload, Pane parent) {
            ViewLoader.loadView("RemovableContainer.fxml", this);

            // Add the content inside this container.
            setCenter(payload);
            mParent = parent;
            mPayload = payload;
        }

        @FXML
        void onRemove() {
            // Have to remove the ArgumentView<?> element from the list containing it
            setCenter(null);
            mArgViews.remove(mPayload);
            // Remove the UI element.
            mParent.getChildren().remove(this);
        }
    }

}
