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
package org.alljoyn.triumph;
import java.io.IOException;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import org.alljoyn.triumph.controller.ViewController;
import org.alljoyn.triumph.model.TriumphModel;
import org.alljoyn.triumph.util.loaders.NativeLoader;

/**
 * The main application class that will be in charge of a set key initial and control tasks.
 * For example:
 * 	1. Will start the main application.
 * 	2. load the fxml file into the application
 * 
 * Initiate any model instance 
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class MainApplication extends Application {

    // Statically load All Joyn java library
    static {
        NativeLoader loader = new NativeLoader();
        loader.loadLibrary("triumph");
        loader.loadLibrary("alljoyn_java");
    }

    /**
     * Logger for the stats.
     */
    private static final Logger LOGGER = Logger.getLogger("triumph");

    /**
     * @return The single instance of the logger that pertains to this 
     * application
     */
    public static Logger getLogger() {
        return LOGGER;
    }
    /**
     * Title for the application.
     */
    static final String TITLE = "Triumph";

    /**
     * @param args Unused command line arguments
     */
    public static void main(String[] args) {
        Application.launch(MainApplication.class, args);
    }

    /**
     * Tree of all the services that are presently seen
     */
    private TreeView<String> mTreeView;

    /**
     * @return TreeView of all the current Buses.
     */
    public TreeView<String> getServiceTreeView() {
        return mTreeView;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle(TITLE);		
        TriumphModel model = TriumphModel.getInstance();
        ViewController controller = new ViewController(primaryStage);
        model.addView(controller);
    }
}
