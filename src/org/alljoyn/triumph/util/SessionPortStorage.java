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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.alljoyn.bus.BusAttachment;

/**
 * Class that manages saving the session ports with their
 * respective Service names.
 * 
 * @author mhotan
 */
public class SessionPortStorage {
	
	private static final Logger LOGGER = Logger.getGlobal();

	private static final String DELIMINATOR = ";;";
	
	private static final String BIN_PATH = "bin";
	private static final String DATA_PATH = BIN_PATH + "/" + "data";
	private static final String FILE_PATH = DATA_PATH + "/" + "ports.txt";
	
	private static SessionPortStorage mInstance;

	/**
	 * Internal Mapping that allows fast access.
	 */
	private final Map<String, Short> mInternalMap;

	/**
	 * Cannot instantiate outside this class
	 */
	private SessionPortStorage() {
		mInternalMap = new HashMap<String, Short>();
		// TODO load internal map

		// Here we will do the brunt of the work.  So we can minimize work done 
		// post this process
		try {
		    File binDir = new File(BIN_PATH);
		    // Make sure the bin directory exists
		    if (binDir.exists() && !binDir.isDirectory()) {
		        binDir.delete();
		    } 
		    if (!binDir.exists()) {
		        binDir.mkdir();
		    }
		    
		    // Make the data directory
		    File dataDir = new File(DATA_PATH);
		    if (!dataDir.exists())
		        dataDir.mkdir();
		    
		    // Make the file if it doesn't exist
			File file = new File(FILE_PATH);
			if (file.exists()) {

				// 1. If the file exists, pre load our map.
				FileReader reader = new FileReader(file);
				BufferedReader br = new BufferedReader(reader);
				String line;
				
				// Read line by line.
				while ((line = br.readLine()) != null) {
					String[] pair = line.split(Pattern.quote(DELIMINATOR));
					
					// Safe for valid line structure
					if (pair.length != 2) {
						LOGGER.warning("Invalid File entry: " + line + " Deliminated value " + pair);
						continue;
					}
					mInternalMap.put(pair[0], Short.valueOf(pair[1]));
				}
				
				br.close();
				reader.close();
				reader = null;
				br = null;
				
				// Clean out the file and then delete it
				// 2. flush the file for all its contents
				file.delete();
			}

			file = new File(FILE_PATH);
			file.createNewFile();
			
			// 3. Write the map again to the file.
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			for (String name: mInternalMap.keySet()) {
				bw.append(name + DELIMINATOR + mInternalMap.get(name));
			}
			bw.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error initializing SessionPortStorage: " + e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException("Error initializing SessionPortStorage: " + e.getMessage());
		}
	}

	/**
	 * @return Singleton instance of this storage
	 */
	private static SessionPortStorage getInstance() {
		if (mInstance == null) {
			mInstance = new SessionPortStorage();
		}
		return mInstance;
	}

	/**
	 * Saves the port associated to the service name.
	 * 
	 * @param serviceName Service name to save port to
	 * @param portNumber Port number to save for the session
	 */
	public static void savePort(String serviceName, short portNumber) {

		// Soft pre conditions allows clients to attempt a complete range of values
		if (serviceName == null || portNumber < 0) return;

		// Save in memory cache.
		getInstance().mInternalMap.put(serviceName, portNumber);
		
		try {
			BufferedWriter bw =  new BufferedWriter(new FileWriter(new File(FILE_PATH), true));
			bw.append(serviceName + DELIMINATOR + portNumber);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			throw new RuntimeException("Error trying to extract port: " + e.getMessage());
		} 
	}

	/**
	 * returns all the known relationships for the all the names and ports.
	 * A Mapping of Service name => port number is returned.
	 * @return Mapping of service name to port number.
	 */
	public static Map<String, Short> getAllPorts() {
		return new HashMap<String, Short>(getInstance().mInternalMap);
	} 

	/**
	 * Attempts to look up Port number of the service name
	 * 
	 * @param serviceName Service name to look for.
	 * @return Port number associated to service or BusAttachment.SESSION_PORT_ANY if it cannot be found
	 */
	public static short getPort(String serviceName) {
		SessionPortStorage storage = getInstance();
		if (storage.mInternalMap.containsKey(serviceName))
			return storage.mInternalMap.get(serviceName);
		return BusAttachment.SESSION_PORT_ANY;
	}
}
