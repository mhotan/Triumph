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

import org.alljoyn.triumph.MainApplication;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Represents a general annotation that can be made
 * on a general entity.
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class Annotation implements Attributable {
	
	private static final String ANNOTATION_LABEL = "annotation";
	
	/**
	 * Labels if element is annotated as being deprecated.
	 * See DBus specification for description.
	 */
	public static final String DEPRECATED = "org.freedesktop.DBus.Deprecated";
	
	/**
	 * Labels if element is annotated as to possibly not going to reply
	 * See DBus specification for description.
	 */
	public static final String NOREPLY = "org.freedesktop.DBus.Method.NoReply";
	
	/**
	 * Labels if the element is annotated as a secure element
	 */
	public static final String SECURE = "org.alljoyn.Bus.Secure";
	
	/**
	 * Specific name of the annotation
	 */
	private final String mName, mValue;
	
	/**
	 * List of all attributes of this annotation.
	 * IE a common one is value-"<true | false>"
	 */
	private final List<Attribute> mAttributes;
	
	/**
	 * Creates an annotation without and attributes.
	 */
	public Annotation(String name, String value) {
		mValue = value;
		mName = name;
		mAttributes = new ArrayList<Attribute>();
	}
	
	/**
	 * Builds a Annotation from a node created form an XML introspect.
	 * 
	 * @param node Node to 
	 */
	Annotation(Node node) {
		if (!"annotation".equals(node.getNodeName())) {
			throw new IllegalArgumentException("Node does not represent an Annotation object");
		}
		NamedNodeMap attrList = node.getAttributes();
		
		// Attempt to find the name and value literal values from the DOM parse.
		Node nameNode = attrList.getNamedItem("name");
		Node valueNode = attrList.getNamedItem("value");
		
		// Extract the annotation name
		if (nameNode != null && nameNode.getNodeValue() != null) 
			mName = nameNode.getNodeValue();
		else {
			MainApplication.getLogger().warning("Unable to find name for annotation " + node);
			mName = "";
		}
		
		// Extract the annotation value
		if (valueNode != null && valueNode.getNodeValue() != null) {
			mValue = valueNode.getNodeValue();
		} else {
			MainApplication.getLogger().warning("Unable to find value for annotation " + node);
			mValue = "";
		}
		
		mAttributes = new ArrayList<Attribute>();
		for (int i = 0; i < attrList.getLength(); ++i) {
			Attribute a = new Attribute(attrList.item(i));
			if (a.isName()) continue; // Attribute name
			mAttributes.add(a); // Non name attribute goes on the list
		}
	}
	
	public String getName() {
		return mName;
	}
	
	public String getValue() {
		return mValue;
	}
	
	/**
	 * Returns whether this node is considered
	 * to represents a Annotation.
	 * @param node Speculated Node
	 * @return whether node represents a signal or not.
	 */
	public static boolean isAnnotation(Node node) {
		String name = node.getNodeName();
		return name != null && name.equals(ANNOTATION_LABEL);
	}
	
	/**
	 * Whether there exist an attribute that has a 
	 * key value of the input argument
	 * 
	 * @param key Key value of attribute
	 * @return Whether is the value associated with key is true
	 */
	private boolean isTrue(String key) {
		for (Attribute a : mAttributes) {
			if (key.equals(a.getKey())) {
				return "true".equals(a.getValue());
			}
		}
		return false;
	}
	
	/**
	 * If this annotation represents a Deprecation annotation
	 * returns the value associated with this annotation.  If it does 
	 * not represent a Deprecated annotation then false is returned.
	 * @return Whether this annotation represents a deprecated element.
	 */
	public boolean isDeprecated() {
		if (!mName.equals(DEPRECATED))
			return false;
		return isTrue("value");
	}
	
	/**
	 * If this annotation represents a No reply annotation
	 * then the boolean value associated with this annotation is returned.
	 * If it does not represent a No Reply annotation then false is returned.
	 * 
	 * @return Whether this annotation represents a no reply
	 */
	public boolean isNoReply() {
		if (!mName.equals(NOREPLY)) 
			return false;
		return isTrue("value");
	}
	
	/**
	 * If this annotation represents a Secure annotation
	 * then the boolean value associated with this annotation is returned.
	 * If it does not represent a No Reply annotation then false is returned.
	 * 
	 * @return Whether this annotation represents a secure element
	 */
	public boolean isSecure() {
		if (!mName.equals(SECURE)) 
			return false;
		return isTrue("value");
	}
	
	
	@Override
	public List<Attribute> getAttributes() {
		return new ArrayList<Attribute>(mAttributes);
	}
	
	@Override
	public void addAttribute(Attribute attr) {
		if (attr == null) return;
		if (mAttributes.contains(attr)) {
			mAttributes.remove(attr);
		}
		mAttributes.add(attr);
	}
	
	@Override
    public void removeAttribute(Attribute attr) {
	    mAttributes.remove(attr);
    }
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
//		buf.append("annotation ");
		if (!mName.isEmpty()) {
			buf.append(mName);
			buf.append(" ");
		}
		for (Attribute attr: mAttributes) {
			buf.append(attr);
			buf.append(" ");
		}
		return buf.toString().trim();
	}

}
