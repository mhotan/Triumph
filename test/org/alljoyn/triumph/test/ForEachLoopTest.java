package org.alljoyn.triumph.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class ForEachLoopTest {

	static final int[] array = {1,2,3,4,5,6}; 

	@Test
	public void test() {

		for (int j = 0; j < 100; ++j ){
			int i = 0;
			for (int val: array) {
				assertEquals(array[i],val);
				i++;
			}
		}
	}

}
