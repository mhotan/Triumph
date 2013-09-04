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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;

import org.alljoyn.triumph.util.loaders.ViewLoader;

/**
 * Tabbed Support View.
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class TabbedSupportView extends HBox {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ScrollPane mLogPane;

    @FXML
    private ScrollPane mSignalReceivedPane;

    @FXML
    private TabPane mTabPane;

    public TabbedSupportView() {
        ViewLoader.loadView(this);
        mTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    }
    
    public void setLogView(LogView logView) {
        mLogPane.setContent(logView);
    }
    
    public void setSignalReceivedView(SignalsReceivedView view) {
        mSignalReceivedPane.setContent(view);
    }
    
    @FXML
    void initialize() {
        assert mLogPane != null : "fx:id=\"mLogPane\" was not injected: check your FXML file 'TabbedSupportView.fxml'.";
        assert mSignalReceivedPane != null : "fx:id=\"mSignalReceivedPane\" was not injected: check your FXML file 'TabbedSupportView.fxml'.";
        assert mTabPane != null : "fx:id=\"mTabPane\" was not injected: check your FXML file 'TabbedSupportView.fxml'.";
    }
}
