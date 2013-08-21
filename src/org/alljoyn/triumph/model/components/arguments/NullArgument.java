package org.alljoyn.triumph.model.components.arguments;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.alljoyn.triumph.util.AJConstant;
import org.alljoyn.triumph.view.argview.ArgumentView;
import org.alljoyn.triumph.view.argview.NullArgumentView;

/**
 *  
 */
public class NullArgument extends Argument<Object> {

    NullArgument(String name, DIRECTION direction) {
        super(name, direction);
    }

    @Override
    protected ArgumentView<Object> createJavaFXNode() {
        return new NullArgumentView(this);
    }

    @Override
    public String getSignature() {
        return "Null";
    }

    @Override
    protected String getAJSignature() {
        return "Null";
    }

    @Override
    public void setValue(Object o) {
        // Do nothing
    }

    @Override
    public Object getValue() {
        return null;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //////  Methods that are used for Serialization.
    //////  
    //////////////////////////////////////////////////////////////////////////////////////

    /**
     * As implemented regarding by interface Serializable.
     * This method requires this exact signature.
     * <br> This method is called from serialization to write out internal 
     * fields to a file storage.
     * 
     * @param out ObjectOutputStream to write the file out to.
     * @throws IOException Exception occured while writing a file.
     */
    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        writeOut(out);
    }

    /**
     * As implemented regarding by interface Serializable.
     * This method requires this exact signature.
     * <br> This method is called when reading an object from file storage.
     * 
     * @param in ObjectInputStream to read in from
     * @throws IOException Error occured accessing file
     * @throws ClassNotFoundException unable to load default values.
     */
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        readIn(in);
    }
}
