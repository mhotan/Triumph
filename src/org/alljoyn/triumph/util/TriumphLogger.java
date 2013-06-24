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
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logger intended for tracking information in application
 * @author mhotan
 */
public class TriumphLogger {
	
	/**
	 * 
	 */
	public static void setup() {
		
		// retrieve the global logger
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		
		logger.setLevel(Level.ALL);
		
		try {
			FileHandler handler = new FileHandler("FileHandler.html");
			handler.setFormatter(new HTMLFormatter());
			logger.addHandler(handler);
		} catch (SecurityException | IOException e) {
			// Unable to create handler
		}
		
		ConsoleHandler consoleHandler = new ConsoleHandler();
		logger.addHandler(consoleHandler);
	}
	
}
