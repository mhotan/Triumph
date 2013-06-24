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

import java.util.Map;

import org.alljoyn.bus.MarshalBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.model.components.arguments.Argument.DIRECTION;
import org.alljoyn.triumph.util.AJConstant;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Factory that produces the correct type of argument based on the 
 * parsed XML node.
 * 
 * @author mhotan
 */
public class ArgumentFactory {

	/**
	 * Creates an argument with type associated with signature
	 * 
	 * @param type Type of the argument
	 * @param name Name to label the argument
	 * @param direction Default direction (in -> input : out - Output)
	 * @return null on failure, Argument corresponding to type
	 * @throws MarshalBusException The signature of the node is not supported
	 */
	public static Argument<?> getArgument(
			String signature, String name, DIRECTION direction) {

		if (name == null) {
			name = "";
		}

		boolean unsigned = false;
		// The first character of the signature 
		// determines the overall type of the argument
		char first = signature.charAt(0);
		switch (first) {
		case AJConstant.ALLJOYN_BYTE:
			return new ByteArgument(name, direction);
		case AJConstant.ALLJOYN_BOOLEAN:
			return new BooleanArgument(name, direction);
		case AJConstant.ALLJOYN_UINT16:
			unsigned = true;
		case AJConstant.ALLJOYN_INT16:
			return new ShortArgument(name, direction, unsigned);
		case AJConstant.ALLJOYN_UINT32:
			unsigned = true;			
		case AJConstant.ALLJOYN_INT32:
		case AJConstant.UNIX_FD:
			return new IntegerArgument(name, direction, unsigned);
		case AJConstant.ALLJOYN_UINT64:
			unsigned = true;
		case AJConstant.ALLJOYN_INT64:
			return new LongArgument(name, direction, unsigned);
		case AJConstant.ALLJOYN_DOUBLE:
			return new DoubleArgument(name, direction, false);
		case AJConstant.ALLJOYN_STRING:
			return new StringArgument(name, direction);
		case AJConstant.ALLJOYN_SIGNATURE:
			return new SignatureArgument(name, direction);
		case AJConstant.ALLJOYN_OBJECT_PATH:
			return new ObjectPathArgument(name, direction);
		case AJConstant.ALLJOYN_ARRAY:
			// identify the type of the internal arguments
			// This could be a primitive type or a more complicated type
			// IE
			//	a{ ... } Some sort of Dictionary
			// or a simple array of other types
			char elementTypeId = signature.charAt(1);
			if (AJConstant.ALLJOYN_DICT_ENTRY_OPEN == elementTypeId) {
				// Handle the dictionaryl
				return new DictionaryArgument(name, direction, signature);
			}

			// Handle Returning arrays
			boolean isElemUnsigned = false;
			switch (elementTypeId) {
			case AJConstant.ALLJOYN_BYTE:
				return new ByteArrayArgument(name, direction);
			case AJConstant.ALLJOYN_BOOLEAN:
				return new BooleanArrayArgument(name, direction);
			case AJConstant.ALLJOYN_UINT16:
				isElemUnsigned = true;
			case AJConstant.ALLJOYN_INT16:
				return new ShortArrayArgument(name, direction, isElemUnsigned);
			case AJConstant.ALLJOYN_UINT32:
			case AJConstant.UNIX_FD:
				isElemUnsigned = true;
			case AJConstant.ALLJOYN_INT32:
				return new IntegerArrayArgument(name, direction, isElemUnsigned);
			case AJConstant.ALLJOYN_UINT64:
				isElemUnsigned = true;
			case AJConstant.ALLJOYN_INT64:
				return new LongArrayArgument(name, direction, isElemUnsigned);
			case AJConstant.ALLJOYN_DOUBLE:
				return new DoubleArrayArgument(name, direction);
			default:
				return new ObjectArrayArgument(name, direction, signature);
			}
		case AJConstant.ALLJOYN_STRUCT_OPEN:
			// IF we came across a struct then we know that 
			// this argument is only a single struct.
			// This means the beginning and end character of the signature
			// is a ( and ) respectively
			// we need to extract all the components internally 
			return new StructArgument(name, direction, signature);
		case AJConstant.ALLJOYN_VARIANT:
			return new VariantArgument(name, direction); // TODO Implement Variant View
		case AJConstant.ALLJOYN_DICT_ENTRY_OPEN:
			return new DictionaryEntryArgument(name, direction, signature);
		default:
			return null; // Error unsupported Signature
		}
	}

