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

package org.alljoyn.triumph.model.components;

import java.util.ArrayList;
import java.util.List;

import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.model.components.arguments.Argument.DIRECTION;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class that shares attributes with Method and Signal.  Provides all the necessary 
 * features of an invocable method.
 * @author mhotan
 */
public abstract class Member extends InterfaceComponent {

    /**
     * List of all Arguments associated with this method
     */
    protected final List<Argument<?>> mInArguments, mOutArguments;

    /**
     * List of all Annotation associated with this method.
     */
    private final List<Annotation> mAnnotions;

    /**
     * Owning interface.
     */
    protected final Interface mInterface;

    private final int isDeprecated, isNoReply;

    private static String ACCESS_PERMISSIONS = null; // No access permissions

    /**
     * Creates a method instance that is able to parse a node
     * that represents a method
     * 
     * node must have a name "method"
     * 
     * @param node node that represents the method.
     */
    Member(Node node, Interface iface, TYPE type, DIRECTION defaultDirection) {
        super(node, type, iface);
        if (iface == null) {
            throw new IllegalArgumentException("Can't have null interface as owner");
        }
        mInterface = iface;

        mInArguments = new ArrayList<Argument<?>>();
        mOutArguments = new ArrayList<Argument<?>>();
        mAnnotions = new ArrayList<Annotation>();

        int tempIsDep = 0, tempIsNoReply = 0;

        // Get the arguments 
        NodeList children = node.getChildNodes();
        int numChildren = children.getLength();

        // For every child parse each individual piece.
        Node child;
        for (int i = 0; i < numChildren; i++) {
            child = children.item(i);

            // Check if the XML contains an element.
            if (Node.ELEMENT_NODE != child.getNodeType()) {
                continue; // skip anything that isn't an ELEMENT
            }

            // For all the arguments found for this particular method.
            // place the arguments appropiately.
            if (Argument.isArgument(child)) {
                // We found an argument
                Argument<?> arg = ArgumentFactory.getArgument(child, defaultDirection);
                if (arg == null) {
                    throw new RuntimeException("Argument \"" + child + "\" cannot be null");
                }

                if (arg.isInput()) {
                    mInArguments.add(arg);
                } else 
                    mOutArguments.add(arg);

            } else if (Annotation.isAnnotation(child)) {
                Annotation a = new Annotation(child);

                // Check if any of the annotations
                // Show that this is 
                for (Attribute attr: a.getAttributes()) {
                    if (Annotation.DEPRECATED.equals(attr.getKey()) 
                            && "true".equals(attr.getValue())) {
                        tempIsDep = 2;
                    }
                    if (Annotation.NOREPLY.equals(attr.getKey()) &&
                            "true".equals(attr.getValue()))
                        tempIsNoReply = 1;
                }
                addAnnotation(a);
            }
        }

        isDeprecated = tempIsDep;
        isNoReply = tempIsNoReply;
    }

    /**
     * Adds the annotation if the annotation is not null
     * and it is not already in present within this member.
     * 
     * @param a Annotation to add
     */
    public void addAnnotation(Annotation a) {

        // Check if we need to add the annotation
        if (a == null || mAnnotions.contains(a))
            return;

        mAnnotions.add(a);
    }

    /**
     * @return the owning interface.
     */
    public Interface getInterface() {
        return mInterface;
    }

    /**
     * Returns any access permission to this Invokable Attribute.
     * @return
     */
    public String getAccessPermission() {
        return ACCESS_PERMISSIONS;
    }

    /**
     * Returns the flag annotation associated with this method
     * @return Flag 
     */
    public int getAnnotation() {
        return isDeprecated ^ isNoReply;
    }

    /**
     * See DBus specification "org.freedesktop.DBus.Deprecated"
     * @return Whether this method is deprecated
     */
    public boolean isDeprecated() {
        return isDeprecated != 0;
    }

    /**
     * See DBus specification "org.freedesktop.DBus.Method.NoReply"
     * @return Whether this method is not intended to reply
     */
    public boolean isNoReply() {
        return isNoReply != 0;
    }

    /**
     * Returns the input signature of this method.
     * IE. aisb
     * @return The input signature of this method.
     */
    public String getInputSignature() {
        StringBuffer buf = new StringBuffer();
        for (Argument<?> arg: mInArguments)
            buf.append(arg.getDBusSignature());
        return buf.toString();
    }

    /**
     * Returns the output signatures of the arguments
     * that will be returned.
     * @return
     */
    public String getOutputSignature() {
        StringBuffer buf = new StringBuffer();
        for (Argument<?> arg: mOutArguments)
            buf.append(arg.getDBusSignature());
        return buf.toString();
    }

    /**
     * @return Ordered list of input parameters (if any)
     */
    public List<Argument<?>> getInputArguments() {
        return new ArrayList<Argument<?>>(mInArguments);
    }

    /**
     * @return Ordered list of output parameters (if any)
     */
    public List<Argument<?>> getOutputArguments() {
        return new ArrayList<Argument<?>>(mOutArguments);
    }

    /**
     * Returns a list of all annotations.
     * @return List of all annotations that pertain to this member
     */
    public List<Annotation> getAnnotations() {
        return new ArrayList<Annotation>(mAnnotions);
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String name = super.toString();

        if (!name.isEmpty()) {
            buf.append(name);
        }

        List<Attributable> list = new ArrayList<Attributable>();
        list.addAll(mInArguments);

        buf.append("(");
        int size = list.size();
        for (int i = 0; i < size; ++i) {
            buf.append(list.get(i).toString());
            if (i != size - 1) {
                buf.append(", ");
            }
        }
        buf.append(")");

        // TODO Print out put
        if (!mOutArguments.isEmpty()) {
            buf.append(" => ");
            size = mOutArguments.size();
            for (int i = 0; i < size; ++i) {
                buf.append(mOutArguments.get(i).toString());
                if (i != size - 1) {
                    buf.append(", ");
                }
            }
        }

        if (!mAnnotions.isEmpty()) {
            buf.append("\n	Annotations:\n");
            for (Annotation a : mAnnotions) {
                buf.append(a.toString() + "\n");
            }
        }

        return buf.toString().trim();
    }

    /**
     * Compares the name of the name with the name 
     * of the member if o is a member.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!o.getClass().equals(getClass())) return false;
        Member m = (Member) o;
        return m.getName().equals(getName()) && m.getInterface().equals(getInterface());
    }

    @Override
    public int hashCode() {
        return getName().hashCode() * getInterface().hashCode();
    }

}
