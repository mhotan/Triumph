package org.alljoyn.triumph.test.ifaces;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusSignal;

// Interface to test Triumph Signals
@BusInterface
public interface SignalInterface {

	@BusSignal
	public void receiveInt(int i) throws BusException;
	
	@BusSignal
	public void receiveStringArray(String[] array) throws BusException;
	
	@BusSignal
	public void receiveStruct(Struct struct) throws BusException;
	
}
