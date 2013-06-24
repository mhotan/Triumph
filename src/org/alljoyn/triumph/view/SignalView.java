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
import org.alljoyn.triumph.model.components.Signal;
import org.alljoyn.triumph.model.components.arguments.Argument;

/**
 * The view that represents a single Signal member of the interface. 
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class SignalView extends MemberView {

	/**
	 * Internal Signal reference to the view.
	 */
	private final Signal mSignal;
	
	/**
	 * Create's a signal view for the signal instance.
	 * 
	 * @param signal signal to present.
	 */
	public SignalView(Signal signal) {
		super(signal);
		mSignal = signal;
	
		
	}

	@Override
	protected String getMemberTypeName() {
		return "Signal";
	}

	@Override
	@FXML
	protected void invoke() throws BusException {
		// Use the model as the call back to emit the signal
		Argument<?>[] outArgs = getOutputArguments();
		TriumphModel.getInstance().onEmitSignal(mSignal, outArgs);
	}

}
