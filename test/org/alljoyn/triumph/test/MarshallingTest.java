package org.alljoyn.triumph.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.MsgArg;
import org.alljoyn.bus.Variant;
import org.alljoyn.triumph.TriumphCPPAdapter;
import org.alljoyn.triumph.util.AJConstant;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MarshallingTest {

	private long mArg;

	private Byte mByte = 1;
	private Boolean mBoolean = false;
	private Short mShort = 1;
	private Integer mInt = 1;
	private Long mLong = 1L;
	private Double mDouble = 1.0;
	private String mString = "test";
	private String mSignature = "ai";
	private String mObjectPath = "/test";
	private Map<String, String> mDictionary;
	private static final String mDictSig = "a{ss}"; 
	private byte[] mByteArray = {1,2,3};
	private boolean[] mBooleanArray = { false, false, true };
	private short[] mShortArray = {1,2,3};
	private int[] mIntArray = {1,2,3};
	private long[] mLongArray = {1,2,3};
	private double[] mDoubleArray = { 1.0, 2.0, 3.0 };
	private Object[] mObjectArray = { new String("Hello"), new String("How"), new String("are"),  new String("you?")};

	// Use this as a struct argument
	private final Object[] structMembers = {Boolean.TRUE, new String("Name"), new Integer(5)};
	private final String structSignature = "(" + AJConstant.ALLJOYN_BOOLEAN + AJConstant.ALLJOYN_STRING + AJConstant.ALLJOYN_INT32 + ")";

	private Variant mVariant;

	private final Type OBJECT_CLASS = Object.class;

	@BeforeClass 
	public static void onlyOnce() {
		System.loadLibrary("alljoyn_java");
		System.loadLibrary("triumph");
	}

	@Before
	public void setUp() throws Exception {
		mArg = TriumphCPPAdapter.createNewMsgArg();
		mDictionary = new HashMap<String, String>();
		mDictionary.put("1", "one");
		mDictionary.put("2", "two");
		mDictionary.put("3", "three");
		mVariant = new Variant(mDouble);
	}

	@After
	public void tearDown() throws Exception {
		TriumphCPPAdapter.destroyMsgArg(mArg);
		mDictionary = null;
	}

	private Object testMarshal(char sig, Object object) { 
		return testMarshal("" + sig, object);
	}

	private Object testMarshal(String sig, Object object) {
		try {
			MsgArg.marshal(mArg, sig, object);
			return MsgArg.unmarshal(mArg, OBJECT_CLASS);
		} catch (BusException e) {
			fail("BusException caught " + e);
		}
		return null;
	}

	@Test
	public void testSignatureEquality() {
		int y = 'y';
		assertEquals(y, AJConstant.ALLJOYN_BYTE);
	}

	@Test
	public void testByte() {
		Object o = testMarshal(AJConstant.ALLJOYN_BYTE, mByte);
		assertNotNull("Null unmarshalled result", o);
		assertTrue("Unmarshaled result is of type Byte", o instanceof Byte);
		assertEquals(mByte.byteValue(), ((Byte)o).byteValue());
	}

	@Test
	public void testBoolean() {
		Object o = testMarshal(AJConstant.ALLJOYN_BOOLEAN, mBoolean);
		assertNotNull("Null unmarshalled result", o);
		assertTrue("Unmarshaled result is of type Boolean", o instanceof Boolean);
		assertEquals(mBoolean.booleanValue(), ((Boolean)o).booleanValue());
	}

	@Test
	public void testShort() {
		Object o = testMarshal(AJConstant.ALLJOYN_INT16, mShort);
		assertNotNull("Null unmarshalled result", o);
		assertTrue("Unmarshaled result is of type Short", o instanceof Short);
		assertEquals(mShort.shortValue(), ((Short)o).shortValue());
	}

	@Test
	public void testInteger() {
		Object o = testMarshal(AJConstant.ALLJOYN_INT32, mInt);
		assertNotNull("Null unmarshalled result", o);
		assertTrue("Unmarshaled result is of type Integer", o instanceof Integer);
		assertEquals(mInt.intValue(), ((Integer)o).intValue());
	}

	@Test
	public void testLong() {
		Object o = testMarshal(AJConstant.ALLJOYN_INT64, mLong);
		assertNotNull("Null unmarshalled result", o);
		assertTrue("Unmarshaled result is of type Long", o instanceof Long);
		assertEquals(mLong.longValue(), ((Long)o).longValue());
	}

	@Test
	public void testDouble() {
		Object o = testMarshal(AJConstant.ALLJOYN_DOUBLE, mDouble);
		assertNotNull("Null unmarshalled result", o);
		assertTrue("Unmarshaled result is of type Double", o instanceof Double);
		assertTrue(mDouble.doubleValue() - ((Double)o).doubleValue() < .001);
	}

	@Test
	public void testString() {
		Object o = testMarshal(AJConstant.ALLJOYN_STRING, mString);
		assertNotNull("Null unmarshalled result", o);
		assertTrue("Unmarshaled result is of type String", o instanceof String);
		assertEquals(mString, (String)o);
	}

	@Test
	public void testSignature() {
		Object o = testMarshal(AJConstant.ALLJOYN_SIGNATURE, mSignature);
		assertNotNull("Null unmarshalled result", o);
		assertTrue("Unmarshaled result is of type Signature", o instanceof String);
		assertEquals(mSignature, (String)o);
	}

	@Test
	public void testObjectPath() {
		Object o = testMarshal(AJConstant.ALLJOYN_OBJECT_PATH, mObjectPath);
		assertNotNull("Null unmarshalled result", o);
		assertTrue("Unmarshaled result is of type Object Path", o instanceof String);
		assertEquals(mObjectPath, (String)o);
	}

	@Test
	public void testInterface() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		Type type = map.getClass();
		assertTrue(type instanceof Class<?>);
	}

	private void testDictionary(String signature, Map<?, ?> dict) throws BusException {

		MsgArg.marshal(mArg, signature, dict);
		Object o = MsgArg.unmarshal(mArg, OBJECT_CLASS);

		assertNotNull("Null unmarshalled result", o);
		assertTrue("Unmarshaled result Type check", o instanceof Map);

		Map<Object, Object> dictionary = (Map<Object, Object>)o;	
		for (Object key : dictionary.keySet()) {
			assertTrue("Any key that exists in the unmarshalled dictionary exists in the source dictionary", dict.containsKey(key));
		}

		for (Object key : dict.keySet()) {
			assertTrue("Any key that exists in the source dictionary exists in the unmarshalled dictionary", dictionary.containsKey(key));
		}

		// check that values match
		for (Object key : dict.keySet()) {
			assertEquals(dict.get(key), dictionary.get(key));
		}
	}

	@Test
	public void testDictionary() {
		try {
			testDictionary(mDictSig, mDictionary);
		} catch (BusException e) {
			fail("Exception => " + e);
		}
	}

	@Test
	public void testByteArray() {
		Object o = testMarshal(
				"" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_BYTE, mByteArray);
		assertNotNull("Null unmarshalled result", o);
		assertEquals("Unmarshaled result type error", o.getClass(), byte[].class);
		assertArrayEquals(mByteArray, (byte[])o);
	}

	@Test
	public void testBooleanArray() {
		Object o = testMarshal(
				"" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_BOOLEAN, mBooleanArray);
		assertNotNull("Null unmarshalled result", o);
		assertEquals("Unmarshaled result type error", o.getClass(), boolean[].class);
		assertEquals(mBooleanArray.length, ((boolean[])o).length);
		for (int i = 0; i < mBooleanArray.length; ++i) {
			assertEquals(mBooleanArray[i], ((boolean[])o)[i]);
		}
	}

	@Test
	public void testShortArray() {
		Object o = testMarshal(
				"" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_INT16, mShortArray);
		assertNotNull("Null unmarshalled result", o);
		assertEquals("Unmarshaled result type error", o.getClass(), short[].class);
		assertArrayEquals(mShortArray, (short[])o);
	}

	@Test
	public void testIntArray() {
		Object o = testMarshal(
				"" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_INT32, mIntArray);
		assertNotNull("Null unmarshalled result", o);
		assertEquals("Unmarshaled result type error", o.getClass(), int[].class);
		assertArrayEquals(mIntArray, (int[])o);
	}

	@Test
	public void testLongArray() {
		Object o = testMarshal(
				"" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_INT64, mLongArray);
		assertNotNull("Null unmarshalled result", o);
		assertEquals("Unmarshaled result type error", o.getClass(), long[].class);
		assertArrayEquals(mLongArray, (long[])o);
	}

	@Test
	public void testDoubleArray() {
		Object o = testMarshal(
				"" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_DOUBLE, mDoubleArray);
		assertNotNull("Null unmarshalled result", o);
		assertEquals("Unmarshaled result type error", o.getClass(), double[].class);
		assertEquals(mDoubleArray.length, ((double[])o).length);
		for (int i = 0; i < mDoubleArray.length; ++i) {
			assertTrue(mDoubleArray[i] - ((double[])o)[i] < .001);
		}
	}

	@Test
	public void testObjectArray() {
		Object o = testMarshal("as", mObjectArray);
		assertNotNull("Null unmarshalled result", o);
		assertEquals("Unmarshaled result type error", o.getClass(), Object[].class);
		assertEquals(mObjectArray.length, ((Object[])o).length);
		for (int i = 0; i < mObjectArray.length; ++i) {
			assertEquals(mObjectArray[i], ((Object[])o)[i]);
		}
	}

	@Test
	public void testStruct() {
		try {
			MsgArg.marshal(mArg, structSignature.substring(1, structSignature.length() - 1), structMembers);
			Object o = MsgArg.unmarshal(mArg, OBJECT_CLASS);
			assertNotNull("Struct returned null", (Object[])o);
			
		} catch (Exception e){
			fail("Exception caught " + e);
		}
	}

	//	@Test
	//	public void testVariant() {}
}
