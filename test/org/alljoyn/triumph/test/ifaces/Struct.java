package org.alljoyn.triumph.test.ifaces;

import org.alljoyn.bus.annotation.Position;

public class Struct {

	/**
	 * NOTE! 
	 * Must have a Public default Constructor.
	 */
	public Struct() {}
	
	public Struct(String name, int i, double d) {
		mString = name;
		mInt = i;
		mDouble = d;
	}
	
	@Position(0)
	public String mString;
	
	@Position(1)
	public int mInt;
	
	@Position(2)
	public double mDouble;
	
	@Override
	public String toString() {
		return mString + " " + mInt + " " + mDouble; 
	}

	public Object[] toObjectArray() {
		Object[] self = new Object[3];
		self[0] = mString;
		self[1] = new Integer(mInt);
		self[2] = new Double(mDouble);
		return self;
	}
	
}
