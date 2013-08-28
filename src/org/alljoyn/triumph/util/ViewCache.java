package org.alljoyn.triumph.util;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.Node;


/**
 * Creates a way to track and manage view for distinct model type.
 * 
 * @author mhotan
 *
 * @param <T> Key object that maps directly to View type
 * @param <V> Type of view that corresponds to T
 */
public class ViewCache<T, V extends Node> {

    private final Map<T,V> mMap;
    
    /**
     * Creates an initially empty cache
     */
    public ViewCache() {
        this(new HashMap<T,V>());
    } 
    
    /**
     * 
     * @param manager Manager to build from
     */
    public ViewCache(ViewCache<T,V> manager) {
        this(manager.mMap);
    }
    
    /**
     * Creates a cache from a mapping
     * 
     * @param currentViews Currently tracked views.
     */
    public ViewCache(Map<T,V> currentViews) {
        mMap = new HashMap<T,V>(currentViews);
    }
    
    /**
     * Add view using the element as the key
     */
    public void addView(T element, V view) {
       mMap.put(element, view); 
    }
    
    /**
     * Attempts to 
     * @return 
     */
    public V getViewForElement(T element) {
        return mMap.get(element);
    }
    
}
