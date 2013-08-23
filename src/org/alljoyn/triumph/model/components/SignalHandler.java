package org.alljoyn.triumph.model.components;

/**
 * Class that represents Signal Handler 
 */
public class SignalHandler {

    /**
     * Signal to handle
     */
    private final Signal mSignal;
    
    private final java.lang.reflect.Method mHandle;
    
    /**
     * Listener for this
     */
    private SignalListener mListener;
    
    private static final String HANDLE_NAME = "handle";
    
    /**
     * Signal Handler for a specific signal.
     * @param signal
     */
    SignalHandler(Signal signal) {
        mSignal = signal;
        
        java.lang.reflect.Method handle = null;
        java.lang.reflect.Method[] methods = getClass().getDeclaredMethods();
        for (java.lang.reflect.Method method: methods) {
            if (method.getName().equals(HANDLE_NAME)) {
                handle = method;
                mHandle = handle;
                return;
            }
        }
        mHandle = null;
        // Should always be able to find the handle.
        // If we dont then something is terribly wrong.
        throw new RuntimeException("Unable to find method " + HANDLE_NAME + "() Was this method's name changed or removed?");
    }
    
    /**
     * Returns the specific method to handle incoming signals.
     * @return The Method handler.
     */
    java.lang.reflect.Method getHandleMethod() {
        return mHandle;
    }
    
    /**
     * The method that it is invoked via the alljoyn JNI on reception of
     * a signal. 
     * 
     * @param args Arguments of the received signal
     */
    public void handle(Object... args) {
        if (mListener == null) return;
        mListener.onSignalReceived(mSignal, args);
    }

    /**
     * Sets the listener for this signal handler.
     * 
     * @param list Listener to set
     */
    public void setListener(SignalListener list) {
        mListener = list;
    }
    
    /**
     * Removes listener for 
     */
    public void removeListener() {
        mListener = null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!o.getClass().equals(getClass())) return false;
        return mSignal.equals(((SignalHandler)o).mSignal);
    }
    
    @Override
    public int hashCode() {
        return mSignal.hashCode();
    }
    
    /**
     * Interface that allows external classes to register for this signal.
     * 
     * @author mhotan, Michael Hotan
     */
    public interface SignalListener {
        
        /**
         * Called everytime this signal is received.
         * 
         * @param signal Signal that was received 
         * @param args The arguments of the signal.
         */
        public void onSignalReceived(Signal signal, Object... args);
        
    }
    
}