	/**
	 * Returns an argument that is represented by this node.
	 * @param node Node that represents the argument
	 * @return Argument or null on failure to parse the node.
	 * @throws MarshalBusException The signature of the node is not supported
	 */
	public static Argument<?> getArgument(Node node, DIRECTION defaultDir) {
		if (!Argument.LABEL.equals(node.getNodeName())) {
			throw new IllegalArgumentException("Node: " + node + " is not an argument node.");
		}

		String signature = getSignature(node);
		if (signature == null) {
			// TODO Change this to throw an exception
			// This should never happen.
			// We should be able to support all the types
			return null; // Fail to have type.
		}

		// This variable helps track if we came 
		// across an unsigned number value.
		boolean unsigned = false;

		char first = signature.charAt(0);
		switch (first) {
		// Simple 
		case AJConstant.ALLJOYN_BYTE:
			return new ByteArgument(node, defaultDir);
		case AJConstant.ALLJOYN_BOOLEAN:
			return new BooleanArgument(node, defaultDir);
		case AJConstant.ALLJOYN_UINT16:
			unsigned = true;
		case AJConstant.ALLJOYN_INT16:
			return new ShortArgument(node, unsigned, defaultDir);
		case AJConstant.ALLJOYN_UINT32:
		case AJConstant.UNIX_FD:
			unsigned = true;			
		case AJConstant.ALLJOYN_INT32:
			return new IntegerArgument(node, unsigned, defaultDir);
		case AJConstant.ALLJOYN_UINT64:
			unsigned = true;
		case AJConstant.ALLJOYN_INT64:
			return new LongArgument(node, unsigned, defaultDir);
		case AJConstant.ALLJOYN_DOUBLE:
			return new DoubleArgument(node, false, defaultDir);
		case AJConstant.ALLJOYN_STRING:
			return new StringArgument(node, defaultDir);
		case AJConstant.ALLJOYN_SIGNATURE:

		case AJConstant.ALLJOYN_OBJECT_PATH:
			return new ObjectPathArgument(node, defaultDir);
		case AJConstant.ALLJOYN_ARRAY:
			// identify the type of the internal arguments
			// This could be a primitive type or a more complicated type
			// IE
			//	a{ ... } Some sort of Dictionary
			// or a simple array of other types
			char elementTypeId = signature.charAt(1);
			if (AJConstant.ALLJOYN_DICT_ENTRY_OPEN == elementTypeId) {
				// Handle the dictionary
				return new DictionaryArgument(node, defaultDir);
			}

			// Handle Returning arrays
			switch (elementTypeId) {
			case AJConstant.ALLJOYN_BYTE:
				return new ByteArrayArgument(node, defaultDir);
			case AJConstant.ALLJOYN_BOOLEAN:
				return new BooleanArrayArgument(node, defaultDir);
			case AJConstant.ALLJOYN_INT16:
			case AJConstant.ALLJOYN_UINT16:
				return new ShortArrayArgument(node, defaultDir);
			case AJConstant.ALLJOYN_UINT32:
			case AJConstant.UNIX_FD:
			case AJConstant.ALLJOYN_INT32:
				return new IntegerArrayArgument(node, defaultDir);
			case AJConstant.ALLJOYN_UINT64:
			case AJConstant.ALLJOYN_INT64:
				return new LongArrayArgument(node, defaultDir);
			case AJConstant.ALLJOYN_DOUBLE:
				return new DoubleArrayArgument(node, defaultDir);
			default:
				return new ObjectArrayArgument(node, defaultDir);
			}
		case AJConstant.ALLJOYN_STRUCT_OPEN:
			// IF we came across a struct then we know that 
			// this argument is only a single struct.
			// This means the beginning and end character of the signature
			// is a ( and ) respectively
			// we need to extract all the components internally 
			return new StructArgument(node, defaultDir); // TODO
		case AJConstant.ALLJOYN_VARIANT:
			return new VariantArgument(node, defaultDir); // TODO
		case AJConstant.ALLJOYN_DICT_ENTRY_OPEN:
			return new DictionaryEntryArgument(node, defaultDir);
		default:
			return null; // Error unsupported Signature
		}
	}

