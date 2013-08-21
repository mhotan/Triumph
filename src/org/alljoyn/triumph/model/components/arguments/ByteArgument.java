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
import org.alljoyn.triumph.view.argview.ArgumentView;
import org.alljoyn.triumph.view.argview.ByteArgumentView;

/**
 * Argument that represents a single byte.
 */
public class ByteArgument extends NumberArgument<Byte> {

    /**
     * Added Serialization ID
     */
    private static final long serialVersionUID = 449607774098645923L;

    ByteArgument(String name, DIRECTION direction) {
        super(name, direction, false);
    }

    ByteArgument(org.w3c.dom.Node node, DIRECTION direction) {
        super(node, false, direction);
    }

    @Override
    protected ArgumentView<Byte> createJavaFXNode() {
        return new ByteArgumentView(this);
    }

    @Override
    public String getSignature() {
        return Byte.class.getSimpleName() + " " + getName();
    }

    @Override
    protected String getAJSignature() {
        return "" + AJConstant.ALLJOYN_BYTE;
    }
    
}
