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

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import org.alljoyn.triumph.util.loaders.ViewLoader;

public class MessagePane extends VBox {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label mMessage;

    @FXML
    private Label mTitle;
    
    public MessagePane(String title, String message) {
        ViewLoader.loadView(this);
        mTitle.setText(title == null ? "" : title);
        mMessage.setText(message == null ? "" : message);
    }
    
    @FXML
    void initialize() {
        assert mMessage != null : "fx:id=\"mMessage\" was not injected: check your FXML file 'MessagePane.fxml'.";
        assert mTitle != null : "fx:id=\"mTitle\" was not injected: check your FXML file 'MessagePane.fxml'.";
    }
}
