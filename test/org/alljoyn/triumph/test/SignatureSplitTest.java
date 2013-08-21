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

import static org.junit.Assert.assertEquals;

import org.alljoyn.triumph.TriumphCPPAdapter;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Was unable to fully understand the nature of splitting up a signature
 * implementation in alljoyn sousrce code.  So i will just try myself.
 * @author mhotan
 */
public class SignatureSplitTest {

	@BeforeClass 
	public static void onlyOnce() {
		System.loadLibrary("alljoyn_java");
		System.loadLibrary("triumph");
	}

	@Test
	public void testSplitSignatureOneElement() {
		String[] sigs = TriumphCPPAdapter.splitSignature("i");
		assertEquals("Parsing Single Int Length", 1, sigs.length);
		assertEquals("Parsing Single Int Values", "i", sigs[0]);
	}
	
	@Test
	public void testSplitSignature3SimpleElements() {
		String[] sigs = TriumphCPPAdapter.splitSignature("igo");
		assertEquals("Parsing Single Int Length", 3, sigs.length);
		assertEquals("Parsing Single Int Values", "i", sigs[0]);
		assertEquals("Parsing Single Int Values", "g", sigs[1]);
		assertEquals("Parsing Single Int Values", "o", sigs[2]);
	}
	
	@Test 
	public void testSplit4ElementsMoreComplex() {
		String[] sigs = TriumphCPPAdapter.splitSignature("ig(ind)ao");
		assertEquals("Parsing Single Int Length", 4, sigs.length);
		assertEquals("Parsing Single Int Values", "i", sigs[0]);
		assertEquals("Parsing Single Int Values", "g", sigs[1]);
		assertEquals("Parsing Single Int Values", "(ind)", sigs[2]);
		assertEquals("Parsing Single Int Values", "ao", sigs[3]);
	}



}
