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
import java.util.Map.Entry;

import org.alljoyn.triumph.TriumphCPPAdapter;
import org.alljoyn.triumph.view.argview.ArgumentView;
import org.alljoyn.triumph.view.argview.DictionaryElemArgumentView;
import org.w3c.dom.Node;

/**
 * This argument represents a single Dictionary Entry.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class DictionaryEntryArgument extends Argument<Map.Entry<?, ?>> {

	/**
	 * Internal arguments of this dictionary element.
	 */
	private final Argument<?> mKey, mVal;
	
	private final String mAJSignature;
	
	/**
	 * Creates a blank Dictionary Entry Argument. 
	 * 
	 * @param name Name of this Dictionary element, can be null
	 * @param isInput whether this argument is an input argument
	 * @param signature AllJoyn Signature of the element, IE {is}
	 */
	DictionaryEntryArgument(String name, DIRECTION direction, String signature) {
		super(name, direction);
		
		mAJSignature = signature;
		String[] inner = getInnerSignatures(signature);
		if (inner == null)
			throw new IllegalArgumentException(
					"Illegal Signature for Dictionary Entry " + signature);
		
		// Signature of key and values
		String mKeySignature = inner[0];
		String mValueSignature = inner[1];
		
		mKey = ArgumentFactory.getArgument(mKeySignature, "key", getDirection());
		mVal = ArgumentFactory.getArgument(mValueSignature, "value", getDirection());
		
		if (mKey == null) 
			throw new IllegalArgumentException("Illegal signature " + mKeySignature);
		if (mVal == null) 
			throw new IllegalArgumentException("Illegal signature " + mValueSignature);
	}
	
	DictionaryEntryArgument(Node node, DIRECTION defaultDirection) {
		super(node, defaultDirection);
		
		String signature = ArgumentFactory.getSignature(node);
		mAJSignature = signature;
		String[] inner = getInnerSignatures(signature);
		if (inner == null)
			throw new IllegalArgumentException(
					"Illegal Node Signature for Dictionary Entry " + signature);
		
		String mKeySignature = inner[0];
		String mValueSignature = inner[1];
		
		mKey = ArgumentFactory.getArgument(mKeySignature, "key", getDirection());
		mVal = ArgumentFactory.getArgument(mValueSignature, "value", getDirection());
		
		if (mKey == null) 
			throw new IllegalArgumentException("Illegal signature " + mKeySignature);
		if (mVal == null) 
			throw new IllegalArgumentException("Illegal signature " + mValueSignature);
	}
	
	/**
	 * Attempts to extract the internal elements with the signature which represents
	 * the Dictionary Entry.  
	 * 
	 * @param sig Signature to check
	 * @throws IllegalArgumentException If the signature is not valid.
	 */
	private static String[] getInnerSignatures(String sig) {
		
		// Signatures must have at a minimum 4 characters
		// including the opening and closing bracket.
		if (sig == null || sig.length() < 4)
			return null;
		
		// Must have open and close brackets.
		if (!sig.startsWith("{") && !sig.endsWith("}"))
			return null;
		
		// Split away the brackets
		sig = sig.substring(1, sig.length() - 1);
		
		// Internal members must have exactly two elements.
		String[] split = TriumphCPPAdapter.splitSignature(sig);
		if (split.length != 2) 
			return null;
		
		// All Good!
		return split;
	}

	/**
	 * @return The argument correlated with this key
	 */
	public Argument<?> getKey() {
		return mKey;
	}
	
	/**
	 * @return The argument correlated with this value
	 */
	public Argument<?> getVal() {
		return mVal;
	}

	@Override
	protected ArgumentView<Entry<?, ?>> createJavaFXNode() {
		return new DictionaryElemArgumentView(this);
	}

	@Override
	public String getSignature() {
		return "{ " + mKey.getSignature() + ", " + mVal.getSignature() + " }" ;
	}

	@Override
	protected String getAJSignature() {
		return mAJSignature;
	}

}
