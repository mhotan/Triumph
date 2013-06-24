package org.alljoyn.triumph.test.ifaces;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusSignalHandler;

public class SignalReceiver implements SignalInterface, BusObject {
	
	public int mInt;
	public String[] myStringArray;
	public Struct mStruct;
	
	@Override
	@BusSignalHandler (iface="org.alljoyn.triumph.test.ifaces.SignalInterface", signal="receiveInt")
	public void receiveInt(int i) throws BusException {
		mInt = i;
	}

	@Override
	@BusSignalHandler (iface="org.alljoyn.triumph.test.ifaces.SignalInterface", signal="receiveStringArray")
	public void receiveStringArray(String[] array) throws BusException {
		myStringArray = array;
	}

	@Override
	@BusSignalHandler (iface="org.alljoyn.triumph.test.ifaces.SignalInterface", signal="receiveStruct")
	public void receiveStruct(Struct struct) throws BusException {
		mStruct = struct;
	}

}
