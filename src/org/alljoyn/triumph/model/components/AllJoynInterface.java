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

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import org.alljoyn.triumph.MainApplication;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents the Interface definition that Alljoyn/Dbus uses
 * 
 * @author Michael Hotan mhotan@quicinc.com
 */
public class AllJoynInterface extends AllJoynComponent {

	private static final String LABEL = "interface";

	/**
	 * List of Methods associated with this interface
	 */
	private final List<Method> mMethods;
	
	/**
	 * List of Signal associated with this interface
	 */
	private final List<Signal> mSignals;
	
	/**
	 * List of properties associated with this interface
	 */
	private final List<Property> mProperties;
	
	/**
	 * A List of specific annotations that pertains to this interface
	 */
	private final List<Annotation> mAnnotations;
	
	
	private final AllJoynObject mOwner;

	/**
	 * Creates an empty interface associated with name
	 * @param name Name to associate to interface
	 */
	public AllJoynInterface(String name, AllJoynObject owner) {
		super(TYPE.INTERFACE);

		if (owner == null)
			throw new NullPointerException("AllJoynInterface constructor, Null owner");
		if (name == null)
			throw new IllegalArgumentException("AllJoynInterface constructor, Null name of interface not valid!");

		mOwner = owner;
		mMethods = new ArrayList<Method>();
		mSignals = new ArrayList<Signal>();
		mProperties = new ArrayList<Property>();
		mAnnotations = new ArrayList<Annotation>();
		setName(name);
	}

	/**
	 * Creates an alljoyn interface 
	 * @param node
	 */
	AllJoynInterface(Node node, AllJoynObject owner) {
		super(node, TYPE.INTERFACE);

		if (owner == null)
			throw new NullPointerException("AllJoynInterface constructor, Null owner");

		mOwner = owner;
		mMethods = new ArrayList<Method>();
		mSignals = new ArrayList<Signal>();
		mProperties = new ArrayList<Property>();
		mAnnotations = new ArrayList<Annotation>();

		// Method must be of type method
		if (!isInterface(node)) 
			throw new IllegalArgumentException("Illegal node for " 
					+ getClass().getSimpleName() + " Constructor.  " +
					"Node must have name \"" + LABEL + "\"");

		// iterate through all the immediate children 
		// and place that particular child in 
		// the correct list
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0;i < numChildren; i++) {
			Node child = children.item(i);

			// Hmm something wierd like META data perhaps
			if (Node.ELEMENT_NODE != child.getNodeType()) {
				continue; // skip anything that isn't an ELEMENT
			}

			if (Method.isMethod(child)){
				// The child is a method so add it to the 
				// child list
				mMethods.add(new Method(child, this));
			} 
			else if (Signal.isSignal(child)){
				mSignals.add(new Signal(child, this));
			} 
			else if (Property.isProperty(child)){
				mProperties.add(new Property(child, this));
			}
			else if (Annotation.isAnnotation(child)) {
				mAnnotations.add(new Annotation(child));
			} else 
				MainApplication.getLogger().warning("AllJoynInterface: Unknown node " + child);
		} // end for
	}

	/**
	 * Returns the owning object 
	 * @return Owning object of this interface.
	 */
	public AllJoynObject getObject() {
		return mOwner;
	}

	/**
	 * determines whether the given node is an interface
	 * @param node The node in questions
	 * @return Whether this node is an interface or not.
	 */
	static boolean isInterface(Node node) {
		String name = node.getNodeName();
		return name != null && name.equals(LABEL);
	}

	/**
	 * Adds the method only if it does not already exists
	 * 
	 * @param m Method to attempt to add
	 */
	public void addMethod(Method m) {
		if (mMethods.contains(m)) return;
		mMethods.add(m);
	}

	/**
	 * Adds a signal only if it does not already exists
	 * 
	 * @param s Signal to attempt to add
	 */
	public void addSignal(Signal s) {
		if (mSignals.contains(s)) return;
		mSignals.add(s);
	}

	/**
	 * Adds a property only if it does not already exists
	 * 
	 * @param p Property to attempt to add
	 */
	public void addProperty(Property p) {
		if (mProperties.contains(p)) return;
		mProperties.add(p);
	}

	/**
	 * Interfaces can be secure through 
	 * @return Whether this annotation is secure
	 */
	public boolean isSecure() {
		for (Annotation a : mAnnotations) {
			if (a.isSecure()) return true;
		}
		return false;
	}

	/**
	 * Checks whether a member exists by name
	 * 
	 * @param memberName Name of the member to find
	 * @return true if member exists, false if not
	 */
	public boolean hasMember(String memberName) {
		return getMember(memberName) != null;
	}
	
	/**
	 * Checks whether a property exists by name.
	 * 
	 * @param propertyName Name of the property to find
	 * @return true if property exists, false if not
	 */
	public boolean hasProperty(String propertyName) {
		return getProperty(propertyName) != null;
	}
	
	/**
	 * @return A list of all the methods of this interface
	 */
	public List<Method> getMethods() {
		return new ArrayList<Method>(mMethods);
	}
	
	/**
	 * @return A list of all the signals of this interface
	 */
	public List<Signal> getSignals() {
		return new ArrayList<Signal>(mSignals);
	}
	
	/**
	 * @return A list of all the properties of this interface
	 */
	public List<Property> getProperties() {
		return new ArrayList<Property>(mProperties);
	}
	
	/**
	 * Gets the member of this Interface that has the name 
	 * that is requested.
	 * 
	 * @param memberName The name of the member to find.
	 * @return the member associated with member name, Null if not member is found
	 */
	public Member getMember(String memberName) {
		for (Method m: mMethods) {
			if (m.getName().equals(memberName))
				return m;
		}
		for (Signal s: mSignals) {
			if (s.getName().equals(memberName))
				return s;
		}
		return null;
	}
	
	/**
	 * Gets the property with an associated name.
	 * 
	 * @param propertyName name of the property to return.
	 * @return the Property if it exists, else null.
	 */
	public Property getProperty(String propertyName) {
		for (Property prop: mProperties) {
			if (prop.getName().equals(propertyName))
				return prop;
		}
		return null;
	}
	
	@Override
	public String getString() {
		// return the name of the object.
		return getName();
	}

	@Override
	public TreeItem<AllJoynComponent> toTree() {

		// Create the root for this object
		TreeItem<AllJoynComponent> label = new TreeItem<AllJoynComponent>(this);
		ObservableList<TreeItem<AllJoynComponent>> children = label.getChildren();

		// Add a node for methods if they exists
		if (!mMethods.isEmpty()) {
			TreeItem<AllJoynComponent> methodLabel = new TreeItem<AllJoynComponent>(new Label("Methods"));
			for (Method m : mMethods) 
				methodLabel.getChildren().add(m.toTree());
			children.add(methodLabel);
		}

		if (!mSignals.isEmpty()) {
			TreeItem<AllJoynComponent> signalLabel = new TreeItem<AllJoynComponent>(new Label("Signals"));
			for (Signal s : mSignals)
				signalLabel.getChildren().add(s.toTree());
			children.add(signalLabel);
		}

		if (!mProperties.isEmpty()) {
			TreeItem<AllJoynComponent> propertyLabel = new TreeItem<AllJoynComponent>(new Label("Properties"));
			for (Property p: mProperties) 
				propertyLabel.getChildren().add(p.toTree());
			children.add(propertyLabel);
		}
		return label;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!o.getClass().equals(getClass())) return false;
		AllJoynInterface s = (AllJoynInterface) o;
		return s.getName().equals(getName()) && s.getObject().equals(getObject());
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode() * getObject().hashCode();
	}
	
}
