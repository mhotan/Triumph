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

package org.alljoyn.triumph.model.components.arguments;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.alljoyn.bus.MarshalBusException;
import org.alljoyn.triumph.TriumphCPPAdapter;
import org.alljoyn.triumph.TriumphException;
import org.w3c.dom.Node;

/**
 * General argument that represents a Struct.  This struct can be defined
 * and altered dynamically.  That is, once the struct signature is defined then resources
 * for its internal field is set.  Then the internal fields can be updated
 * dynamically.
 */
public class StructArgument extends Argument<Object[]> {

    /**
     * 
     */
    private static final long serialVersionUID = -1484245186890951349L;

    /**
     * An array of signatures of the contained members.
     */
    private String[] mSigs;

    /**
     * The alljoyn type signature of this struct
     * IE. (asi)
     */
    private String mAJSignature;

    /**
     * Structs are composed of internal fields.  Therefore
     * Struct arguments are composed of internal Argument<?> instances.
     */
    private Argument<?>[] mInternalArgs;

    /**
     * Creates a StructArgument based off the inputted signature.  This 
     * requires that the signature is. 
     * 
     * @param name Name of the argument
     * @param dir is Input argument
     * @param signature Signature of the argument.
     * @throws MarshalBusException 
     */
    StructArgument(String name, DIRECTION dir, String signature) {
        super(name, dir, signature);
        isStructOrThrow(signature);

        mAJSignature = signature;
        // Remove the beginning and ending brackets.
        signature = signature.substring(1, signature.length() - 1);
        mSigs = TriumphCPPAdapter.splitSignature(signature);
        if (mSigs == null) {
            throw new RuntimeException("Unable to extract Struct" +
                    " inner types for signature: " + signature);
        }

        // Argument length must match the length of the signatures.
        mInternalArgs = new Argument<?>[mSigs.length];

        // For every signature 
        // Produce a Argument for every signature type internally
        for (int i = 0; i < mSigs.length; ++i) {
            mInternalArgs[i] = ArgumentFactory.getArgument(
                    mSigs[i], posToName(i+1), getDirection());
        }
    }

    /**
     * Creates a Struct arguments from a XML DOm node.
     * 
     * @param node Node that represents a Struct
     * @param defaultDirection default direction of this argument
     * @throws MarshalBusException 
     */
    StructArgument(Node node, DIRECTION defaultDirection) {
        super(node, defaultDirection);

        // Get the AJ Struct signature
        String signature = ArgumentFactory.getSignature(node);
        isStructOrThrow(signature);

        if (!(signature.startsWith("(") && signature.endsWith(")"))) {
            throw new IllegalArgumentException(
                    "Node is not a struct. Illegal Signature " + signature);
        }
        mAJSignature = signature;
        // Remove the beginning and ending brackets.
        signature = signature.substring(1, signature.length() - 1);
        mSigs = TriumphCPPAdapter.splitSignature(signature);
        if (mSigs == null) {
            throw new RuntimeException("Unable to extract Struct" +
                    " inner types for signature: " + signature);
        }

        // Argument length must match the length of the signatures.
        mInternalArgs = new Argument<?>[mSigs.length];
        // For every signature 
        // Produce a Argument for every signature type internally
        for (int i = 0; i < mSigs.length; ++i) {
            mInternalArgs[i] = ArgumentFactory.getArgument(
                    mSigs[i], posToName(i+1), getDirection());
        }
    }

    @Override
    public String getSignature() {
        return "Struct " + getName();
    }

    /**
     * Returns a independent copy of the array of signatures
     * @return an array of the signature types in the array.
     */
    public String[] getTypes() {
        return mSigs.clone();
    }

    /**
     * Return all the internal members of this field with is
     * corresponding argument value.
     * @return Arguments inside this struct.
     */
    public Argument<?>[] getInternalMembers() {
        return mInternalArgs;
    }

    @Override
    public void setValue(Object[] value) {
        // Ensure the arguments match 
        if (mSigs.length != value.length) {
            throw new IllegalArgumentException("Illegal amount of fields");
        }
        String signature = null;
        Object object = null;
        try {
            // Set up the internal arguments of this Struct Argument.
            for (int i = 0; i < value.length; ++i) {
                signature = mSigs[i];
                object = value[i];
                mInternalArgs[i] = ArgumentFactory.getArgument(posToName(i+1), signature, object);
            }
            // Set the value actual values
            super.setValue(value);
        } catch (TriumphException e) {
            throw new IllegalArgumentException("StructArgument.setValue() Object of type " 
                    + object == null ? "Null" : object.getClass().getSimpleName() 
                            + " does not conform to the DBus signature " + signature);
        }
    }

    @Override
    protected String getAJSignature() {
        return mAJSignature;
    }



    /**
     * This is a strict enforcement that makes sure the signature is
     * a struct signature.  That is the signature begins with "(" and ends
     * with ")"
     * 
     * @param signature Signature to check
     */
    private static void isStructOrThrow(String signature) {
        if (!isStructSignature(signature)) {
            throw new IllegalArgumentException(
                    "Node is not a struct. Illegal Signature " + signature);
        }
    }

    /**
     * A boolean check that the signature belongs to a struct.
     * 
     * @param signature Signature to check
     * @return true is Struct, and false otherwise
     */
    private static boolean isStructSignature(String signature) {
        return signature.startsWith("(") && signature.endsWith(")");
    }

    /**
     * @param position Position to label field
     * @return Name that assigns field position
     */
    private static String posToName(int position) {
        return "Field " + position + ".";
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

    @Override
    protected void writeOut(ObjectOutputStream out) throws IOException {
        super.writeOut(out);
        out.writeObject(mSigs);
        out.writeUTF(mAJSignature);
        out.writeObject(getValue());
    }

    @Override
    protected void readIn(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readIn(in);
        mSigs = (String[]) in.readObject();
        mAJSignature = in.readUTF();
        Object[] fields = (Object[])in.readObject();
        mInternalArgs = new Argument<?>[fields.length];
        setValue(fields);
    }

}
