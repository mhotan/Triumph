package org.alljoyn.triumph.model;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.alljoyn.triumph.util.EndPointFilter;

public class EndPointFilterStorage {

    private static final Logger LOG = Logger.getLogger(EndPointFilterStorage.class.getSimpleName());

    private static final String BIN_PATH = "bin";
    private static final String DATA_PATH = BIN_PATH + "/" + "data";
    private static final String ARG_DIR_PATH = DATA_PATH + "/" + "endpoint-filters";
   
    private static EndPointFilterStorage instance;
    
    private List<SaveListener> mListeners;
    
    private Map<String, EndPointFilter> mKnownFilters;
    
    private final ObjectOutputStream mOos;
    
    private final File mFile;
    
    public static EndPointFilterStorage getInstance() {
        try {
        if (instance == null)
            instance = new EndPointFilterStorage();
        return instance;
        } catch (IOException e) {
            throw new RuntimeException("Unable to create storage " + e);
        }
    }
    
    private EndPointFilterStorage() throws IOException {
        File argsDir = new File(DATA_PATH);
        if (!argsDir.exists()) 
            argsDir.mkdirs();
        
        mFile = new File(ARG_DIR_PATH);
        if (!mFile.exists()) 
            mFile.createNewFile();
        mKnownFilters = new HashMap<String, EndPointFilter>(getFilterMap());
        
        mOos = new ObjectOutputStream(new FileOutputStream(mFile));
        mListeners = new ArrayList<SaveListener>();
        
        for (String name: mKnownFilters.keySet()) {
            saveFilter(mKnownFilters.get(name));
        }
    }
    
    
    
    /**
     * Attempt to save the Filter
     * @param filter Filter to save
     */
    public void saveFilter(EndPointFilter filter) {
        try {
            mOos.writeObject(filter);
            mKnownFilters.put(filter.getSaveByName(), filter);
            
            for (SaveListener list: mListeners) {
                list.onSaved(filter);
            }
            
        } catch (IOException e) {
            LOG.warning("Unable to save argument: " + filter);
        }
    }
    
    /**
     * Return a list of EndPointFilters
     * @return Endpoint filters
     */
    public List<EndPointFilter> getFilters() {
        return new ArrayList<EndPointFilter>(mKnownFilters.values());
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
        public void onSaved(EndPointFilter savedArg);
        
    }   
    
    /**
     * Retrieves all the current filters saved from the file.
     * @return Mapping of save by names to endpoint filters.
     */
    private Map<String, EndPointFilter> getFilterMap() {
        Map<String, EndPointFilter> mapping = new HashMap<String, EndPointFilter>();
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(new FileInputStream(mFile));
            while (true) {
                try {
                    EndPointFilter filter = (EndPointFilter) is.readObject();
                    mapping.put(filter.getSaveByName(), filter);
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
            clearFile();
        }
        return mapping;
    }
    
    /**
     * Clear away the file of all of its objects.
     */
    private void clearFile() {
        try {
            // Wipe away a
            PrintWriter writer = new PrintWriter(mFile);
            writer.print("");
            writer.close();
        } catch (IOException e) {
            LOG.warning("Could not clear the persistent storage " + mFile.getPath());
        }
    }
}
