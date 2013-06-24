package org.alljoyn.triumph.test.ifaces;

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.annotation.Position;

public class ComplexStruct {
	
	public ComplexStruct() {
		mName = "testy";
		mInnerStructs = new Struct[5];
		for (int i = 0; i < 5; i++) 
			mInnerStructs[i] = new Struct("Struct " + i, i, i);
		mMap = new HashMap<String, Integer>();
		mMap.put("1", new Integer(1));
		mMap.put("2", new Integer(2));
		mMap.put("3", new Integer(3));
		mMap.put("4", new Integer(4));
		mMap.put("5", new Integer(5));
	}
	
	@Position(0)
	public String mName;
	
	@Position(1)
	public Struct[] mInnerStructs;

	@Position(2)
	public Map<String, Integer> mMap;
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(mName);
		for (Struct s : mInnerStructs)
			buf.append(s);
		buf.append("Map size:" + mMap.keySet().size());
		return buf.toString(); 
	}
}
