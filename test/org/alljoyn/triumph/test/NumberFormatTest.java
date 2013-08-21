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

import java.math.BigInteger;

import org.alljoyn.triumph.util.NumberUtil;
import org.junit.Test;

public class NumberFormatTest {

	private static final int uShortBound = (((int)Short.MAX_VALUE + 1) * 2);
	private static final int uShortMax = uShortBound - 1;
	
	@Test
	public void testShortSignedConversion() {
		assertTrue(true);
	}
	
	@Test (expected = NumberFormatException.class)
	public void numberOutOfBoundsNegative() {
		int minOver = Short.MIN_VALUE - 1;
		String s = "" + minOver;
		Short.valueOf(s);
	}
	
	@Test (expected = NumberFormatException.class)
	public void numberOutOfBoundsPositive() {
		String s = "" + (int)Short.MAX_VALUE + 1;
		Short.valueOf(s);
	}
	
	@Test
	public void negativeNumberTest() {
		assertEquals(Short.valueOf("-1").shortValue(), -1);
	}
	
	/**
	 * 
	 */
	@Test
	public void numberFormatCorrectlyTest() {
		
		// Have the string represent the largest possible short value
		String s = "" + ((((int)Short.MAX_VALUE + 1) * 2) - 1);
		Integer val = Integer.valueOf(s);
		Short negativeOne = val.shortValue();
		
		// This proves the bit representation of the bytes is value to interpret
		assertEquals("Max short value cannot be interpretted as short.", -1, negativeOne.shortValue());
		
		// Convert back to signed
		BigInteger i = new BigInteger(negativeOne.toString());
		// two compliment flip.
		i = i.add(new BigInteger("" + (uShortMax + 1)));
		
		
		assertEquals("Number conversion of signedness with BigInteger", ((((int)Short.MAX_VALUE + 1) * 2) - 1), i.intValue());
	}
	
	/**
	 * Here we simulate storing all the unsigned short values after Short.MAX until its actual MAX value as
	 * negative shorts.  then Convert them back to make sure the values look the same
	 */
	@Test
	public void testAllUnsignedShort() {
		
		for (int i = (int)Short.MAX_VALUE + 1; i < uShortBound; ++i) {
			String expected = "" + i;
			Integer val = Integer.valueOf(expected);
			Short unsignedAsNegative = val.shortValue();
			BigInteger bi = new BigInteger(unsignedAsNegative.toString());
			bi = bi.add(new BigInteger("" + (uShortMax + 1)));
			assertEquals("Number conversion of signedness with BigInteger", expected, bi.toString());
		}
		
	} 
	
	@Test
	public void testBigIntVsInt() {
		String s = "" + (int)Short.MAX_VALUE + 10;
		assertEquals("BigInteger type conversion equals integer", new BigInteger(s).shortValue(), Integer.valueOf(s).shortValue());
	}

	
	@Test
	public void testNumberFormatSimpleShort() {
		
		assertEquals(NumberUtil.parseLong("0", false).shortValue(), (short)0);
		assertEquals(NumberUtil.parseLong("" + Short.MAX_VALUE, false).shortValue(), Short.MAX_VALUE);
		assertEquals(NumberUtil.parseLong("0", true).shortValue(), (short)0);
		assertEquals(NumberUtil.parseLong("" + Short.MAX_VALUE, true).shortValue(), Short.MAX_VALUE);
	}

	
	@Test
	public void testNumberFormatSimpleInteger() {
		
		assertEquals(NumberUtil.parseLong("0", false).intValue(), (int)0);
		assertEquals(NumberUtil.parseLong("" + Integer.MAX_VALUE, false).intValue(), Integer.MAX_VALUE);
		assertEquals(NumberUtil.parseLong("0", true).intValue(), (int)0);
		assertEquals(NumberUtil.parseLong("" + Integer.MAX_VALUE, true).intValue(), Integer.MAX_VALUE);
	}
	
