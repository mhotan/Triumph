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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.fxml.FXML;

import org.alljoyn.bus.BusException;
import org.alljoyn.triumph.controller.TriumphController;
import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.arguments.Argument;

/**
 * Basic view to represent a Method
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class MethodView extends MemberView {

    private static final Logger LOGGER = Logger.getLogger(MethodView.class.getSimpleName());

    private final Method mMethod;

    /**
     * Creates a method view
     * @param method Method to create view around.
     */
    public MethodView(Method method) {
        super(method);
        mMethod = method;

        // Make sure output arguments are not editable 
        // and input argument are editable
        setIntputArgumentEditability(true);
        setOutputArgumentEditability(false);
    }

    @Override
    @FXML
    protected void invoke() throws BusException {

        StringBuffer buf = new StringBuffer();
        for (LoadableArgumentView view : mInputArgs) {
            // If there is an error on save
            String error = view.getCurrentView().onSetCurrentValue();
            if (error == null) continue;
           
            buf.append(error);
            buf.append("\n");
        }
            
        if (buf.length() > 0) {
            showError("Invocation Cancelled due to following errors: \n" + buf.toString());
            return;
        }
        
        // TODO Collect all the arguments and 
        TriumphController model = TriumphController.getInstance();
        List<Argument<?>> args = mMethod.getInputArguments();
        Argument<?> outArg  = model.onMethodInvoked(mMethod, args);

        List<Argument<?>> outArgList = new ArrayList<Argument<?>>(1);
        outArgList.add(outArg);
        setOutputArguments(outArgList);
        LOGGER.info("Result of method '" + mMethod.getName() + "' is " + outArg.getValue().toString());
    }

}
