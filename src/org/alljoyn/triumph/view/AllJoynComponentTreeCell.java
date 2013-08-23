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


import javafx.scene.control.Label;

import javafx.scene.control.TreeCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import org.alljoyn.triumph.model.components.AllJoynComponent;
import org.alljoyn.triumph.model.components.AllJoynService;


/**
 * Tree cell for presenting a specific type in a special way.
 * 
 * @author Michael Hotan mhotan@quicinc.com
 */
public class AllJoynComponentTreeCell extends TreeCell<AllJoynComponent> {

    private final AllJoynTreeCellFactory mFactory;

    public AllJoynComponentTreeCell(AllJoynTreeCellFactory factory) {
        mFactory = factory;
    }

    public AllJoynComponentTreeCell() {
        mFactory = null;
    }
    @Override
    protected void updateItem(AllJoynComponent item, boolean empty) {
        super.updateItem(item, empty);

        setFont(Font.font("Verdana", getFont().getSize()));
        if (item == null) {
            setText("NULL AllJoynComponent... ???");
            return;
        }

        switch (item.getType()) {
        case SERVICE:
            drawService(item);
            break;
        case OBJECT:
            // TODO Create a graphic for the this object
            drawObject(item);
            break;
        case METHOD:
            drawMethod(item);
            break;
        case SIGNAL:
            drawSignal(item);
            break;
        case PROPERTY:
            drawProperty(item);
            break;
        case INTERFACE:
            drawInterface(item);
            break;
        default: // LABEL
            drawLabel(item);
        }
    }

    /**
     * Draws an interface for this Component
     * @param item
     */
    private void drawInterface(AllJoynComponent item) {
        // Handle the presentation for the interface.
        setText(item.getString());
        setTextFill(Color.RED);
    }

    private void drawLabel(AllJoynComponent item) {
        // Present a general label
        setText(item.getString());
        setTextFill(Color.BLACK);
        setTextAlignment(TextAlignment.CENTER);
    }

    private void drawProperty(AllJoynComponent item) {
        // TODO Complete with actual Representation
        setTextFill(Color.BLACK);
        setText(item.getString());
    }

    private void drawSignal(AllJoynComponent item) {
        // TODO Complete with actual Representation
        setTextFill(Color.BLACK);
        setText(item.getString());
    }

    private void drawMethod(AllJoynComponent item) {
        // TODO Complete with actual Representation
        setTextFill(Color.BLACK);
        setText(item.getString());
    }

    /**
     * 
     * @param item
     */
    private void drawObject(AllJoynComponent item) {
        // TODO Complete with actual Representation
        setText(item.getString());
        setTextFill(Color.GREEN);
    }

    /**
     * Draws the service.
     * @param item
     */
    private void drawService(AllJoynComponent item) {
        ServiceView view = new ServiceView((AllJoynService)item);
        setGraphic(view);
        Label label = view.getLabel();
        double width = label.getPrefWidth();
        if (width > mFactory.getMaxWidth()) {
            mFactory.setMaxWidth(width);
        }
        label.setPrefWidth(mFactory.getMaxWidth());
    }
}
