package org.alljoyn.triumph.test;

import static org.junit.Assert.assertNotNull;

import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.model.components.arguments.BooleanArgument;
import org.alljoyn.triumph.model.components.arguments.StringArgument;
import org.alljoyn.triumph.model.components.arguments.Argument.DIRECTION;
import org.alljoyn.triumph.view.argview.BooleanArgumentView;
import org.alljoyn.triumph.view.argview.StringArgumentView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author mhotan
 *
 */
public class SimpleArgumentTest {

	StringArgument stringArg;
	BooleanArgument boolArg;
	
	@Before
	public void setUp() throws Exception {
		stringArg = (StringArgument) ArgumentFactory.getArgument("s", "testArgument", DIRECTION.IN);
		assertNotNull(stringArg);
		boolArg = (BooleanArgument) ArgumentFactory.getArgument("b", "test boolean", DIRECTION.IN);
		assertNotNull(boolArg);
	}

	@After
	public void tearDown() throws Exception {
		stringArg = null;
		boolArg = null;
	}

	@Test
	public void StringTest() {
		StringArgumentView view = new StringArgumentView(stringArg);
		assertNotNull(view);
		
	}
	
	@Test
	public void BooleanTest() {
		BooleanArgumentView view = new BooleanArgumentView(boolArg);
		assertNotNull(view);
	}

}
