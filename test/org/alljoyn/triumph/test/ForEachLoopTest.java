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


package org.alljoyn.triumph.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class ForEachLoopTest {

	static final int[] array = {1,2,3,4,5,6}; 

	@Test
	public void test() {

		for (int j = 0; j < 100; ++j ){
			int i = 0;
			for (int val: array) {
				assertEquals(array[i],val);
				i++;
			}
		}
	}

}
