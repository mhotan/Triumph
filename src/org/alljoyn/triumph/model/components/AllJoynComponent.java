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

import org.w3c.dom.Node;

/**
 * Class encapuslates a specific attribute about a particular service.
 * It also enforces that all subclasses provide a 
 * 
 * TODO: Find a better less ambiguous name.
 * 
 * @author mhotan
 */
public abstract class AllJoynComponent implements Comparable<AllJoynComponent> {

    private final TYPE mType;

    /**
     * Canonical name of this attributes name
     */
    private String mName;

    public enum TYPE {
        SERVICE, // Usually represented the service well known name
        OBJECT, // Represents a subobjects
        INTERFACE,
        METHOD, 
        SIGNAL, 
        PROPERTY
    }

    /**
     * Creates a skeleton Object Attribute named according to 
     * the node passed in. 
     * @param node Node that represents the XML document
     * @param type 
     */
    AllJoynComponent(Node node, TYPE type){
        String name = TriumphAJParser.findName(node);
        mName = name == null ? "" : name;
        mType = type;
    }

    /**
     * Creates an object attribute.
     * 
     * @param type type of the object attribute
     */
    AllJoynComponent(TYPE type){
        assert type != null;
        mType = type;
        mName = "";
    }

    public EndPoint toService() {
        if (mType != TYPE.SERVICE) return null;
        return (EndPoint) this;
    }

    public Method toMethod() {
        if (mType != TYPE.METHOD) return null;
        return (Method) this;
    }

    public Signal toSignal() {
        if (mType != TYPE.SIGNAL) return null;
        return (Signal) this;
    }

    public Property toProperty() {
        if (mType != TYPE.PROPERTY) return null;
        return (Property) this;
    }

    public TYPE getType() {
        return mType;
    }

    /**
     * Specialized String getter intended for showing Users on 
     * a type of user interface.
     * @return Special representational string
     */
    public abstract String getString();


    /**
     * Name of this particular attribute.
     * @param name Name to set the attribute to
     */
    public void setName(String name) {
        mName = name == null ? "" : name;
    }

    /**
     * @return Name of this Attribute.
     */
    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(AllJoynComponent o) {
        return this.mName.compareTo(o.mName);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!o.getClass().equals(this.getClass())) return false;
        AllJoynComponent as = (AllJoynComponent) o;
        return as.getName().equals(this.getName());
    }

    public int hashCode() {
        return this.getClass().getName().hashCode() * getName().hashCode();	
    }
}
