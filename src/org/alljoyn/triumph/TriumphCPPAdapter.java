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

package org.alljoyn.triumph;

import java.lang.reflect.Type;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.Variant;

/**
 * Class that serves as an adpater to access native methods required 
 * by this application
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
public class TriumphCPPAdapter {

	private static TriumphCPPAdapter mInstance;
	
	/**
	 * Invokes Remote method on the ProxyBusObject via the BusAttachment.
	 * The method is identified by being a member of the interface identified 
	 * by interfaceName.  The interface Name has to be a name of a 
	 * 
	 * @param busAttachment Busattachment to handle the method call
	 * @param proxy The proxy object that has the method to invoke
	 * @param interfaceName The interface name of the method
	 * @param methodName The name of the method
	 * @param inputSig The input signature of the method
	 * @param outType The output type of the method call (Usually just general Object.class)
	 * @param args The argument values themselves.
	 * @return a general referenc
	 */
	public native static Object callMethod(BusAttachment busAttachment, ProxyBusObject proxy, String interfaceName,
            String methodName, String inputSig, Type outType, Object[] args);
	
	/**
	 * Given a signature of a contiguous set of AllJoyn types parse and seperate out
	 * all the internal types.  The inputted signature must not include the Struct
	 * open and close brackets.
	 * 
	 * @param signature Signature of a contiguous group 
	 * @return Array of individual signatures.
	 */
	public native static  String[] splitSignature(String signature);
	
	/**
	 * Emits a signal using the emitter.  Signal name must be a member 
	 * of the interface.  The interface must have been defined and 
	 * registered on the bus attachment. The signature of must match the
	 * array of Object arguments.
	 * 
	 * @param emitter The Signal Emmitter that will be used to emit the signal.
	 * @param interfaceName The interface name that contains the signal signature
	 * @param signalName The Signal name of the to invoke. 
	 * @param inputSignature Input signature of the signal
	 * @param args Array of arguments that correspond to the signature.
	 */
	public native static void emitSignal(SignalEmitter emitter, String interfaceName, 
			String signalName, String inputSignature, Object[] args);
	
	/**
	 * zl
	 * 
	 * @param bus Bus Attachment to communicate through
	 * @param proxy ProxyBusObject that contains the property
	 * @param interfaceName Name of the interface that contains the property
	 * @param propertyName The name of the property
	 * @param signature Signature of the property
	 * @param value Value to assign to
	 */
	public native static void setProperty(BusAttachment bus, ProxyBusObject proxy, String interfaceName,
            String propertyName, String signature, Object value);
	
	/**
	 * Attempts to retrieve the property value from the designated Proxy bus object. 
	 * 
	 * @param busAttachment Bus attachment to communicate through
	 * @param proxy ProxyBusObject that contains the property
	 * @param interfaceName Name of the interface that contains the property
	 * @param propertyName The name of the property
	 * @return
	 * @throws BusException
	 */
	public static Object getProperty(BusAttachment busAttachment, ProxyBusObject proxy, String interfaceName,
            String propertyName) throws BusException {
		
		// Unmarshal the variant a general object.  
		Variant v = getPropertyPriv(busAttachment, proxy, interfaceName, propertyName);
		return v.getObject(Object.class);
	}
	
	/**
	 * 
	 * @param busAttachment
	 * @param proxy
	 * @param interfaceName
	 * @param propertyName
	 * @return
	 */
	private native static Variant getPropertyPriv(BusAttachment busAttachment, ProxyBusObject proxy, String interfaceName,
            String propertyName);
	
}
