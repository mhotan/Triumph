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

package org.alljoyn.triumph;

import org.alljoyn.bus.BusException;

public class TriumphException extends BusException {

	/**
     * Serialization ID
     */
    private static final long serialVersionUID = -3950377483923106492L;

    public TriumphException() {
		super("TriumphException");
	}
	
	public TriumphException(String message) {
		super("TriumphException: " + message);
	}

	public TriumphException(Throwable t) {
		super("TriumphException, Cause: " + t.getCause() + " Message: " + t.getMessage());
	}
}
