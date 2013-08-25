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

import javafx.scene.control.TreeItem;

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
	 * Cached tree that can help prevent doing unnecessary builds and
	 * wasting computational time. 
	 */
	private TreeItem<AllJoynComponent> mCachedRoot;
	
	/**
	 * Canonical name of this attributes name
	 */
	private String mName;
	
	/**
	 * XML Node of this object.
	 */
	private Node mNode;
	
	public enum TYPE {
		SERVICE, // Usually represented the service well known name
		OBJECT, // Represents a subobjects
		INTERFACE,
		METHOD, 
		SIGNAL, 
		PROPERTY,
		LABEL
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
		mNode = node;
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

	public AllJoynService toService() {
		if (mType != TYPE.SERVICE) return null;
		return (AllJoynService) this;
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
	 * Creates a Tree that best represents this Entity.
	 * This allows the ability to show the user
	 * what this object looks like as a tree.
	 * @return The root of the tree.
	 */
	public abstract TreeItem<AllJoynComponent> toTree();
	
	/**
	 * Gets the tree that has already been created for this instance.
	 * @return Cached tree or null if there cache is empty
	 */
	protected TreeItem<AllJoynComponent> getCachedTree() {
		return mCachedRoot;
	}
	
	/**
	 * Stores this root as a cached version of this tree.
	 * <b>If root is null cache is flushed.
	 * @param root Root of the tree to cache
	 */
	protected void setCachedTree(TreeItem<AllJoynComponent> root) {
		mCachedRoot = root;
	}
	
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
