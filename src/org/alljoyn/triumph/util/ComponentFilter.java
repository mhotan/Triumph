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

import org.alljoyn.triumph.model.components.AJObject;
import org.alljoyn.triumph.model.components.Interface;
import org.alljoyn.triumph.model.components.InterfaceComponent;

/**
 * A filter that 
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class ComponentFilter implements Filterable<InterfaceComponent> {

    /**
     * The object that contains the component in question.
     */
    private AJObject mObject;
    
    /**
     * The Interface that has the component in question.
     */
    private Interface mIface;
    
    /**
     * The Name or prefix of the name the component starts with.
     */
    private String mName;
    
    /**
     * Flag to show methods.
     */
    private boolean mShowMethods;
    
    /**
     * Flag to show signals.
     */
    private boolean mShowSignals;
    
    /**
     * Flag to show properties
     */
    private boolean mShowProperties;
    
    public ComponentFilter() {
        mObject = null;
        mIface = null;
        mName = "";
        mShowMethods = true;
        mShowSignals = true;
        mShowProperties = true;
    }
    
    public boolean showMethods() {
        return mShowMethods;
    }
    
    public boolean showSignals() {
        return mShowSignals;
    }
    
    public boolean showProperties() {
        return mShowProperties;
    }
    
    public String getCurrentName() {
        return mName;
    }
    
    @Override
    public boolean filter(InterfaceComponent component) {
        // If the Object is null then the user is requesting to see
        // all the objects.
        if (mObject != null && !component.belongsToObject(mObject))
            return false;
        
        // If the interface is null then the user is requesting to
        // all the interfaces.
        if (mIface != null && !component.belongsToInterface(mIface))
            return false;
        // Compare the lower case version of the component name
        if (!component.getName().toLowerCase().startsWith(mName))
            return false;
        
        // Depending on the type of the interface component check
        // if the current filter is set to show the specific type.
        switch (component.getType()) {
        case METHOD:
            return mShowMethods;
        case PROPERTY:
            return mShowProperties;
        case SIGNAL:
            return mShowSignals;
        default:
            break;
        }
        return true;
    }
    
    /**
     * Sets this filter to look for only components with this name
     * 
     * @param name name of component to look for.
     */
    public void setName(String name) {
        if (name == null) 
            name = "";
        mName = name.trim().toLowerCase();
    }

    /**
     * Sets the object to use in the filter.  Any future
     * InterfaceComponent will be checked to see
     * if the object it belongs to is the same as this one.
     * If the object argument is null then there will be no filter put on the 
     * object the component belongs to.
     * 
     * @param object Object to use
     */
    public void setObject(AJObject object) {
        mObject = object;
    }
    
    /**
     * Sets the interface to use in the filter.  Any future
     * InterfaceComponent will be checked to see
     * if the interface it belongs to is the same as this one.
     * If the interface argument is null then there will be no filter put on the 
     * object the component belongs to.
     * 
     * @param iface Interface to filter for
     */
    public void setInterface(Interface iface) {
        mIface = iface;
    }
    
    /**
     * Boolean flag to set if the they want to show methods.
     * 
     * @param show boolean flag to set to show methods
     */
    public void setShowMethods(boolean show) {
        mShowMethods = show;
    }
    
    /**
     * Boolean flag to set to show properties
     * 
     * @param show boolean flag to set to show properties
     */
    public void setShowProperties(boolean show) {
        mShowProperties = show;
    }
    
    /**
     * 
     * @param show boolean flag to set to show signals
     */
    public void setShowSignals(boolean show) {
        mShowSignals = show;
    }
    
    /**
     * Sets the flag for all interface components.
     * 
     * @param show boolean flag to set to show all interface components
     */
    public void setShowAll(boolean show) {
        if (show) {
            setShowMethods(show);
            setShowProperties(show);
            setShowSignals(show);
        }
    }
}
