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

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.triumph.MainApplication;
import org.alljoyn.triumph.util.AJConstant;
import org.w3c.dom.Node;

/**
 * 
 * @author mhotan
 */
public class VariantArgument extends Argument<Variant> {

    /**
     * 
     */
    private static final long serialVersionUID = -4225584971504006014L;

    VariantArgument(Node node, DIRECTION defaultDirection) {
        super(node, defaultDirection);
    }

    VariantArgument(String name, DIRECTION isInput) {
        super(name, isInput);
    }

    @Override
    public String getSignature() {
        return "Variant " + getName();
    }

    @Override
    protected String getAJSignature() {
        return "" + AJConstant.ALLJOYN_VARIANT;
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
        Variant v = getValue();
        try {
            Object o;
            if (v == null || (o = v.getObject(Object.class)) == null) {
                out.write((byte) 0);
                return;
            }
            out.write((byte) 1);
            out.writeUTF(v.getSignature());
            out.writeObject(o);
        } catch (BusException e) {
            // Unable to save Varaint
            MainApplication.getLogger().warning("Unable to save varirant " + v);
            out.write((byte) 0);
            return;
        }
        
    }

    @Override
    protected void readIn(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readIn(in);
        byte hasValue = in.readByte();
        if (hasValue == 0) return;
        String sig = in.readUTF();
        Object o = in.readObject();
        setValue(new Variant(o, sig));
    }

}
