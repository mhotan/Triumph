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

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alljoyn.triumph.model.components.arguments.Argument;

import com.sun.istack.internal.logging.Logger;

/**
 * Class that handles the persistent storage of arguments.
 * 
 * @author mhotan
 */
public class ArgumentStorage {

    private static final Logger LOG = Logger.getLogger(ArgumentStorage.class);

    // The root of all directories
    private static final String ROOT_DIR = "bin/data/args";

    private static ArgumentStorage instance;

    private Map<String, InternalArgumentStorage> mInstances;

    /*
     * To keep track of the different type of arguments.
     * This storage has a reference to internal storage managers.
     * There is one internal manager for each argument type.
     * This way each file that hold argument objects only hold arguments
     * of the same type.
     * 
     * Therefore this class is 
     */

    public static ArgumentStorage getInstance() {
        if (instance == null)
            instance = new ArgumentStorage();
        return instance;
    }

    private ArgumentStorage() {
        try {
            Files.createDirectories(Paths.get(ROOT_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize ArgumentStorage because " + e.getMessage());
        }
    }

    /**
     * Returns the internal storage manager that handles the instance
     * 
     * @param argSignature Argument signature for that storage.
     * @return Storage that handles the signature
     */
    private InternalArgumentStorage getStorage(String argSignature) {
        if (argSignature == null)
            throw new NullPointerException("cannot have a argument signature as null");

        // If this is the first ever request.
        if (mInstances == null) {
            mInstances = new HashMap<String, InternalArgumentStorage>();
        } 
        // check if we already have a storage.
        InternalArgumentStorage storage = mInstances.get(argSignature);
        if (storage == null) {
            storage = new InternalArgumentStorage(argSignature);
            mInstances.put(argSignature, storage);
        }
        return storage;
    }

    /**
     * Saves the argument persistently.
     * @param arg Argument to save
     */
    public void saveArgument(Argument<?> arg) {
        String dbusSignature = arg.getDBusSignature();
        InternalArgumentStorage storage = getStorage(dbusSignature);
        storage.saveArgument(arg);
    }
    
    /**
     * Checks whether there is already an argument that resembles 
     * this argument.
     * 
     * @param arg Argument to check for
     * @return whether the Argument already exists
     */
    public boolean hasArgument(Argument<?> arg) {
        String dbusSignature = arg.getDBusSignature();
        InternalArgumentStorage storage = getStorage(dbusSignature);
        return storage.hasArgument(arg.getSaveByName());
    }

    /**
     * Returns a list of all the arguments that match the signature
     * 
     * @param signature Signature to match
     * @return Return list of arguments 
     */
    public List<Argument<?>> getArguments(String signature) {
        if (signature == null) 
            throw new NullPointerException("Illegal Null Signature");
        if (signature.length() == 0) 
            throw new IllegalArgumentException("Illegal signature requested '" + signature + "'" );

        InternalArgumentStorage storage = getStorage(signature);
        return storage.getArguments();
    }

    /**
     * Internal Storage manager that handles a specific Argument group with a specific type
     * signature.
     * @author mhotan
     */
    private class InternalArgumentStorage {

        private final String mSignature;

        /**
         * Mapping of saved by names and correlated
         * argument.
         */
        private Map<String, Argument<?>> mKnownArgs;

        /**
         * 
         */
        private final Path mPath;

        private InternalArgumentStorage(String argSignature) {
            mSignature = argSignature;
            mPath = Paths.get(ROOT_DIR + "/" + "args-" + argSignature);
            mKnownArgs = new HashMap<String, Argument<?>>(getArguments(argSignature));
        }

        /**
         * Used on construction.
         * @param name Signature name.
         * @return
         */
        private Map<String, Argument<?>> getArguments(String name) {
            Map<String, Argument<?>> mapping = new HashMap<String, Argument<?>>();
            ObjectInputStream is = null;
            try {
                is = new ObjectInputStream(Files.newInputStream(
                        mPath, StandardOpenOption.CREATE, StandardOpenOption.READ));
                while (true) {
                    try {
                        Argument<?> arg = (Argument<?>) is.readObject();
                        mapping.put(arg.getSaveByName(), arg);
                    } catch (EOFException e) {
                        break;
                    } 
                }
            } catch (Exception e) {
                LOG.warning("Exception reading from " + mPath + " because of " + e.getMessage());
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) { /* Couldn't close move on.*/}
                }

                // Delete the old file
                try {
                    Files.deleteIfExists(mPath);
                } catch (IOException e) {
                    LOG.warning("Could not delete the file to start again.");
                }
            }
            return mapping;
        }

        /**
         * 
         * @param name Name to check for
         * @return true if name already exists.
         */
        boolean hasArgument(String name) {
            return mKnownArgs.containsKey(name);
        }
        
        /**
         * Attempt to save the argument
         * @param arg Argument to save
         */
        void saveArgument(Argument<?> arg) {
            
            mKnownArgs.put(arg.getSaveByName(), arg);
            ObjectOutputStream os = null;
            try {
                os = new ObjectOutputStream(Files.newOutputStream(
                        mPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE));
                os.writeObject(arg);
            } catch (IOException e) {
                LOG.warning("Could not save argument " + arg.getSignature() + " because of " + e.getMessage());
            } finally {
                if (os != null) {
                    try { // Attempt to close.
                        os.close();
                    } catch (IOException e) {}
                }
            }
        }

        /**
         * Return a list of arguments
         * @return
         */
        List<Argument<?>> getArguments() {
            return new ArrayList<Argument<?>>(mKnownArgs.values());
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!o.getClass().equals(getClass()));
            InternalArgumentStorage i = (InternalArgumentStorage)o;
            return i.mSignature.equals(mSignature);
        }

        @Override
        public int hashCode() {
            return mSignature.hashCode();
        }
    }

}
