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

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

import org.alljoyn.triumph.model.components.arguments.BooleanArgument;

/**
 * Argument Cell that controls the presentation of Boolean Objects.
 * 
 * @author mhotan 
 */
public class BooleanArgumentView extends ArgumentView<Boolean> {

    /**
     * Input variable for this
     */
    @FXML private ToggleButton mBoolToggle;

    /**
     * Attempt to load a view based off an FXML document.
     * @param arg Boolean Argument to assign to this view.
     */
    public BooleanArgumentView(BooleanArgument arg) {
        super(arg);
        //		ViewLoader.loadView("BooleanArgView.fxml", this);

        Boolean value = getValue();
        if (value == null) return;

        mBoolToggle.setSelected(value);
        mBoolToggle.setText(value ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
    }

    @Override
    protected String getFXMLFileName() {
        return "BooleanArgView.fxml";
    }

    @FXML
    void onToggled() {
        mBoolToggle.setText(getString(mBoolToggle.isSelected()));
        // Sets the argument value to add.
        setValue(mBoolToggle.isSelected());
    }

    /**
     * Using this method can eventually lead to the
     * internationalization of this app.
     * @param bool Boolean value to return string of
     * @return String String representation
     */
    private static String getString(boolean bool) {
        return bool ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
    }

    @Override
    public String onSetCurrentValue() {
        onToggled();
        return null; // can never have error
    }

    @Override
    public void setEditable(boolean editable) {
        mBoolToggle.setDisable(!editable);
    }

    @Override
    protected boolean isEditable() {
        return mBoolToggle.isDisabled();
    }

}
