package org.alljoyn.triumph.view.arguments.editable;

import org.alljoyn.triumph.model.components.arguments.NullArgument;

public class NullArgumentView extends SimpleArgumentView<Object> {

    public NullArgumentView(NullArgument argument) {
        super(argument);
        setEditable(false);
        mInput.setText("NULL Pointer");
    }

    @Override
    protected boolean setArgument(String raw, StringBuffer errorBuffer) {
        // This will never be called but if it does trick it into the right value
        return true;
    }

}
