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

import java.util.List;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Status;
import org.alljoyn.triumph.model.TriumphModel;
import org.alljoyn.triumph.model.components.Signal;
import org.alljoyn.triumph.model.components.SignalHandlerManager;
import org.alljoyn.triumph.model.components.arguments.Argument;

/**
 * The view that represents a single Signal member of the interface. 
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class SignalView extends MemberView {

    private static final Logger LOG = Logger.getLogger(SignalView.class.getSimpleName());
    
    /**
     * Internal Signal reference to the view.
     */
    private final Signal mSignal;

    private final CheckBox sessionlessBox, receiveBox;
    
    private final SignalHandlerManager mManager;
    
    /**
     * Create's a signal view for the signal instance.
     * 
     * @param signal signal to present.
     */
    public SignalView(Signal signal) {
        super(signal);
        mSignal = signal;
     // get the manager for signals.
        mManager = TriumphModel.getInstance().getSignalHandlerManager();
        boolean hasSignalHandler = mManager.hasSignalHandler(mSignal);
        
        // Make sure output arguments are not editable 
        // and input argument are editable
        setIntputArgumentEditability(false);
        setOutputArgumentEditability(true);
        
        // Add all additional Ui elements.
        sessionlessBox = new CheckBox("Send Sessionless");
        sessionlessBox.setSelected(false);
        mButtonBar.getChildren().add(sessionlessBox);
        
        receiveBox = new CheckBox("Receive Signal");
        // If there exists a signal handler for this signal
        // Make sure we notify the user.
        receiveBox.setSelected(hasSignalHandler);
        receiveBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable,
                    Boolean oldValue, Boolean newValue) {
                if (newValue == null) return;
                
                // If the user wishes to recieve signals notify the user
                boolean receiveSignals = newValue.booleanValue();
                if (receiveSignals) {
                    // Attempt to add the signal handler
                    Status status = mManager.addSignalHandler(mSignal);
                    if (status != Status.OK) {
                        // Something wrong happened
                        LOG.severe("Unable add Signal handler for " + mSignal + " Status: " + status);
                        receiveBox.setSelected(false);
                    }
                } else {
                    // remove the signal handler if it exists.
                    mManager.removeSignalHandler(mSignal);
                }
            }
        });
        mButtonBar.getChildren().add(receiveBox);
    }

    @Override
    @FXML
    protected void invoke() throws BusException {
        
        StringBuffer buf = new StringBuffer();
        for (LoadableArgumentView view : mOutputArgs) {
            String error = view.getCurrentView().onSetCurrentValue();
            if (error == null) continue;
            buf.append(error);
            buf.append("\n");
        }

        if (buf.length() > 0) {
            showError("Invocation Cancelled due to following errors: \n" + buf.toString());
            return;
        }

        // Use the model as the call back to emit the signal
        TriumphModel model = TriumphModel.getInstance();
        List<Argument<?>> args = mSignal.getOutputArguments();
        model.onEmitSignal(mSignal, args, sessionlessBox.selectedProperty().get());
    }
}
