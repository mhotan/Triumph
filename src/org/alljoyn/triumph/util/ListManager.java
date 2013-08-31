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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A management class the maintains the list of a specific type.
 * <br>Make sure that the list is full of unique elements.  
 * <br>Also make sures that no object is unecesarrily removed.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 *
 * @param <T> Type that is contained in the list
 */
public class ListManager<T> {

    private static final Logger LOG = Logger.getLogger(ListManager.class.getSimpleName());
    
    /**
     * List of elements that passes the filter. 
     */
    private final ObservableList<T> mList;
    
    /**
     * Set of elements that do not 
     */
    private final Set<T> mUnusedSet;
    
    /**
     * List of filters for this specific type of list.
     */
    private final List<Filterable<T>> mFilters;
    
    /**
     * Creates an empty list manager.
     */
    public ListManager() {
        mList = FXCollections.observableArrayList();
        mFilters = new ArrayList<Filterable<T>>();
        mUnusedSet = new HashSet<T>();
    }
    
    /**
     * Creates a list manager for a specific list.
     * @param list
     */
    public ListManager(ObservableList<T> list) {
        if (list == null)
            mList = FXCollections.observableArrayList();
        else
            mList = list;
        mFilters = new ArrayList<Filterable<T>>();
        mUnusedSet = new HashSet<T>();
    }
    
    /**
     * Returns the underlying list this manager has.
     * @return the list of underlying items.
     */
    public ObservableList<T> getUnderlyingList() {
        return mList;
    }
    
    public void updateState(List<T> newItems) {
        if (newItems == null) return;
        
        // Iterate through the list of new items
        // check to make sure the item is already
        for (T item: newItems) {
            add(item);
        }
        
        // Get all the elements and
        // check if the new state has the item.
        Set<T> set = new HashSet<T>();
        set.addAll(mList);
        set.addAll(mUnusedSet);
        for (T item: set) {
            if (!newItems.contains(item)) {
                remove(item);
            }
        }
    }
    
    /**
     * Check if the item is contained in this manager
     * 
     * @param item item to check for
     * @return true if this contains this item
     */
    public boolean contains(T item) {
        if (item == null) return false;
        return mList.contains(item) || mUnusedSet.contains(item);
    }
    
    /**
     * Attempts to add to this list.
     * 
     * @param item item to add
     */
    public void add(T item) {
        if (item == null) {
            LOG.warning("Attempting to add null element");
            return;
        }
        
        // If we already contain this item then return
        if (contains(item)) return;

        // Check if the item matches the filter
        if (matchFilters(item))
            mList.add(item);
        else 
            mUnusedSet.add(item);
    }
    
    /**
     * Remove item if it exists.
     * 
     * @param item Item to attempt to remove
     * @return true if item was contained in list, false
     */
    public boolean remove(T item) {
        // Make sure remove is called on both data structures.
        boolean inList = mList.remove(item);
        boolean inSet = mUnusedSet.remove(item);
        return inList || inSet;
    }

    /**
     * Adds a filter to this.
     * @param filter filter to add
     */
    public void addFilter(Filterable<T> filter) {
        if (filter == null) return;
        mFilters.add(filter);
        update();
    }
    
    /**
     * Checks if this contains a filter.
     * 
     * @param filter Filter to find
     * @return true if this has filter, false otherwise
     */
    public boolean hasFilter(Filterable<T> filter) {
        return mFilters.contains(filter);
    }
    
    /**
     * Remove filter from this
     * 
     * @param filter Filter to remove
     * @return true if filter was removed
     */
    public boolean removeFilter(Filterable<T> filter) {
        boolean removed = mFilters.remove(filter);
        update(); // Update the state of the list.
        return removed;
    }
    
    /**
     * 
     * @param item
     * @return
     */
    private boolean matchFilters(T item) {
        boolean matches = true;
        for (Filterable<T> filter: mFilters) {
            matches &= filter.filter(item);
        }
        return matches;
    }
    
    /**
     * Updates the current list with the current filters.
     */
    private void update() {
        List<T> tmpList = new ArrayList<T>(mList);
        
        // iterate over the current list and check
        // if it matches the filters.  If it doesn't
        // remove it and put it in the unused set.
        for (T item: tmpList) {
            if (!matchFilters(item)) {
                mList.remove(item);
                mUnusedSet.add(item);
            }
        }
        
        // iterate over the current unused set.
        // If the item matches the filter add it to the list.
        Set<T> tmpSet = new HashSet<T>(mUnusedSet);
        for (T item: tmpSet) {
            if (matchFilters(item)) {
                mList.add(item);
                mUnusedSet.remove(item);
            }
        }
    }
}
