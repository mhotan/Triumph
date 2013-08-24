package org.alljoyn.triumph.model.components.arguments;

import java.io.IOException;

/**
 *  A Special argument that represents a null pointer.
 */
public class NullArgument extends Argument<Object> {

    /**
     * 
     */
    private static final long serialVersionUID = 8845003753579360204L;

    NullArgument(String name, DIRECTION direction) {
        super(name, direction);
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
