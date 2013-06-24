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
 * A Wrapper around an attribute of a particular entity
 * <b>IE. An argument to a method can have an attribute...
 * <b>	type=u
 * <b>Where the key is type
 * <b>and the value is u
 * 
 * @author Michael Hotan mhotan@quicinc.com
 */
public class Attribute {
	private final String mKey;
	private String mValue;
	
	/**
	 * Create a wrapper for a particular attribute
	 * @param key Key that points to value
	 * @param value Value to associate with key.
	 */
	public Attribute(String key, String value) {
		assert key != null;
		assert value != null;
		mKey = key;
		mValue = value;
	}
	
	/**
	 * Return whether this attribute is a name
	 * 
	 * @return true if this attribute represents a name
	 */
	public boolean isName() {
		return mKey.equals("name");
	}
	
	public String getKey() {
		return mKey;
	}
	
	public String getValue() {
		return mValue;
	}
	
	public void updateValue(String value) {
		mValue = value;
	}
	
	public Attribute(Node node) {
		this(node.getNodeName(), node.getNodeValue());
	}
	
	@Override
	public String toString() {
		if (isName())
			return mValue == null ? "" : mValue.trim();
		return mKey + "=" + mValue;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!o.getClass().equals(this.getClass())) return false;
		Attribute a = (Attribute) o;
		return a.mKey.equals(this.mKey);
	}
	
	@Override
	public int hashCode() {
		return mKey.hashCode();
	}
}
