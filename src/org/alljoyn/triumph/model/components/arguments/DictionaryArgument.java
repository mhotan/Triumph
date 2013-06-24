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

import org.alljoyn.triumph.util.AJConstant;
import org.alljoyn.triumph.view.argview.ArgumentView;
import org.alljoyn.triumph.view.argview.DictionaryArgumentView;
import org.w3c.dom.Node;

/**
 * Argument that represents a Dictionary.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class DictionaryArgument extends ArrayArgument<Map<?, ?>> {
	
	/*
	 * AllJoyn abstracts a dicitonary as an array of Dicitionary entries.
	 */
	
	private final String mSignature, mAJSignature;
	
	/**
	 * Creates a dicitionary argument from this node.
	 * @param node Node to create argument from.
	 */
	DictionaryArgument(Node node, DIRECTION defaultDirection) {
		super(node, defaultDirection);
		
		String sig = ArgumentFactory.getSignature(node);
		checkSignature(sig);
		
		DictionaryEntryArgument mEntryTypeHolder = new DictionaryEntryArgument("" , getDirection(), getInnerElementType());
		mSignature = getName() + " " + mEntryTypeHolder.getSignature();
		mAJSignature = "" + AJConstant.ALLJOYN_ARRAY + mEntryTypeHolder.getAJSignature();
	}
	
	/**
	 * Creates an empty dicitionary with associated name and signature that 
	 * determines the internal type.
	 * 
	 * @param name The name to associate to this dictionary
	 * @param isInput determines whether this dictionary is an input or output argument
	 * @param signature Signature of this
	 */
	DictionaryArgument(String name, DIRECTION direction, String signature) {
		super(name, direction, signature);
		checkSignature(signature);
		
		DictionaryEntryArgument mEntryTypeHolder = new DictionaryEntryArgument("" , getDirection(), getInnerElementType());
		mSignature = getName() + " " + mEntryTypeHolder.getSignature();
		mAJSignature = "" + AJConstant.ALLJOYN_ARRAY + mEntryTypeHolder.getAJSignature();
	}
	
	/**
	 * Checks the signature of this.
	 * @param sig Signature
	 */
	private static void checkSignature(String sig) {
		if (sig == null || sig.length() < 5 || !sig.startsWith("a{") && sig.endsWith("}"))
			throw new IllegalArgumentException("Illegal signature for Dictionary: " + sig );
		
	}
	
	/**
	 * @return a new Dictionary element
	 */
	public DictionaryEntryArgument getNewEntry() {
		return new DictionaryEntryArgument("" , getDirection(), getInnerElementType());
	}

	@Override
	protected ArgumentView<Map<?, ?>> createJavaFXNode() {
		return new DictionaryArgumentView(this);
	}

	@Override
	public String getSignature() {
		return mSignature;
	}

	@Override
	protected String getAJSignature() {
		return mAJSignature;
	}

}
