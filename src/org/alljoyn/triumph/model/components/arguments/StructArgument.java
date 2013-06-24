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

import org.alljoyn.bus.MarshalBusException;
import org.alljoyn.triumph.TriumphCPPAdapter;
import org.alljoyn.triumph.view.argview.ArgumentView;
import org.alljoyn.triumph.view.argview.StructArgumentView;
import org.w3c.dom.Node;

/**
 * General argument that represents a struct 
 * @author mhotan
 */
public class StructArgument extends Argument<Object[]> {

	/**
	 * An array of signatures of the contained members.
	 */
	private final String[] mSigs;

	private final Argument<?>[] mInternalArgs;
	
	private final String mAJSignature;

	/**
	 * Creates a StructArgument based off 
	 * @param name Name of the argument
	 * @param dir is Input argument
	 * @param signature Signature of the argument.
	 * @throws MarshalBusException 
	 */
	StructArgument(String name, DIRECTION dir, String signature) {
		super(name, dir);
		isStructOrThrow(signature);

		mAJSignature = signature;
		// Remove the beginning and ending brackets.
		signature = signature.substring(1, signature.length() - 1);
		mSigs = TriumphCPPAdapter.splitSignature(signature);
		if (mSigs == null) {
			throw new RuntimeException("Unable to extract Struct" +
					" inner types for signature: " + signature);
		}

		// For every signature 
		// Produce a Argument for every signature type internally
		mInternalArgs = new Argument<?>[mSigs.length];
		for (int i = 0; i < mInternalArgs.length; ++i) {
			mInternalArgs[i] = ArgumentFactory.getArgument(mSigs[i], null, getDirection());
		}
		
		
	}

	/**
	 * Creates a Struct arguments from a XML DOm node.
	 * 
	 * @param node Node that represents a Struct
	 * @param defaultDirection default direction of this argument
	 * @throws MarshalBusException 
	 */
	StructArgument(Node node, DIRECTION defaultDirection) {
		super(node, defaultDirection);

		// Get the AJ Struct signature
		String signature = ArgumentFactory.getSignature(node);
		isStructOrThrow(signature);

		if (!(signature.startsWith("(") && signature.endsWith(")"))) {
			throw new IllegalArgumentException(
					"Node is not a struct. Illegal Signature " + signature);
		}
		mAJSignature = signature;
		// Remove the beginning and ending brackets.
		signature = signature.substring(1, signature.length() - 1);
		mSigs = TriumphCPPAdapter.splitSignature(signature);
		if (mSigs == null) {
			throw new RuntimeException("Unable to extract Struct" +
					" inner types for signature: " + signature);
		}

		// For every signature 
		// Produce a Argument for every signature type internally
		mInternalArgs = new Argument<?>[mSigs.length];
		for (int i = 0; i < mInternalArgs.length; ++i) {
			mInternalArgs[i] = ArgumentFactory.getArgument(mSigs[i], null, getDirection());
		}
		
		
	}

	/**
	 * This is a strict enforcement that makes sure the signature is
	 * a struct signature.  That is the signature begins with "(" and ends
	 * with ")"
	 * 
	 * @param signature Signature to check
	 */
	private static void isStructOrThrow(String signature) {
		if (!isStructSignature(signature)) {
			throw new IllegalArgumentException(
					"Node is not a struct. Illegal Signature " + signature);
		}
	}

	/**
	 * A boolean check that the signature belongs to a struct.
	 * 
	 * @param signature Signature to check
	 * @return true is Struct, and false otherwise
	 */
	private static boolean isStructSignature(String signature) {
		return signature.startsWith("(") && signature.endsWith(")");
	}

	/**
	 * Returns a independent copy of the array of signatures
	 * @return an array of the signature types in the array.
	 */
	public String[] getTypes() {
		return mSigs.clone();
	}
	
	/**
	 * Return all the internal members of this field with is
	 * corresponding argument value.
	 * @return Arguments inside this struct.
	 */
	public Argument<?>[] getInternalMembers() {
		return mInternalArgs;
	}

	@Override
	protected ArgumentView<Object[]> createJavaFXNode() {
		return new StructArgumentView(this);
	}

	@Override
	public String getSignature() {
		return "Struct " + getName();
	}

	@Override
	protected String getAJSignature() {
		return mAJSignature;
	}
	
}
