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
import java.util.Map;

import org.alljoyn.triumph.TriumphCPPAdapter;
import org.alljoyn.triumph.TriumphException;
import org.w3c.dom.Node;

import com.sun.istack.internal.logging.Logger;

/**
 * This argument represents a single Dictionary Entry.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class DictionaryEntryArgument extends Argument<Map.Entry<?, ?>> {

    private final static Logger LOG = Logger.getLogger(DictionaryEntryArgument.class);
    
    /**
     * Added Serialization ID
     */
    private static final long serialVersionUID = 5378752071453737256L;

    /**
     * Internal arguments of this dictionary element.
     */
    private Argument<?> mKey, mVal;
    
    private String mKeySignature, mValSignature;

    private String mAJSignature;

    /**
     * Creates a blank Dictionary Entry Argument. 
     * 
     * @param name Name of this Dictionary element, can be null
     * @param isInput whether this argument is an input argument
     * @param signature AllJoyn Signature of the element, IE {is}
     */
    DictionaryEntryArgument(String name, DIRECTION direction, String signature) {
        super(name, direction);

        mAJSignature = signature;
        String[] inner = getInnerSignatures(signature);
        if (inner == null)
            throw new IllegalArgumentException(
                    "Illegal Signature for Dictionary Entry " + signature);

        // Signature of key and values
        mKeySignature = inner[0];
        mValSignature = inner[1];

        mKey = ArgumentFactory.getArgument(mKeySignature, "key", getDirection());
        mVal = ArgumentFactory.getArgument(mValSignature, "value", getDirection());

        if (mKey == null) 
            throw new IllegalArgumentException("Illegal signature " + mKeySignature);
        if (mVal == null) 
            throw new IllegalArgumentException("Illegal signature " + mValSignature);
    }

    DictionaryEntryArgument(Node node, DIRECTION defaultDirection) {
        super(node, defaultDirection);

        String signature = ArgumentFactory.getSignature(node);
        mAJSignature = signature;
        String[] inner = getInnerSignatures(signature);
        if (inner == null)
            throw new IllegalArgumentException(
                    "Illegal Node Signature for Dictionary Entry " + signature);

        mKeySignature = inner[0];
        mValSignature = inner[1];

        mKey = ArgumentFactory.getArgument(mKeySignature, "key", getDirection());
        mVal = ArgumentFactory.getArgument(mValSignature, "value", getDirection());

        if (mKey == null) 
            throw new IllegalArgumentException("Illegal signature " + mKeySignature);
        if (mVal == null) 
            throw new IllegalArgumentException("Illegal signature " + mValSignature);
    }

    /**
     * Attempts to extract the internal elements with the signature which represents
     * the Dictionary Entry.  
     * 
     * @param sig Signature to check
     * @throws IllegalArgumentException If the signature is not valid.
     */
    private static String[] getInnerSignatures(String sig) {

        // Signatures must have at a minimum 4 characters
        // including the opening and closing bracket.
        if (sig == null || sig.length() < 4)
            return null;

        // Must have open and close brackets.
        if (!sig.startsWith("{") && !sig.endsWith("}"))
            return null;

        // Split away the brackets
        sig = sig.substring(1, sig.length() - 1);

        // Internal members must have exactly two elements.
        String[] split = TriumphCPPAdapter.splitSignature(sig);
        if (split.length != 2) 
            return null;

        // All Good!
        return split;
    }

    /**
     * @return The argument correlated with this key
     */
    public Argument<?> getKey() {
        return mKey;
    }

    /**
     * @return The argument correlated with this value
     */
    public Argument<?> getVal() {
        return mVal;
    }

    @Override
    public String getSignature() {
        return "{ " + mKey.getSignature() + " => " + mVal.getSignature() + " }" ;
    }

    @Override
    protected String getAJSignature() {
        return mAJSignature;
    }
    
    @Override
    public void setValue(Map.Entry<?, ?> value) {
        try {
            mKey = ArgumentFactory.getArgument(mKey.getName(), mKeySignature, value.getKey());
            mVal = ArgumentFactory.getArgument(mVal.getName(), mValSignature, value.getValue());
            super.setValue(value);
        } catch (TriumphException e) {
            LOG.warning(DictionaryEntryArgument.class.getSimpleName() + ": Unable to set Value " + value);
        }
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
        out.writeObject(getKey());
        out.writeObject(getVal());
    }

    @Override
    protected void readIn(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readIn(in);
        mAJSignature = in.readUTF();
        mKey = (Argument<?>) in.readObject();
        mKeySignature = mKey.getAJSignature();
        mVal = (Argument<?>) in.readObject();
        mValSignature = mVal.getAJSignature();
    }
}
