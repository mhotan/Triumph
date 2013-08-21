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

import java.util.List;

/**
 * Interface that describes features of a method.
 * @author mhotan
 */
public interface Attributable {

    /**
     * Returns a list of attributes that describes this.
     * @return list of attributes for this
     */
    public List<Attribute> getAttributes();
    
	/**
	 * Adds an attribute to this annotation.
	 * <b>Ignores attributes that are null
	 * @param attr add attributes
	 */
	public void addAttribute(Attribute attr);
	
	/**
	 * Remove attributes
	 * @param attr attribute to remove.
	 */
	public void removeAttribute(Attribute attr);
	
}
