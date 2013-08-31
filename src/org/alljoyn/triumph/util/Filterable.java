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

/**
 * Interface for creating a general filter.
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 *
 * @param <T> Type to filter for.
 */
public interface Filterable<T>  {

    /**
     * Returns whether or not the object meets the requirement of the filter.
     * 
     * @param object Object to check
     * @return true if the object is filters, false otherwise
     */
    public boolean filter(T object);
    
}
