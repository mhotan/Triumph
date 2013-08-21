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

package org.alljoyn.triumph.view.argview;

import javafx.scene.control.ListCell;

import org.alljoyn.triumph.model.components.arguments.Argument;

/**
 * Factory that helps produce a UI list item that helps represent an argument.
 * This abstracts away all the details of what the argument is and how values
 * are assigned. 
 * 
 * This cell maintains the ability produce the respective View for a particular argument type.
 * 
 * @author mhotan
 */
public class ArgumentCell extends ListCell<Argument<?>> {

	@Override
	protected void updateItem(final Argument<?> item, boolean empty) {
		super.updateItem(item, empty);

		if (item != null) {
			// This allows the ability for this cell to call 
			// on the argument to decide how to present this view.
			// Each argument can now define its own View.
			// TODO Instead of the getView method.
			// Give each argument an identifier or use a current one.
			// Then create a factory that returns the appropiate ArgumentView for the argument.
			setMaxWidth(Double.MAX_VALUE);
			setGraphic(item.getView());
		} else 
			setText(""); // No item
	}

}
