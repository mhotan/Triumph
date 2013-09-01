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

package org.alljoyn.triumph.util.loaders;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;

/**
 * Class that handles the loading of views from the res folder in the project directory.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public final class ViewLoader {

    private static final Logger LOG = Logger.getLogger(ViewLoader.class.getSimpleName());

    /**
     * Cannot instantiate
     */
    private ViewLoader() {}

    /**
     * Given the instance of the object to the Custom Pane, loads the pane
     * setting the instance as the root and the controller. It is the client responsibility
     * to ensure the instance is the controller and root.
     * <p>
     * The FXML file name is the relative path relative to the instance class
     * file.  
     * <p>
     * Instance cannot be null.
     *
     * @param fxmlFileName The FXML file path relative to the instance class file.  The is the FXML document to load
     * @param instance The Object instance that will be the FXML file.
     */
    public static void loadView(String fxmlFileName, Object instance) {
        try {
            URL resourceURL = ViewLoader.class.getResource("/layouts/" + fxmlFileName);

            // FXML load the fxml layer.
            FXMLLoader fxmlLoader = new FXMLLoader(resourceURL);

            // Root is defined by fx:root is FXML
            fxmlLoader.setRoot(instance);
            fxmlLoader.setController(instance);
            fxmlLoader.load();
        } catch (IOException e) {
            LOG.severe("Unable to load '" + fxmlFileName + "' from layouts directory");
            throw new RuntimeException("Exception caught loading '" + fxmlFileName + "': " + e.getMessage());
        }
    }

    /**
     * Uses the instance to load a view of the same simple name with appended suffix ".fxml".
     * <br>Therefore this is like calling loadView(instance.getClass().getSimpleName() + ".fxml", instance);
     * <br>NOTE: it is up to the client to correctly create a FXML file name with the correct name.\
     * 
     * @param instance Instance to use
     */
    public static void loadView(Object instance) {
        loadView(instance.getClass().getSimpleName() + ".fxml", instance);
    }
    
}
