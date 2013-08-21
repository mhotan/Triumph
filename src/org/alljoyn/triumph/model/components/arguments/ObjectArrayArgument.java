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
import org.alljoyn.triumph.view.argview.ArgumentView;
import org.alljoyn.triumph.view.argview.ObjectArrayArgumentView;
import org.w3c.dom.Node;

/**
 * Argument that represents a Object Array.  This is a generalized class that accepts all kinds
 * of non primitive arrays.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class ObjectArrayArgument extends ArrayArgument<Object[]> {

    /**
     * 
     */
    private static final long serialVersionUID = 1735909341478014829L;
    private String mAJSignature;

    /**
     * Creates an Object array of a specific signature. 
     * 
     * @param name Name of this argument
     * @param direction Input or output declaration 
     * @param signature complete signature of this Object array.
     */
    public ObjectArrayArgument(String name, DIRECTION direction, String signature) {
        super(name, direction, signature);
        checkRep();
        mAJSignature = signature;
    }

    /**
     * 
     * 
     * @param node
     * @param defaultDir
     */
    public ObjectArrayArgument(Node node, DIRECTION defaultDir) {
        super(node, defaultDir);
        checkRep();
        mAJSignature = "" + AJConstant.ALLJOYN_ARRAY + getInnerElementType();
    }

    @Override
    protected ArgumentView<Object[]> createJavaFXNode() {
        return new ObjectArrayArgumentView(this);
    }

    @Override
    protected String getAJSignature() {
        return mAJSignature;
    }



    /**
     * Representation checker
     */
    private void checkRep() {
        String innerElem = getInnerElementType();
        char id = innerElem.charAt(0);
        if (id == AJConstant.ALLJOYN_BYTE 
                || id == AJConstant.ALLJOYN_BOOLEAN
                || id == AJConstant.ALLJOYN_INT16
                || id == AJConstant.ALLJOYN_UINT16
                || id == AJConstant.ALLJOYN_INT32
                || id == AJConstant.ALLJOYN_UINT32 
                || id == AJConstant.ALLJOYN_INT64 
                || id == AJConstant.ALLJOYN_UINT64 
                || id == AJConstant.ALLJOYN_DOUBLE)
            throw new IllegalStateException("Illegal array element type '" + innerElem + "' for Object arrays");
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
        out.writeUTF(mAJSignature);
        out.writeObject(getValue());
    }

    @Override
    protected void readIn(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readIn(in);
        mAJSignature = in.readUTF();
        setValue((Object[])in.readObject());
    }
}
