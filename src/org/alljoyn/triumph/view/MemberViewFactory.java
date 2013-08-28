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

import org.alljoyn.triumph.model.components.Member;
import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.Signal;

/**
 * Factory class that helps generate views for 
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public final class MemberViewFactory {

    private MemberViewFactory() {
        // cannot instantiate
    }

    /**
     * Produces a View for this method.
     * 
     * @param member member instance to create view for.
     * @return View that corresponds to the member instance
     */
    public static MemberView produceView(Member member) {
        if (member instanceof Signal) {
            return new SignalView((Signal) member);
        } else if (member instanceof Method) {
            return new MethodView((Method) member);
        }
        throw new RuntimeException("Unsupported member type " + member.getClass());
    }
}
