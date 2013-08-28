package org.alljoyn.triumph.view.arguments.simple;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import org.alljoyn.triumph.model.components.arguments.Argument;

public class SimpleArgumentView extends HBox {

    private final Argument<?> mArg;
    
    /**
     * Creates a simple argument view for this argument
     * @param arg Argument 
     */
    SimpleArgumentView(Argument<?> arg) {
        super();
        mArg = arg;
        Object value = mArg.getValue();
        if (value == null)
            value = "Null";
        getChildren().add(new Label(value.toString()));
    }

}
