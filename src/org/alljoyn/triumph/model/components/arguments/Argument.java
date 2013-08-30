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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.model.components.Attributable;
import org.alljoyn.triumph.model.components.Attribute;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This is the base class that represents an argument in AllJoyn.  This allows
 * to differentiate the behavior between different types of arguments.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 *
 * @param <T> The type associated with the value of this argument
 */
public abstract class Argument<T extends Object> implements Attributable, Serializable, Cloneable {

    private static final Logger LOG = Logger.getLogger(Argument.class.getSimpleName());
    
    /**
     * Serialization ID.
     */
    private static final long serialVersionUID = -6667997932611267064L;

    public enum DIRECTION {
        IN("in"),
        OUT("out");

        private final String mName;

        DIRECTION(String s) {
            mName = s;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    public static final String LABEL = "arg";
    private static final String DIRECTION_LABEL = "direction";
    public static final String SIGNATURE = "type";

    public static final String NULL = "<null>";

    /**
     * Name of the argument.
     */
    private String mName;
    private String mAJSignature;

    /**
     * If null then this argument was never saved
     * Else the last time the argument was saved.
     */
    protected Date timeLastSaved;
    
    /**
     * List of the attributes.
     */
    private List<Attribute> mAttr;

    /**
     * Labels if this argument is an input argument
     * if not then it is assumed that it is an 
     * output argument.
     */
    private DIRECTION mDirection;

    /**
     * Payload value of the argument.
     */
    private T mValue;
    
    /**
     * Because we are just writing to a file we need some kind of name to associate
     * to the object when we save it.  This is the work around we have for this.
     * TODO Fix this work around.
     */
    private String mSavedByName;

    /**
     * Creates an empty argument
     * 
     * Name can be null.
     * 
     * @param name Associated name of this argument
     * @param direction Direction of the argument.
     * @param Alljoyn type signature IE. 'as' for array of signature.
     */
    Argument(String name, DIRECTION direction, String dBusSignature) {
        if (direction == null)
            throw new IllegalArgumentException("Direction cannot be null");

        mName = name;
        mAttr = new ArrayList<Attribute>();
        mDirection = direction;
        mAJSignature = dBusSignature;
        mSavedByName = "" + new Date().toString();
        mValue = null;
    }

    /**
     * Constructor for a Alljoyn interface member. This 
     * @param node
     * @param defaultDir
     */
    Argument(Node node, DIRECTION defaultDir) {
        if (defaultDir == null)
            throw new NullPointerException("Argument Constructor: NULL Default Direction argument");

        if (!LABEL.equals(node.getNodeName())) {
            throw new IllegalArgumentException("Node does not represent an Argument object");
        }

        NamedNodeMap attrList = node.getAttributes();

        // Attempt to extract the name of the node.
        Node n2 = attrList.getNamedItem("name");
        mName = n2 != null ? n2.getNodeValue() : "";
        mAttr = new ArrayList<Attribute>();

        // iterate through all the attributes of the node
        // and add them to our list of attributes
        int numAttributes = attrList.getLength();

        // DBus specification infers that argument are
        // defaulted to input arguments.
        // Members like Signals do not specify whether the argument is in or out
        // 	but instead arguments for signals are always considered to be
        DIRECTION tmpDir = defaultDir;

        for (int i = 0; i < numAttributes; ++i) {
            Node n = attrList.item(i);
            String nodeName = n.getNodeName();

            // We already are tracking the name.
            if ("name".equals(nodeName)) continue;

            // Found the direction attribute
            if (DIRECTION_LABEL.equals(nodeName)) {
                tmpDir = "in".equals(n.getNodeValue()) ? DIRECTION.IN: DIRECTION.OUT;
            }
            
            if (SIGNATURE.equals(nodeName)) {
                mAJSignature = n.getNodeValue();
            }

            // Add the argument attribute
            addAttribute(new Attribute(n));
        }

        mDirection = tmpDir;
    }

    public String getName() {
        return mName == null ? "": mName;
    }

    /**
     * Allows clients to see if this argument is a input argument.
     * If this returns false then this argument is assumed to be an output parameter. 
     * @return whether this argument is an input argument.
     */
    public boolean isInput() {
        return mDirection == DIRECTION.IN;
    }

    /**
     * @return The direction of this Argument.
     */
    public boolean isOutput() {
        return mDirection == DIRECTION.OUT; 
    }

    /**
     * Every argument knows whether it is an input or output argument.
     * Therefore it is possible to retrieve the known direction.
     * 
     * @return direction of this argument
     */
    public DIRECTION getDirection() {
        return mDirection;
    }

    /**
     * Gets the Type of this argument.  This is annotated as
     * an alljoyn signature.  IE ai, i, (bis)
     * 
     * @return String name of the type of this, or null if no type is defined
     */
    public String getDBusSignature() {
        if (mAJSignature == null) {
            return getAJSignature();
        }
        return mAJSignature;
    }

    /**
     * Returns whether this node is considered
     * to represents a Argument.
     * @param node Speculated Node
     * @return whether node represents a signal or not.
     */
    public static boolean isArgument(Node node) {
        String name = node.getNodeName();
        return name != null && name.equals(LABEL);
    }

    @Override
    public List<Attribute> getAttributes() {
        return new ArrayList<Attribute>(mAttr);
    }
    
    @Override
    public void addAttribute(Attribute attr) {
        if (attr == null) return;
        if (mAttr.contains(attr)) {
            mAttr.remove(attr);
        }
        mAttr.add(attr);
    }

    @Override
    public void removeAttribute(Attribute a) {
        mAttr.remove(a);
    }

    /**
     * Sets the current value of this object to inputted argument.
     * @param value value to set argument to
     * @return Error message if error occured or null on success
     * @throws TriumphException 
     */
    public void setValue(T value) {
        mValue = value;
    }

    /**
     * @return Current value of this object.
     */
    public T getValue() {
        return mValue;
    }

    /**
     * Sets the name this argument will be saved by.
     * @param name Name to use to save this argument
     */
    public void setSaveByName(String name) {
        if (name == null)
            throw new NullPointerException("can't save a name with null as its name");
        mSavedByName = name;
    }
    
    /**
     * 
     * @return null if never saved else the name it was saved by
     */
    public String getSaveByName() {
        return mSavedByName;
    }
    
    @Override
    public String toString() {
        return getSignature();
    }

    /**
     * returns human readable singature.  Allows for easy interpretation
     * of the argument.
     * 
     * IE. Unsigned int x
     * 
     * @return human readable signature
     */
    public abstract String getSignature();

    /**
     * Every argument has an alljoyn signature IE
     * Integer has signature 'i'.
     * 
     * Every argument that can be constructed must have an inner type
     * 
     * @return Signature of alljoyn type.
     */
    protected abstract String getAJSignature();
    
    //////////////////////////////////////////////////////////////////////////////////////
    ////// Handle equality and hashcode
    ////// This allows us to store Argument persistantly and maintain the same specifcation
    //////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!getClass().equals(o.getClass())) return false;
        Argument<T> a = (Argument<T>) o;
        return a.mName.equals(mName) && a.getDBusSignature().equals(getDBusSignature());
    }
    
