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

import javafx.scene.control.TreeItem;

import org.alljoyn.triumph.model.components.arguments.Argument;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * A class that represents a DBus / Alljoyn 'property'.  A property is a 
 * synonymous to a field of an object.
 * 
 * @author Michael Hotan mhotan@quicinc.com
 */
public class Property extends AllJoynComponent implements Attributable {

	private static final String PROPERTY_LABEL = "property";
	private static final String ACCESS_LABEL = "access";
	
	private boolean readAccess, writeAccess;
	
	/**
	 * List of attributes of that property
	 */
	private final List<Attribute> mAttributes;
	
	/**
	 * Owning interface.
	 */
	private final AllJoynInterface mInterface;
	
	/**
	 * Signature of this Property.
	 */
	protected String mSignature;
	
	/**
	 * @return the owning interface.
	 */
	public AllJoynInterface getInterface() {
		return mInterface;
	}
	
	/**
	 * Creates an empty property with no name
	 * no attributes, and full read write access.
	 */
	Property(AllJoynInterface iface) {
		super(AllJoynComponent.TYPE.PROPERTY);
		mAttributes = new ArrayList<Attribute>();
		mInterface = iface;
		// initially make as secure as possibly
		readAccess = writeAccess = true;
	}
	
	/**
	 * Creates a property object based off this node
	 * @param node Node to build from
	 * @param iface Owning interface
	 */
	Property(Node node, AllJoynInterface iface) {
		this(iface);
		
		// Method must be of type method
		if (!isProperty(node)) 
			throw new IllegalArgumentException("Illegal node for Property Constructor.  " +
					"Node must have name \"property\"");
		
		NamedNodeMap attrList = node.getAttributes();
		int numAttributes = attrList.getLength();
		
		for (int i = 0; i < numAttributes; ++i) {
			Attribute a = new Attribute(attrList.item(i));
			
			// We already are tracking the name.
			if (a.isName()) 
				setName(a.getValue());
			else
				addAttribute(a);
			
			if (ACCESS_LABEL.equals(a.getKey())) {
				String accessPermission = a.getValue();
				readAccess = accessPermission.contains("read");
				writeAccess = accessPermission.contains("write");
			} else if (Argument.SIGNATURE.equals(a.getKey())) {
				mSignature = a.getValue();
			}
		}
		
		if (mSignature == null) {
			throw new RuntimeException("Unable to find the signature of the property " + this);
		}
	}
	
	/**
	 * Returns the signature of this property.
	 * IE if this property is of type int then its signature is 'i'
	 * @return Signature of this property.
	 */
	public String getSignature() {
		return mSignature;
	}
	
	/**
	 * Checks if this property for the containing remote object has read access.
	 * If the 
	 * 
	 * @return true if this property has read access, false if not.
	 */
	public boolean hasReadAccess() {
		return readAccess;
	}
	
	/**
	 * Checks if this property hsa write access.
	 * 
	 * @return true if this property has write access, false if not.
	 */
	public boolean hasWriteAccess() {
		return writeAccess;
	}
	
	@Override
	public void addAttribute(Attribute attr) {
		if (attr == null) return;
		if (mAttributes.contains(attr)) {
			mAttributes.remove(attr);
		}
		mAttributes.add(attr);
	}

	/**
	 * Returns whether this node is considered
	 * to represents a Property.
	 * @param node Speculated Node
	 * @return whether node represents a Property or not.
	 */
	public static boolean isProperty(Node node) {
		String name = node.getNodeName();
		return name != null && name.equals(PROPERTY_LABEL);
	}
	
	@Override
	public String getString() {
		return this.toString();
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		// Append the name
		String name = super.toString();
		if (!name.isEmpty()) {
			buf.append(name);
			buf.append(" ");
		}
		
		// Show all the attributes
		int size = mAttributes.size();
		for (int i = 0; i < size; ++i) {
			buf.append(mAttributes.get(i).toString());
			if (i != size - 1) {
				buf.append("; ");
			}
		}
		
		return buf.toString().trim();
	}

	@Override
	public TreeItem<AllJoynComponent> toTree() {
		return new TreeItem<AllJoynComponent>(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!o.getClass().equals(getClass())) return false;
		Property m = (Property) o;
		return m.getName().equals(getName()) && m.getInterface().equals(getInterface());
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode() * getInterface().hashCode();
	}

}
