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

package org.alljoyn.triumph.view;

import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.Signal;

/**
 * Interface that allows the viewable elements
 * to get updates from the underlying model.
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public interface TriumphViewable {

	/**
	 * Notifies any view component that some aspect of the model 
	 * has changed.  It is up to the view to independently find which
	 * aspect of the model has changed.
	 *   
	 * It is up to the View to find a way to access the data it needs to
	 * update its view.
	 */
	public void update();

	/**
	 * An Error occurred and the user should be notified
	 * @param message Error message
	 */
	public void showError(String message);
	
	/**
	 * Given a particular method expose the appropriate amount of detail.
	 * 
	 * @param method Method to show
	 */
	public void showMethod(Method method);
	
	/**
	 * Given an Signal the view shows the appropriate
	 * fields and members that represent this Signal instance.
	 * 
	 * @param signal Signal to show
	 */
	public void showSignal(Signal signal);
	
	/**
	 * Given an Property the view shows the appropriate
	 * fields and members that represent this Property instance.
	 * 
	 * @param property Property to show
	 */
	public void showProperty(Property property);

/*	*//**
	 * Shows the result of a particular method.
	 * @param result result of a recent invocation.
	 *//*
	public void showResult(Object result);*/
	
	
}
