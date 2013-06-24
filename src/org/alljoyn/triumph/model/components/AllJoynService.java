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

/**
 * A wrapper class that pertains to an endpoint or service
 * that can be seen by an established well known name.
 * 
 * @author Michael Hotan mhotan@quicinc.com 
 */
public class AllJoynService extends AllJoynComponent {

	
	private final List<AllJoynObject> mObjects;
	
	/**
	 * Creates an empty Service with no objects
	 * with this name.
	 * 
	 * @param name Name of this service.
	 */
	public AllJoynService(String name) {
		this(name, null);
	}
	
	/**
	 * Create an AllJoyn service with name and nested objects. 
	 * 
	 * @param name Name of the service.
	 * @param objects Objects contained in this service.
	 */
	public AllJoynService(String name, List<AllJoynObject> objects) {
		super(TYPE.SERVICE);
		if (name == null || name.isEmpty()) 
			throw new IllegalArgumentException("AllJoynService, Illegal name: " + name);
		setName(name);
		
		// if the input is null it just creates a new empty list
		// of objects.  No big deal.... I think
		mObjects = objects == null ? new ArrayList<AllJoynObject>() 
				: new ArrayList<AllJoynObject>(objects);
	}
	
	/**
	 * Add all the objects to this service.
	 * @param objects Object to add to this service.
	 */
	public void addAll(List<AllJoynObject> objects) {
		// Adds all the objects to this service.
		mObjects.addAll(objects);
		
		// Sets the owning service to this instance of this object
		for (AllJoynObject obj: mObjects) {
			obj.setService(this);
		}
	}
	
	/**
	 * Attempts to get the object that has the object
	 * path inputted.
	 * 
	 * @param objectPath Complete Object path of the object in search for.
	 * @return AllJoynObject with same object path if it exist, else returns null on none found.
	 */
	public AllJoynObject getObject(String objectPath) {
		for (AllJoynObject object: mObjects) {
			if (object.getName().equals(objectPath))
				return object;
		}
		return null;
	}
	
	/**
	 * @return Whether there exists any objects in this service
	 */
	public boolean isEmpty() {
		return mObjects.isEmpty();
	}
	
	@Override
	public String getString() {
		return getName();
	}
	
	@Override
	public String toString() {
		return "Service: " + getName();
	}

	@Override
	public TreeItem<AllJoynComponent> toTree() {
		TreeItem<AllJoynComponent> root = getCachedTree();
		
		// We have already stored this cached tree.
		if (root == null)
			root = new TreeItem<AllJoynComponent>(this);
		
		// Have to update the cache value if necessary
		List<AllJoynComponent> currentObjects = new ArrayList<AllJoynComponent>();
		ObservableList<TreeItem<AllJoynComponent>> children = root.getChildren();
		for (TreeItem<AllJoynComponent> treeItem : children) {
			currentObjects.add(treeItem.getValue());
		}
		
		// Add all the objects under this root.
		for (AllJoynObject object: mObjects) {
			if (!currentObjects.contains(object))
				children.add(object.toTree());
		}
		
		setCachedTree(root);
		return root;
	}

	public List<AllJoynObject> getObjects() {
		return new ArrayList<AllJoynObject>(mObjects);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!o.getClass().equals(getClass())) return false;
		AllJoynService s = (AllJoynService) o;
		return s.getName().equals(getName());
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}