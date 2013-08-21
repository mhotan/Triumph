package org.alljoyn.triumph.test.ifaces;

import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;

@BusInterface
public interface PropertyInterface extends PropsInterface {

	@BusProperty
	public int getReadOnlyInt();
	
	public void setWriteOnlyInt(int i);
	
}
