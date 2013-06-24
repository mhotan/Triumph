package org.alljoyn.triumph.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class DynamicDispatch {

	private static int print(Object o) {
		return 1;
	}
	
	private static int print(Object[] o) {
		return 2;
	}
	
	
	@Test
	public void test() {
		Object o = new Object[1];
		if (o instanceof Object[])
			assertEquals(2, print((Object[])o));
		assertEquals(1, print(o));
	}

}
