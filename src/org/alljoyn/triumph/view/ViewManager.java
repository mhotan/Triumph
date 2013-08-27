package org.alljoyn.triumph.view;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

/**
 * Creates a way to track and manage view for distinct model type.
 * 
 * @author mhotan
 *
 * @param <T> Key object that maps directly to View type
 * @param <V> Type of view that corresponds to T
 */
public class ViewManager<T, V extends Node> {

    private final Map<T,V> mMap;
    
    /**
     * 
     */
    public ViewManager() {
        this(new HashMap<T,V>());
    } 
    
    /**
     * 
     * @param manager
     */
    public ViewManager(ViewManager<T,V> manager) {
        this(manager.mMap);
    }
    
    /**
     * 
     * 
     * @param currentViews Currently tracked views.
     */
    public ViewManager(Map<T,V> currentViews) {
        mMap = new HashMap<T,V>(currentViews);
    }
    
    /**
     * 
     */
    public void addView(T element, V view) {
       mMap.put(element, view); 
    }
    
    /**
     * 
     * @return
     */
    public V getViewForElement(T element) {
        return mMap.get(element);
    }
    
}
