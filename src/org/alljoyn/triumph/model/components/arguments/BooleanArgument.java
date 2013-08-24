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

import org.alljoyn.triumph.util.AJConstant;
import org.w3c.dom.Node;

/**
 * An argument that represent the an argument that is a single boolean.
 * @author mhotan
 */
public class BooleanArgument extends Argument<Boolean> {

    /**
     * Added Serialization ID
     */
    private static final long serialVersionUID = 5918649155584578481L;

    /**
     * 
     * @param name
     * @param direction
     */
    BooleanArgument(String name, DIRECTION direction) {
        super(name, direction);
    }

    /**
     * 
     * @param node Node that contains data about 
     * @param defaultDirection default direction if not specified in introspection data
     */
    BooleanArgument(Node node, DIRECTION defaultDirection) {
        super(node, defaultDirection);
    }

    @Override
    public String getSignature() {
        return Boolean.class.getSimpleName() + " " + getName();
    }

    @Override
    protected String getAJSignature() {
        return "" + AJConstant.ALLJOYN_BOOLEAN;
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
        out.writeObject(getValue());
    }

    @Override
    protected void readIn(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readIn(in);
        setValue((Boolean)in.readObject());
    }

}
