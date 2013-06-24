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

import javafx.fxml.FXML;

import org.alljoyn.bus.BusException;
import org.alljoyn.triumph.model.TriumphModel;
import org.alljoyn.triumph.model.components.Method;

import java.util.logging.Logger;

/**
 * Basic view to represent a Method
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class MethodView extends MemberView {

	private static final Logger LOGGER = Logger.getGlobal();
	
	private final Method mMethod;
	
	/**
	 * 
	 * @param method Method to create view around.
	 */
	public MethodView(Method method) {
		super(method);
		mMethod = method;
	}

	@Override
	protected String getMemberTypeName() {
		return Method.class.getSimpleName();
	}

	@Override
	@FXML
	protected void invoke() throws BusException {
		// TODO Collect all the arguments and 
		TriumphModel model = TriumphModel.getInstance();
		Object result = model.onMethodInvoked(mMethod, getInputArguments());
		
		LOGGER.info("Result of method '" + mMethod.getName() + "' is " + result.toString());
	}

}