	@Test
	public void testNumberFormatSimpleLong() {
		assertEquals(NumberUtil.parseLong("0", false).longValue(), 0L);
		assertEquals(NumberUtil.parseLong("" + Long.MAX_VALUE, false).longValue(), Long.MAX_VALUE);
		assertEquals(NumberUtil.parseLong("0", true).longValue(), 0L);
		assertEquals(NumberUtil.parseLong("" + Long.MAX_VALUE,true).longValue(), Long.MAX_VALUE);
	}
	
	@Test
	public void testNumberFormatNegativeSignedLong() {
		assertEquals(NumberUtil.parseLong("-1", false).longValue(), -1L);
		assertEquals(NumberUtil.parseLong("" + Long.MIN_VALUE, false).longValue(), Long.MIN_VALUE);
	}
	
	@Test
	public void testNumberFormatNegativeUnSignedLong() {
		assertNull(NumberUtil.parseLong("-1", true));
		assertNull(NumberUtil.parseLong("" + Long.MIN_VALUE, true));
	}
	
	@Test
	public void testNumberFormatNegativeSignedInteger() {
		assertEquals(NumberUtil.parseInteger("-1", false).intValue(), -1);
		assertEquals(NumberUtil.parseInteger("" + Integer.MIN_VALUE, false).intValue(), Integer.MIN_VALUE);
	}
	
	@Test
	public void testNumberFormatNegativeUnSignedInteger() {
		assertNull(NumberUtil.parseInteger("-1", true));
		assertNull(NumberUtil.parseInteger("" + Integer.MIN_VALUE, true));
	}
	
	@Test
	public void testNumberFormatNegativeSignedShort() {
		assertEquals(NumberUtil.parseShort("-1", false).shortValue(), -1);
		assertEquals(NumberUtil.parseShort("" + Short.MIN_VALUE, false).shortValue(), Short.MIN_VALUE);
	}
	
	@Test
	public void testNumberFormatNegativeUnSignedShort() {
		assertNull(NumberUtil.parseShort("-1", true));
		assertNull(NumberUtil.parseShort("" + Short.MIN_VALUE, true));
	}

	
	/**
	 * Maximum value that a unsigned short can have
	 */
	private static final BigInteger UNSIGNED_SHORT_MAX_VALUE = new BigInteger("" + ((((int)Short.MAX_VALUE + 1) * 2) - 1));

	/**
	 * Maximum value that a unsigned integer can have
	 */
	private static final BigInteger UNSIGNED_INT_MAX_VALUE = new BigInteger("" + ((((long)Integer.MAX_VALUE + 1) * 2) - 1));

	/**
	 * Maximum value that a unsigned Long can have
	 */
	private static final BigInteger UNSIGNED_LONG_MAX_VALUE =  
			new BigInteger("" + Long.MAX_VALUE).
			add(BigInteger.ONE).
			multiply(BigInteger.valueOf(2)).
			subtract(BigInteger.ONE);
	
	@Test
	public void testNumberFormatUnsignedLong() {
		Long max = Long.MAX_VALUE;
		Long validValue = NumberUtil.parseLong("" + BigInteger.valueOf(max).add(BigInteger.ONE), true);
		assertNotNull(validValue);
		assertEquals(Long.MIN_VALUE, validValue.longValue());
		assertEquals(UNSIGNED_LONG_MAX_VALUE.longValue(), -1L);
	}
	
	@Test
	public void testNumberFormatUnsignedInteger() {
		Integer max = Integer.MAX_VALUE;
		Integer validValue = NumberUtil.parseInteger("" + BigInteger.valueOf(max).add(BigInteger.ONE), true);
		assertNotNull(validValue);
		assertEquals(Integer.MIN_VALUE, validValue.intValue());
		assertEquals(UNSIGNED_INT_MAX_VALUE.intValue(), -1);
	}
	
	@Test
	public void testNumberFormatUnsignedShort() {
		Short max = Short.MAX_VALUE;
		Short validValue = NumberUtil.parseShort("" + BigInteger.valueOf(max).add(BigInteger.ONE), true);
		assertNotNull(validValue);
		assertEquals(Short.MIN_VALUE, validValue.shortValue());
		assertEquals(UNSIGNED_SHORT_MAX_VALUE.shortValue(), -1);
	}
	
}
