package org.alljoyn.triumph.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DynamicDispatch.class, ForEachLoopTest.class,
	MethodTest.class, NumberFormatTest.class,
		SignatureSplitTest.class, SimpleArgumentTest.class })
public class AllTests {

}
