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
import java.util.HashMap;
import java.util.Map;

import org.alljoyn.triumph.util.AJConstant;
import org.w3c.dom.Node;

/**
 * Argument that represents a Dictionary.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class DictionaryArgument extends ArrayArgument<Map<?, ?>> {

    /*
     * AllJoyn abstracts a dicitonary as an array of Dicitionary entries.
     */

    /**
     * 
     */
    private static final long serialVersionUID = -6782809379433197297L;
    /**
     * Signature => How this is labeled in UI
     * AJSignature => 
     */
    private String mSignature, mAJSignature;

    /**
     * Creates a dicitionary argument from this node.
     * @param node Node to create argument from.
     */
    DictionaryArgument(Node node, DIRECTION defaultDirection) {
        super(node, defaultDirection);

        String sig = ArgumentFactory.getSignature(node);
        checkSignature(sig);

        DictionaryEntryArgument mEntryTypeHolder = new DictionaryEntryArgument("" , getDirection(), getInnerElementType());
        mSignature = getName() + " " + mEntryTypeHolder.getSignature();
        mAJSignature = "" + AJConstant.ALLJOYN_ARRAY + mEntryTypeHolder.getAJSignature();
    }

    /**
     * Creates an empty dicitionary with associated name and signature that 
     * determines the internal type.
     * 
     * @param name The name to associate to this dictionary
     * @param isInput determines whether this dictionary is an input or output argument
     * @param signature Signature of this
     */
    DictionaryArgument(String name, DIRECTION direction, String signature) {
        super(name, direction, signature);
        checkSignature(signature);

        DictionaryEntryArgument mEntryTypeHolder = new DictionaryEntryArgument("" , getDirection(), getInnerElementType());
        mSignature = getName() + " " + mEntryTypeHolder.getSignature();
        mAJSignature = "" + AJConstant.ALLJOYN_ARRAY + mEntryTypeHolder.getAJSignature();
    }

    /**
     * Checks the signature of this.
     * @param sig Signature
     */
    private static void checkSignature(String sig) {
        if (sig == null || sig.length() < 5 || !sig.startsWith("a{") && sig.endsWith("}"))
            throw new IllegalArgumentException("Illegal signature for Dictionary: " + sig );

    }

    /**
     * @return a new Dictionary element
     */
    public DictionaryEntryArgument getNewEntry() {
        return new DictionaryEntryArgument("" , getDirection(), getInnerElementType());
    }

    @Override
    public String getSignature() {
        return mSignature;
    }

    @Override
    protected String getAJSignature() {
        return mAJSignature;
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
        out.writeUTF(mSignature);
        out.writeObject(getValue());
    }

    @Override
    protected void readIn(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readIn(in);
        mAJSignature = in.readUTF();
        mSignature = in.readUTF();
        setValue((HashMap<?,?>)in.readObject());
    }
}
