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

package org.alljoyn.triumph.view;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

import org.alljoyn.triumph.model.components.AllJoynComponent;

/**
 * Treecell factory the manages the max display width
 * @author mhotan
 */
public class AllJoynTreeCellFactory implements Callback<TreeView<AllJoynComponent>, TreeCell<AllJoynComponent>> {

    private double maxWidth;
    
    public AllJoynTreeCellFactory() {
        maxWidth = -1;
    }
    
    public void setMaxWidth(double width) {
        if (width < maxWidth) {
            return;
        }
        maxWidth = width;
    }
    
    public double getMaxWidth() {
        return maxWidth;
    }
    
    @Override
    public TreeCell<AllJoynComponent> call(TreeView<AllJoynComponent> param) {
        // Create a TreeCell with a back reference to this factory.
        return new AllJoynComponentTreeCell(this);
    }

}
