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

package org.alljoyn.triumph.model.components.arguments;

import org.alljoyn.triumph.util.AJConstant;
import org.w3c.dom.Node;

public class LongArgument extends NumberArgument<Long> {

    /**
     * 
     */
    private static final long serialVersionUID = 6250147441382470498L;

    LongArgument(String name, DIRECTION direction, boolean isUnsigned) {
        super(name, isUnsigned ? "" + AJConstant.ALLJOYN_UINT64 : "" 
                + AJConstant.ALLJOYN_INT64, direction, isUnsigned);
    }

    LongArgument(Node node, boolean isUnsigned, DIRECTION defaultDir) {
        super(node, isUnsigned, defaultDir);
    }

    @Override
    public String getSignature() {
        String sig = isUnsigned() ? "Unsigned " : "";
        return sig + "long " + getName();
    }

    @Override
    protected String getAJSignature() {
        if (isUnsigned())
            return "" + AJConstant.ALLJOYN_UINT64;
        else 
            return "" + AJConstant.ALLJOYN_INT64;
    }

}