    @Override
    public int hashCode() {
        return mName.hashCode() * getDBusSignature().hashCode();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        Argument<T> arg = null;
        try {
            arg = (Argument<T>) ArgumentFactory.getArgument(mName, mAJSignature, getValue());
        } catch (TriumphException e) {
            LOG.severe("Unable to clone " + this.toString());
        }
        return arg;
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
     * @param in Objectinput
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        readIn(in);
    }
    
    /**
     * Writes out this object data fields to the specified output stream.
     * 
     * @param out Output stream to write out to.
     * @return Returns true if there is a current value to write
     */
    protected void writeOut(java.io.ObjectOutputStream out) throws IOException {
        out.writeUTF(mName); // write the name of the argument.
        out.writeUTF(getDBusSignature());
        out.writeObject(mAttr.toArray());
        out.writeObject(mDirection);
        timeLastSaved = new Date();
        out.writeObject(timeLastSaved);
        out.writeUTF(mSavedByName == null ? timeLastSaved.toString() : mSavedByName);
    }
    
    /**
     * reads in this instance from specified input stream.
     * @param in Input stream to read from.
     * @throws ClassNotFoundException
     * @return Returns true if there is a current value to write 
     */
    protected void readIn(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.mName = in.readUTF();
        this.mAJSignature = in.readUTF();
        Object[] array = (Object[]) in.readObject();
        mAttr = new ArrayList<Attribute>(array.length);
        for (Object a: array)
            mAttr.add((Attribute)a);
        this.mDirection = (DIRECTION) in.readObject();
        this.timeLastSaved = (Date) in.readObject();
        this.mSavedByName = in.readUTF();
    }

}
