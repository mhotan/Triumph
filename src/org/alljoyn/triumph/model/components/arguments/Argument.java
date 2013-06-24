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

import java.util.ArrayList;
import java.util.List;

import org.alljoyn.triumph.MainApplication;
import org.alljoyn.triumph.model.components.Attributable;
import org.alljoyn.triumph.model.components.Attribute;
import org.alljoyn.triumph.view.argview.ArgumentView;
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
public abstract class Argument<T extends Object> implements Attributable {
	
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
	private final String mName, mType;
	
	/**
	 * List of the attributes.
	 */
	private final List<Attribute> mAttr;
	
	/**
	 * Labels if this argument is an input argument
	 * if not then it is assumed that it is an 
	 * output argument.
	 */
	private final DIRECTION mDirection;
	
	/**
	 * Argument View that represents this argument.
	 */
	private ArgumentView<T> mView;
	
	/**
	 * Payload value of the argument.
	 */
	private T mValue;
	
	/**
	 * Creates an empty argument
	 * 
	 * Name can be null.
	 * 
	 * @param name Associated name of this argument
	 * @param direction Direction of the argument.
	 */
	Argument(String name, DIRECTION direction) {
		if (direction == null)
			throw new IllegalArgumentException("Direction cannot be null");
		
		mName = name;
		mAttr = new ArrayList<Attribute>();
		mDirection = direction;
		mType = null;
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
		mName = n2 != null ? n2.getNodeValue() : null;
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

			// Add the argument attribute
			addAttribute(new Attribute(n));
		}
		
		mType = getDBusTypeSignature();
		mDirection = tmpDir;
	}
	
	public String getName() {
		return mName == null 
				|| mName.equals("") 
				? "<Unknown Name>": mName;
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
	public String getType() {
		if (mType == null) {
			return getAJSignature();
		}
		return mType;
	}
	
	/**
	 * Returns the DBus defined type signature of this argument
	 * @return Type signature if it is found in the attributes of this argument, null if can't be found.
	 */
	protected String getDBusTypeSignature() {
		for (Attribute a: mAttr) {
			if (SIGNATURE.equals(a.getKey())) {
				return a.getValue();
			}
		}
		return null;
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
	public void addAttribute(Attribute attr) {
		if (attr == null) return;
		if (mAttr.contains(attr)) {
			mAttr.remove(attr);
		}
		mAttr.add(attr);
	}

	/**
	 * removes any knowledge of the given attribute
	 * @param name Name of the attribute.
	 */
	public void removeAttribute(Attribute a) {
		mAttr.remove(a);
	}

	/**
	 * Sets the current value of this object to inputted argument.
	 * @param value value to set argument to
	 * @return Error message if error occured or null on success
	 */
	public void setValue(T value) {
		mValue = value;
		MainApplication.getLogger().info(this + " set value to " + mValue);
	}
	
	/**
	 * @return Current value of this object.
	 */
	public T getValue() {
		return mValue;
	}
	
	@Override
	public String toString() {
		return getSignature();
	}
	
	/**
	 * Returns a JavaFx View representation of this argument.
	 * @return Root node of the view.
	 */
	public ArgumentView<T> getView() {
		if (mView == null)
			mView = createJavaFXNode();
		return mView;
	}
	
	/**
	 * Produce a 'new' view that represents this argument.
	 * @return The view associated with this argument.
	 */
	protected abstract ArgumentView<T> createJavaFXNode();

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
	
}
