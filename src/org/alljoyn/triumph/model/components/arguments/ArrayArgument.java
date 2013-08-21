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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.w3c.dom.Node;

/**
 * This represents a general array argument.  This allows for us to represent specific 
 * arrays of different Primitive and Object array types.
 * 
 * @author mhotan
 * 
 * @param <ARRAY_TYPE> Type the array to be used when returned. IE byte[], int[], or Object[]
 */
public abstract class ArrayArgument<ARRAY_TYPE> extends Argument<ARRAY_TYPE> {

	/**
     * 
     */
    private static final long serialVersionUID = -3869607870188746440L;
    /**
	 * This is the signature of the elements contained in this 
	 * array.
	 */
	private String mInnerElementSig;
	
	/**
	 * Creates an empty array argument based of the signature 
	 * inputed.  This signature must be that of an array.  That is 
	 * it must start with an 'a' and follow with the array inner type
	 * 
	 * IE.
	 * 		ai
	 * 		ab
	 * 		a(idb)
	 * 
	 * @param name Name of the argument
	 * @param direction Direction of the argument
	 * @param signature Complete signature of the array
	 */
	ArrayArgument(String name, DIRECTION direction, String signature) {
		super(name, direction);
		
		isArray(signature); // Throws exception if not the case
		
		// trim off the a
		mInnerElementSig = signature.substring(1);
	}
	
	/**
	 * Creates an array argument 
	 * @param node
	 */
	ArrayArgument(Node node, DIRECTION direction) {
		super(node, direction);
		
		String sig = ArgumentFactory.getSignature(node);
		isArray(sig);
		mInnerElementSig = sig.substring(1);
	}
	
	/**
	 * Array signature validation check
	 * @param signature Signature to check
	 */
	private static void isArray(String signature) {
		IllegalArgumentException e = new IllegalArgumentException(
				"Illegal signature of array '" + signature + "'");
		
		// the signature cannot be nu
		if (signature == null || signature.isEmpty() || signature.length() == 1)
			throw e;
		
		// if signature does not begin with array or
		// it begins with dictionary prefix.
		if (!signature.startsWith("a"))
			throw e;
	}
	
//	@Override
//	protected char getAJType() {
//		return AJConstant.ALLJOYN_ARRAY;
//	}

	@Override
	public String getSignature() {
		// Create a temporary argument just to extract the signature
		Argument<?> arg = ArgumentFactory.getArgument(mInnerElementSig, "", getDirection());
		return arg.getSignature() + "[]";
	}
	
	/**
	 * Returns the AllJoyn Type signature that represents
	 * 
	 * @return AllJoyn Type signature
	 */
	public String getInnerElementType() {
		return mInnerElementSig;
	}

	/**
     * As implemented regarding by interface Serializable.
     * This method requires this exact signature.
     * <br> This method is called from serialization to write out internal 
     * fields to a file storage.
     * 
     * @param out ObjectOutputStream to write the file out to.
     * @throws IOException Exception occured while writing a file.
     */
    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        writeOut(out);
    }

    /**
     * As implemented regarding by interface Serializable.
     * This method requires this exact signature.
     * <br> This method is called when reading an object from file storage.
     * 
     * @param in ObjectInputStream to read in from
     * @throws IOException Error occured accessing file
     * @throws ClassNotFoundException unable to load default values.
     */
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        readIn(in);
    }

    @Override
    protected void writeOut(ObjectOutputStream out) throws IOException {
        super.writeOut(out);
        out.writeUTF(mInnerElementSig);
    }

    @Override
    protected void readIn(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readIn(in);
        mInnerElementSig = in.readUTF();
    }
	
}