	/**
	 * Attempts to get the signature of the argument.
	 * @param node Node that represents an argument
	 * @return Signature on success null on fail.
	 */
	static String getSignature(Node node) {
		NamedNodeMap attrList = node.getAttributes();
		int size = attrList.getLength();
		for (int i = 0; i < size; i++) {
			Node attr = attrList.item(i);
			if (Argument.SIGNATURE.equals(attr.getNodeName())) {
				return attr.getNodeValue();
			}
		}
		return null; // Fail to have type.
	}

	////////////////////////////////////////////////////////////////////////////
	//////	Unmarshaling into an argument
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Given an Object and its signature the method composed the object into an argument.
	 * The Argument will have the correct type and the correct value.
	 * 
	 * @param name Name to associate to the argument
	 * @param signature Signature of the object 
	 * @param o The object to turn into an argument
	 * @return Argument of that is correctly addociated to the object
	 * @throws TriumphException If the  signature is not supported
	 */
	public static Argument<?> getArgument(String name, String signature, Object o) throws TriumphException {
		
		// Automatically assign the 
		DIRECTION direction = DIRECTION.OUT;
		
		// extract an empty argument of the correct type.
		Argument<?> arg = getArgument(signature, name, direction);
		char first = signature.charAt(0);
		
		// make sure we have a name for this argument
		name = name == null ? "" : name;
		
		// Handle the cas e
		switch (first) {
		case AJConstant.ALLJOYN_BYTE:
			((ByteArgument)arg).setValue((Byte) o);
			break;
		case AJConstant.ALLJOYN_BOOLEAN:
			((BooleanArgument)arg).setValue((Boolean) o);
			break;
		case AJConstant.ALLJOYN_INT16:
		case AJConstant.ALLJOYN_UINT16:
			((ShortArgument)arg).setValue((Short) o);
			break;
		case AJConstant.ALLJOYN_INT32:
		case AJConstant.ALLJOYN_UINT32:
			((IntegerArgument)arg).setValue((Integer) o);
			break;
		case AJConstant.ALLJOYN_INT64:
		case AJConstant.ALLJOYN_UINT64:
			((LongArgument)arg).setValue((Long) o);
			break;
		case AJConstant.ALLJOYN_DOUBLE:
			((DoubleArgument)arg).setValue((Double) o);
			break;
		case AJConstant.ALLJOYN_STRING:
		case AJConstant.ALLJOYN_SIGNATURE:
		case AJConstant.ALLJOYN_OBJECT_PATH:
			((StringArgument)arg).setValue((String) o);
			break;
		case AJConstant.ALLJOYN_ARRAY:
			
			// Find the inner element id.
			char elementTypeId = signature.charAt(1);
			if (AJConstant.ALLJOYN_DICT_ENTRY_OPEN == elementTypeId) {
				((DictionaryArgument)arg).setValue((Map<?,?>) o);
				break;
			}
			switch (elementTypeId) {
				case AJConstant.ALLJOYN_BYTE:
					((ByteArrayArgument)arg).setValue((byte[]) o);
					break;
				case AJConstant.ALLJOYN_BOOLEAN:
					((BooleanArrayArgument)arg).setValue((boolean[]) o);
					break;
				case AJConstant.ALLJOYN_INT16:
				case AJConstant.ALLJOYN_UINT16:
					((ShortArrayArgument)arg).setValue((short[]) o);
					break;
				case AJConstant.ALLJOYN_INT32:
				case AJConstant.ALLJOYN_UINT32:
					((IntegerArrayArgument)arg).setValue((int[]) o);
					break;
				case AJConstant.ALLJOYN_INT64:
				case AJConstant.ALLJOYN_UINT64:
					((LongArrayArgument)arg).setValue((long[]) o);
					break;
				case AJConstant.ALLJOYN_DOUBLE:
					((DoubleArrayArgument)arg).setValue((double[]) o);
					break;
				default:
					((ObjectArrayArgument)arg).setValue((Object[]) o);
			}
			break;
		case AJConstant.ALLJOYN_STRUCT_OPEN:
			((StructArgument)arg).setValue((Object[]) o);
			break;
		case AJConstant.ALLJOYN_VARIANT:
			((VariantArgument)arg).setValue((Variant) o);
			break;
		case AJConstant.ALLJOYN_DICT_ENTRY_OPEN:
			((DictionaryEntryArgument)arg).setValue((Map.Entry<?, ?>) o);
			break;
		}

		if (arg == null) // check to make sure that we were able to decipher the signature
			throw new TriumphException("Unsupported signature " + signature);
		return arg;
	}

}
