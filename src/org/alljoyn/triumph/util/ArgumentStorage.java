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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.alljoyn.triumph.model.components.arguments.Argument;

/**
 * Class that handles the persistent storage of arguments.
 * 
 * @author mhotan
 */
public class ArgumentStorage {

    private static final Logger LOG = Logger.getLogger(ArgumentStorage.class.getSimpleName());


    private static final String BIN_PATH = "bin";
    private static final String DATA_PATH = BIN_PATH + "/" + "data";
    private static final String ARG_DIR_PATH = DATA_PATH + "/" + "args";

    // The root of all directories

    private static ArgumentStorage instance;

    private Map<String, InternalArgumentStorage> mInstances;

    private List<SaveListener> mListeners;
    
    private static Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();

    static {
        //add owners permission
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        //add group permissions
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        //add others permissions
        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OTHERS_WRITE);
    }

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
        //        try {
        File argsDir = new File(ARG_DIR_PATH);
        if (!argsDir.exists()) 
            argsDir.mkdirs();

        mListeners = new ArrayList<ArgumentStorage.SaveListener>();
    }

    /**
     * Returns the internal storage manager that handles the instance
     * 
     * @param argSignature Argument signature for that storage.
     * @return Storage that handles the signature
     * @throws IOException 
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
            try {
                storage = new InternalArgumentStorage(argSignature);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
        
        // Broadcast that an argument was recently saved.
        for (SaveListener list: mListeners)
            list.onSaved(arg);
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

        private final String mPathStr;
        private final File mFile;

        private final ObjectOutputStream mOos;
        
        private InternalArgumentStorage(String argSignature) throws IOException {
            mSignature = argSignature;
            mPathStr = ARG_DIR_PATH + "/" + "args-" + argSignature;
            File f = new File(mPathStr);
            if (!f.exists())
                f.createNewFile();
            mFile = f;
            mKnownArgs = new HashMap<String, Argument<?>>(getArguments(argSignature));

            mOos = new ObjectOutputStream(new FileOutputStream(mFile));
            // Save all the argument that we just uploaded.
            for (String name: mKnownArgs.keySet()) {
                saveArgument(mKnownArgs.get(name));
            }
        }

        /**
         * Used on construction.  Retrieves a mapping of argument name to argument type
         * @param name Signature name.
         * @return Mapping
         */
        private Map<String, Argument<?>> getArguments(String name) {
            Map<String, Argument<?>> mapping = new HashMap<String, Argument<?>>();
            ObjectInputStream is = null;
            try {
                is = new ObjectInputStream(new FileInputStream(mFile));
                while (true) {
                    try {
                        Argument<?> arg = (Argument<?>) is.readObject();
                        mapping.put(arg.getSaveByName(), arg);
                    } catch (EOFException e) {
                        break;
                    } 
                }
            } catch (EOFException e) {
              // Do nothing this is fine.  
            } catch (Exception e) {
                LOG.warning("Exception reading from " + mFile.getPath() + " because of " + e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) { /* Couldn't close move on.*/}
                }
                clearArgumentFile();
            }
            return mapping;
        }

        /**
         * Clear away the file of all of its objects.
         */
        private void clearArgumentFile() {
            try {
                // Wipe away a
                PrintWriter writer = new PrintWriter(mFile);
                writer.print("");
                writer.close();
            } catch (IOException e) {
                LOG.warning("Could not clear the persistent storage " + mFile.getPath());
            }
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
            try {
                mOos.writeObject(arg);
                mKnownArgs.put(arg.getSaveByName(), arg);
            } catch (IOException e) {
                LOG.warning("Unable to save argument: " + arg);
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
    
    public void addListener(SaveListener listener) {
        mListeners.add(listener);
    }
    
    public void removeListener(SaveListener listener) {
        mListeners.remove(listener);
    }

    public static interface SaveListener {
        
        /**
         * Listener for clients to listen for saved arguments.
         * @param savedArg Argument saved
         */
        public void onSaved(Argument<?> savedArg);
        
    }
    
}
