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

/**
 * Class that just contains constant values;
 * @author mhotan
 */
public final class AJConstant {

	private AJConstant() {}
	
	public static final char ALLJOYN_INVALID          =  0;
	public static final char ALLJOYN_ARRAY            = 'a';
	public static final char ALLJOYN_BOOLEAN          = 'b';
	public static final char ALLJOYN_DOUBLE           = 'd';
	public static final char ALLJOYN_DICT_ENTRY       = 'e';
	public static final char ALLJOYN_SIGNATURE        = 'g';
	public static final char ALLJOYN_INT32            = 'i';
	public static final char ALLJOYN_INT16            = 'n';
	public static final char ALLJOYN_OBJECT_PATH      = 'o';
	public static final char ALLJOYN_UINT16           = 'q';
	public static final char ALLJOYN_STRUCT           = 'r';
	public static final char ALLJOYN_STRING           = 's';
	public static final char ALLJOYN_UINT64           = 't';
	public static final char ALLJOYN_UINT32           = 'u';
	public static final char ALLJOYN_VARIANT          = 'v';
	public static final char ALLJOYN_INT64            = 'x';
	public static final char ALLJOYN_BYTE             = 'y';
	public static final char UNIX_FD                  = 'h';

	public static final char ALLJOYN_STRUCT_OPEN      = '(';
	public static final char ALLJOYN_STRUCT_CLOSE     = ')';
	public static final char ALLJOYN_DICT_ENTRY_OPEN  = '{';
	public static final char ALLJOYN_DICT_ENTRY_CLOSE = '}';
}
