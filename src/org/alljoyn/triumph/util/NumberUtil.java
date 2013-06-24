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

package org.alljoyn.triumph.util;

import java.math.BigInteger;
import java.util.List;

import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.model.components.arguments.BooleanArgument;
import org.alljoyn.triumph.model.components.arguments.ByteArgument;
import org.alljoyn.triumph.model.components.arguments.DoubleArgument;
import org.alljoyn.triumph.model.components.arguments.IntegerArgument;
import org.alljoyn.triumph.model.components.arguments.LongArgument;
import org.alljoyn.triumph.model.components.arguments.ShortArgument;

/**
 * Class that handles java inability to store and present unsigned primite numeral types.
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class NumberUtil {

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

	/**
	 * Returns the short value that corresponds to the raw text input.
	 * If the text is not a valid short then null is returned.
	 * 
	 * @param numberStr String representation of number
	 * @param isUnsigned This short value is unsigned.
	 * @return Short value of number string. Returns null on invalid input
	 */
	public static Short parseShort(String numberStr, boolean isUnsigned) {
		try {
			// Handle the case if where 
			if (isUnsigned) {
				BigInteger bi = new BigInteger(numberStr);
				if (bi.compareTo(BigInteger.ZERO) < 0) // if number is negative
					return null; // can't have negative number 

				// Now we know the number is positive or 0 and unsigned
				if (bi.compareTo(UNSIGNED_SHORT_MAX_VALUE) > 0)
					return null; // cant be greater then max value

				return bi.shortValue();
			}

			// The number is a regular number
			return Short.valueOf(numberStr);
		} catch (NumberFormatException e) {
			// not a short number
			return null;
		}
	}

	/**
	 * Given a positive or negative number returns the BigInteger representation of how
	 * it would look as if it was unsigned.  Therefore nonnegative numbers are 
	 * limited to a certain range of representation
	 * 
	 * @param number Unsigned Number to present as string
	 * @return String representation of number
	 */
	public static String getUnsignedRepresentation(Short number) {
		if (number >= 0)
			return BigInteger.valueOf(number).toString();

		BigInteger bi = BigInteger.valueOf(number);
		return bi.add(UNSIGNED_SHORT_MAX_VALUE.add(BigInteger.ONE)).toString();
	}

	/**
	 * Returns the Integer value that corresponds to the raw text input.
	 * If the text is not a valid short then null is returned.
	 * 
	 * @param numberStr String representation of number
	 * @param isUnsigned This short value is unsigned.
	 * @return Integer value of number string. Returns null on invalid input
	 */
	public static Integer parseInteger(String numberStr, boolean isUnsigned) {
		try {
			// Handle the case if where 
			if (isUnsigned) {
				BigInteger bi = new BigInteger(numberStr);
				if (bi.compareTo(BigInteger.ZERO) < 0) // if number is negative
					return null; // can't have negative number 

				// Now we know the number is positive or 0 and unsigned
				if (bi.compareTo(UNSIGNED_INT_MAX_VALUE) > 0)
					return null; // cant be greater then max value

				return bi.intValue();
			}

			// The number is a regular number
			return Integer.valueOf(numberStr);
		} catch (NumberFormatException e) {
			// not a short number
			return null;
		}
	}

	/**
	 * Given a positive or negative number returns the BigInteger representation of how
	 * it would look as if it was unsigned.  Therefore nonnegative numbers are 
	 * limited to a certain range of representation
	 * 
	 * @param number Unsigned Number to present as string
	 * @return String representation of number
	 */
	public static String getUnsignedRepresentation(Integer number) {
		if (number >= 0)
			return BigInteger.valueOf(number).toString();

		BigInteger bi = BigInteger.valueOf(number);
		return bi.add(UNSIGNED_INT_MAX_VALUE.add(BigInteger.ONE)).toString();
	}

	/**
	 * Returns the Long value that corresponds to the raw text input.
	 * If the text is not a valid short then null is returned.
	 * 
	 * @param numberStr String representation of number
	 * @param isUnsigned This short value is unsigned.
	 * @return Long value of number string. Returns null on invalid input
	 */
	public static Long parseLong(String numberStr, boolean isUnsigned) {
		try {
			// Handle the case if where 
			if (isUnsigned) {
				BigInteger bi = new BigInteger(numberStr);
				if (bi.compareTo(BigInteger.ZERO) < 0) // if number is negative
					return null; // can't have negative number 

				// Now we know the number is positive or 0 and unsigned
				if (bi.compareTo(UNSIGNED_LONG_MAX_VALUE) > 0)
					return null; // cant be greater then max value

				return bi.longValue();
			}

			// The number is a regular number
			return Long.valueOf(numberStr);
		} catch (NumberFormatException e) {
			// not a short number
			return null;
		}
	}

	/**
	 * Given a positive or negative number returns the BigInteger representation of how
	 * it would look as if it was unsigned.  Therefore nonnegative numbers are 
	 * limited to a certain range of representation
	 * 
	 * @param number Unsigned Number to present as string
	 * @return String representation of number
	 */
	public static String getUnsignedRepresentation(Long number) {
		if (number >= 0)
			return BigInteger.valueOf(number).toString();

		BigInteger bi = BigInteger.valueOf(number);
		return bi.add(UNSIGNED_LONG_MAX_VALUE.add(BigInteger.ONE)).toString();
	}

	/**
	 * 
	 * 
	 * @param signature
	 * @param arguments
	 * @return null on failure, 
	 */
	public static Object argumentListToPrimitive(String signature, List<Argument<?>> arguments) {
		
		// Handle an argument Signature.
		if (signature.length() > 1) {
			Object[] os = new Object[arguments.size()];
			for (int i = 0; i < os.length; ++i) {
				os[i] = arguments.get(i).getValue();
			}
			return os;
		}
		char typeId = signature.charAt(0);
		switch (typeId) {
		case AJConstant.ALLJOYN_BYTE:
		{
			byte[] args = new byte[arguments.size()];
			for (int i = 0; i < args.length; ++i) {
				// TODO Egh hate this type cast Fix this
				args[i] = ((ByteArgument)arguments.get(i)).getValue();
			}
			return args;
		}
		case AJConstant.ALLJOYN_BOOLEAN:
		{
			boolean[] args = new boolean[arguments.size()];
			for (int i = 0; i < args.length; ++i) {
				// TODO Egh hate this type cast Fix this
				args[i] = ((BooleanArgument)arguments.get(i)).getValue();
			}
			return args;
		}
		case AJConstant.ALLJOYN_INT16:
		case AJConstant.ALLJOYN_UINT16:
		{
			short[] args = new short[arguments.size()];
			for (int i = 0; i < args.length; ++i) {
				// TODO Egh hate this type cast Fix this
				args[i] = ((ShortArgument)arguments.get(i)).getValue();
			}
			return args;
		}
		case AJConstant.ALLJOYN_INT32:
		case AJConstant.ALLJOYN_UINT32:
		{
			int[] args = new int[arguments.size()];
			for (int i = 0; i < args.length; ++i) {
				// TODO Egh hate this type cast Fix this
				args[i] = ((IntegerArgument)arguments.get(i)).getValue();
			}
			return args;
		}
		case AJConstant.ALLJOYN_INT64:
		case AJConstant.ALLJOYN_UINT64:
		{
			long[] args = new long[arguments.size()];
			for (int i = 0; i < args.length; ++i) {
				// TODO Egh hate this type cast Fix this
				args[i] = ((LongArgument)arguments.get(i)).getValue();
			}
			return args;
		}
		case AJConstant.ALLJOYN_DOUBLE:
		{
			double[] args = new double[arguments.size()];
			for (int i = 0; i < args.length; ++i) {
				// TODO Egh hate this type cast Fix this
				args[i] = ((DoubleArgument)arguments.get(i)).getValue();
			}
			return args;
		}
		default:
			Object[] os = new Object[arguments.size()];
			for (int i = 0; i < os.length; ++i) {
				os[i] = arguments.get(i).getValue();
			}
			return os;
		}
	}
}
