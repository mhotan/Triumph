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

package org.alljoyn.triumph.util;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;


public class JavaFXUtil {

	/**
	 * JavaFX Split Panes has very weird characteristics where 
	 * SplitPane puts all items in separate stack panes So we have to use the 
	 * following method to find the right node
	 * 
	 * @param split Split pane that contains id
	 * @param id id to find
	 * @param clazz Class of element to find
	 * @return Child Element with split as parent or null if no child exists
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findByIdInSplitPane(SplitPane split, String id, Class<T> clazz) {
		for (Node node : split.getItems()) {
			boolean idEquals = id.equals(node.getId());
			boolean classMatch = node.getClass().isAssignableFrom(clazz);
			
			if (idEquals && classMatch) {
				return (T)node;
			}
		}
		return null;
	}
}
