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

/**
 * A base class that represents an argument that represents a NumberArgument type.
 * 
 * @author mhotan
 *
 * @param <T> Number type associated with this argument.
 */
public abstract class NumberArgument<T extends Number> extends Argument<T> {

    /**
     * 
     */
    private static final long serialVersionUID = -2286821073344260201L;

    /**
     * Flag to show 
     */
    private boolean isUnsigned;

    /**
     * Standard error representing number should be positive.
     */
    protected static final String NEGATIVE_ERROR = "Error: Must be Non Negative";

    NumberArgument(String name, DIRECTION direction, boolean isUnsigned) {
        super(name, direction);
        this.isUnsigned = isUnsigned;
    }

    /**
     * Creates a number argument
     * 
     * @param node 
     */
    NumberArgument(org.w3c.dom.Node node, boolean isUnsigned, DIRECTION defaultDir) {
        super(node, defaultDir);
        this.isUnsigned = isUnsigned;
    }

    /**
     * @return If value is Unsigned.
     */
    public boolean isUnsigned() {
        return isUnsigned;
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
        out.writeByte(isUnsigned ? 1 : 0);
        out.writeObject(getValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void readIn(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readIn(in);
        isUnsigned = in.readByte() == 1;
        setValue((T)in.readObject());
    }
}
