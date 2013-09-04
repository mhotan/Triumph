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

import java.net.URL;
import java.util.ResourceBundle;

import org.alljoyn.triumph.util.loaders.ViewLoader;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class CustomVBox extends VBox {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Pane BottomPane;

    @FXML
    private ScrollPane MiddlePane;

    @FXML
    private Pane TopPane;

    /**
     * Builds a custom Vbox
     */
    public CustomVBox(Pane top, Pane bottom) {
        ViewLoader.loadView(this);
        
        TopPane.prefHeightProperty().bind(top.prefHeightProperty());
        top.prefWidthProperty().bind(TopPane.widthProperty());
        TopPane.getChildren().add(top);
        
        // Adjust the bottom pane appropiately
        BottomPane.prefHeightProperty().bind(bottom.prefHeightProperty());
        bottom.prefWidthProperty().bind(BottomPane.widthProperty());
        BottomPane.getChildren().add(bottom);
        
        MiddlePane.managedProperty().bind(MiddlePane.visibleProperty());
    }
    
    /**
     * Cet the focal point pane.
     * @param center Center pane
     */
    public void setCenterPane(Pane center) {
        if (center == null) {
            MiddlePane.setVisible(false);
            return;
        }
        
        // Sets the visibility.
        MiddlePane.setContent(center);
        MiddlePane.setVisible(true);
    }
    
    /**
     * Attempts to remove center pane.
     * 
     * @param center Center pane to remove
     * @return true if center is within the MiddlePane
     */
    public boolean removeCenterPane(Pane center) {
        if (center == null) return false;
        Pane content = (Pane) MiddlePane.getContent();
        if (content.equals(center)) {
            MiddlePane.setContent(new MessagePane("Connection lost!", ""));
            return true;
        }
        return false;
    }
    
    @FXML
    void initialize() {
        assert BottomPane != null : "fx:id=\"BottomPane\" was not injected: check your FXML file 'CustomVBox.fxml'.";
        assert MiddlePane != null : "fx:id=\"MiddlePane\" was not injected: check your FXML file 'CustomVBox.fxml'.";
        assert TopPane != null : "fx:id=\"TopPane\" was not injected: check your FXML file 'CustomVBox.fxml'.";
    }

}
