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

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;

/**
 * Class to load basic views
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public final class ViewLoader {

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
		URL url = instance.getClass().getResource(fxmlFileName);
		if (url == null) {
			throw new RuntimeException("loadView, unable to find file named " + fxmlFileName);
		}
		loadView(url, instance);
	}

	/**
	 * Given the Class of the controller to the Custom Pane, loads the pane that
	 * has the name of the class simple name of the object that.  That is there must exists
	 * a file in the same class directory that is equal to 'clazz.getSimpleName() + .fxml'.
	 * 
	 * @param clazz Instance of the object view to find.
	 */
//	public static void loadView(Class<?> claz) {
//		String name = clazz.getSimpleName();
//		name += ".fxml";
//		loadView(name, clazz);
//	}

	/**
	 * Loads view by URL setting instance as root and controller.
	 * 
	 * @param url URL to load
	 * @param instance Instance to assign.
	 */
	private static void loadView(URL url, Object instance) {
		// FXML load the fxml layer.
		FXMLLoader fxmlLoader = new FXMLLoader(url);

		// Root is defined by fx:root is FXML
		fxmlLoader.setRoot(instance);
		fxmlLoader.setController(instance);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
