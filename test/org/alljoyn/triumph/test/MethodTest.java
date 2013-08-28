/******************************************************************************
 * Copyright 2013, Qualcomm Innovation Center, Inc.
 *
 *    All rights reserved.
 *    This file is licensed under the 3-clause BSD license in the NOTICE.txt
 *    file for this project. A copy of the 3-clause BSD license is found at:
 *
 *        http://opensource.org/licenses/BSD-3-Clause.
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the license is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the license for the specific language governing permissions and
 *    limitations under the license.
 ******************************************************************************/


package org.alljoyn.triumph.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.ifaces.DBusProxyObj;
import org.alljoyn.triumph.TriumphCPPAdapter;
import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.model.components.Interface;
import org.alljoyn.triumph.model.components.AJObject;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.Member;
import org.alljoyn.triumph.model.components.TriumphAJParser;
import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.model.components.arguments.Argument.DIRECTION;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.model.components.arguments.DictionaryArgument;
import org.alljoyn.triumph.model.components.arguments.ObjectArrayArgument;
import org.alljoyn.triumph.model.components.arguments.StringArgument;
import org.alljoyn.triumph.model.components.arguments.StructArgument;
import org.alljoyn.triumph.model.session.Session;
import org.alljoyn.triumph.model.session.SessionManager;
import org.alljoyn.triumph.test.ifaces.ComplexStruct;
import org.alljoyn.triumph.test.ifaces.MemberService;
import org.alljoyn.triumph.test.ifaces.MethodInterface;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test that creates a custom AllJoyn obect and uses triumph data types 
 * and generic call method procedures.
 * 
 * @author mhotan
 */
public class MethodTest {

    private final static String ServiceWellKnownName = "org.alljoyn.triumph.test.callMethodTest";
    private final static String ObjectPath = "/test/service";
    private final short PORT = 0;

    static {
        System.loadLibrary("alljoyn_java");
        System.loadLibrary("triumph");
    }

    private static BusAttachment mBus;
    private static SessionManager mSessionManager;
    private static Session mSession;
    private Object[] mInArgs;

    private static MemberService mService;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        mBus = new BusAttachment(MethodTest.class.getName());

        Status status = mBus.connect();
        Assert.assertEquals("Connection status is Ok", Status.OK, status);
        Assert.assertTrue("Bus is connected", mBus.isConnected());

        DBusProxyObj control = mBus.getDBusProxyObj();
        DBusProxyObj.RequestNameResult res = control.RequestName(ServiceWellKnownName, 
                DBusProxyObj.REQUEST_NAME_NO_FLAGS);

        mSessionManager = new SessionManager(mBus);

        Assert.assertEquals(DBusProxyObj.RequestNameResult.PrimaryOwner, res);
    }

    @AfterClass
    public static void tearDownAfterClass() throws BusException {
        DBusProxyObj control = mBus.getDBusProxyObj();
        DBusProxyObj.ReleaseNameResult res = control.ReleaseName(ServiceWellKnownName);
        Assert.assertEquals(DBusProxyObj.ReleaseNameResult.Released, res);

        mSessionManager.destroy();

        mBus.disconnect();
        mBus = null;
    }

    @Before
    public void setup() throws TriumphException {
        mBus.connect();

        mService = new MemberService();
        Status status = mBus.registerBusObject(mService, ObjectPath);
        Assert.assertEquals(Status.OK, status);

        mSession = mSessionManager.createNewSession(ServiceWellKnownName, PORT);
        assertNotNull("Session created", mSession);
    }

    @After
    public void tearDown() {
        Status status = mSession.closeConnection();
        Assert.assertEquals(Status.OK, status);

        mBus.unregisterBusObject(mService);

        mInArgs = null;
    }

    @Test
    public void testIntrospection() throws TriumphException {
        String introSpection =  mSessionManager.getInstrospection(ServiceWellKnownName, ObjectPath, PORT);
        assertNotNull("Introspected data is not null", introSpection);
    }

    private AJObject privTestHasObjectByObjectPath() throws TriumphException {
        TriumphAJParser parser = new TriumphAJParser(mSessionManager);
        EndPoint service = new EndPoint(ServiceWellKnownName);

        service = parser.parseIntrospectData(service, PORT);
        List<AJObject> objects = service.getObjects();

        // There is always the the DBus peer object for this well known name
        // Then there is our registered object.  Therefore there is exactly two objects that exist
        assertEquals("Has only one objects", 2, objects.size());

        // Get the object with the name
        AJObject object = service.getObject(ObjectPath);
        assertNotNull("Doesn't have Object Path " + ObjectPath, object);
        return object;
    }

    @Test
    public void testHasObjectByObjectPath() throws TriumphException {
        privTestHasObjectByObjectPath();
    }

    // Should have the interface it actually implements.
    @Test
    public void testHasObjectHasInterface() throws TriumphException {
        AJObject object = privTestHasObjectByObjectPath();
        String name = MethodInterface.class.getName();
        Assert.assertTrue("Has interface with name " + name, object.hasInterface(name));
        Assert.assertNotNull("Has interface with name " + name, object.getInterface(name));
        Interface iface = object.getInterface(name);
        Assert.assertEquals("Name is correct", name, iface.getName());
    }

    // Should not have an interface that the object does not implement
    @Test
    public void testHasObjectHasInterfaceFail() throws TriumphException {
        AJObject object = privTestHasObjectByObjectPath();
        String failName =  "Name.not.exist";
        Assert.assertFalse("should not have interface name " + failName, object.hasInterface(failName));
        Assert.assertNull("should not have interface name " + failName, object.getInterface(failName));
    }

    /**
     * Generic Call method
     * 
     * @param memberName
     * @param inArgs
     * @return
     * @throws TriumphException
     */
    private Object callMethod(String memberName, Object[] inArgs) throws TriumphException {
        AJObject object = privTestHasObjectByObjectPath();
        assertNotNull(object);
        String ifaceName = MethodInterface.class.getName();

        Interface iface = object.getInterface(ifaceName);
        assertNotNull(iface);

        // Get the method
        Member member = iface.getMember(memberName);
        assertNotNull(member);

        // Get the Proxy object
        ProxyBusObject proxy = mSession.getProxy(ObjectPath);
        assertNotNull(proxy);

        String inputSig = member.getInputSignature();

        Object result = TriumphCPPAdapter.callMethod(mBus, proxy, ifaceName, 
                memberName, inputSig, inArgs);
        assertTrue("Result should be instance of a string", result instanceof String);
        assertNotNull("Result for method '" + member.getName() + "' must not be null", result);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void InputByteMethodTest() throws TriumphException {
        mInArgs = new Object[] { new Byte((byte)1) };
        callMethod("inputByte", mInArgs);
    }

    @Test
    public void inputBooleanMethodTest() throws TriumphException {
        mInArgs = new Object[] { Boolean.FALSE };
        callMethod("inputBoolean", mInArgs);
    }

    @Test
    public void inputShortMethodTest() throws TriumphException {
        mInArgs = new Object[] { new Short((short)1) };
        callMethod("inputShort", mInArgs);
    }

    @Test
    public void inputIntMethodTest() throws TriumphException {
        mInArgs = new Object[] { new Integer((int)1) };
        callMethod("inputInt", mInArgs);
    }

    @Test
    public void inputLongMethodTest() throws TriumphException {
        mInArgs = new Object[] { new Long((long)1) };
        callMethod("inputLong", mInArgs);
    }

    @Test
    public void inputDoubleMethodTest() throws TriumphException {
        mInArgs = new Object[] { new Double((double)1) };
        callMethod("inputDouble", mInArgs);
    }

    @Test
    public void inputStringMethodTest() throws TriumphException {
        mInArgs = new Object[] { "My String" };
        callMethod("inputString", mInArgs);
    }

    @Test 
    public void inputSignatureMethodTest() throws TriumphException {
        mInArgs = new Object[] { "(sib)" };
        callMethod("inputSignature", mInArgs);
    }

    @Test 
    public void inputObjectPathMethodTest() throws TriumphException {
        mInArgs = new Object[] { "/Path/To/Object" };
        callMethod("inputObjectPath", mInArgs);
    }

    @Test 
    public void inputDictionaryMethodTest() throws TriumphException {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("1", new Integer(1));
        map.put("2", new Integer(2));
        map.put("3", new Integer(3));
        map.put("4", new Integer(4));
        map.put("5", new Integer(5));
        mInArgs = new Object[] { map };
        callMethod("inputDictionary", mInArgs);
    }

    @Test 
    public void inputArrayBytePathMethodTest() throws TriumphException {
        byte[] arg = {1,2,3,4,5};
        mInArgs = new Object[] {arg};
        callMethod("inputArrayByte", mInArgs);
    }

    @Test 
    public void inputArrayBooleanMethodTest() throws TriumphException {
        boolean[] arg = {true, false, true};	
        mInArgs = new Object[] {arg};
        callMethod("inputArrayBoolean", mInArgs);
    }

    @Test 
    public void inputArrayShortMethodTest() throws TriumphException {
        short[] arg = {1,2,3,4,5};
        mInArgs = new Object[] {arg};
        callMethod("inputArrayShort", mInArgs);
    }

    @Test 
    public void inputArrayIntMethodTest() throws TriumphException {
        int[] arg = {1,2,3,4,5};
        mInArgs = new Object[] {arg};
        callMethod("inputArrayInt", mInArgs);
    }

    @Test 
    public void inputArrayLongMethodTest() throws TriumphException {
        long[] arg = {1,2,3,4,5};
        mInArgs = new Object[] {arg};
        callMethod("inputArrayLong", mInArgs);
    }

    @Test 
    public void inputArrayDoubleMethodTest() throws TriumphException {
        double[] arg = {1,2,3,4,5};
        mInArgs = new Object[] {arg};
        callMethod("inputArrayDouble", mInArgs);
    }

    @Test 
    public void inputArrayStringTest() throws TriumphException {
        String[] arg = {"Hello", "World", "How", "Are", "you?"};
        mInArgs = new Object[] {arg};
        callMethod("inputArrayString", mInArgs);
    }

    @Test
    public void inputStructTest() throws TriumphException {
        StructArgument arg = (StructArgument) ArgumentFactory.getArgument("(sid)", "struct", DIRECTION.IN);
        mInArgs = new Object[] {"Struct String", new Integer(4), new Double(5)};
        arg.setValue(mInArgs);
        callMethod("inputStruct", new Object[] {arg.getValue()});
    }

    @Test
    public void inputComplexStruct() throws TriumphException {
        AJObject object = privTestHasObjectByObjectPath();
        assertNotNull(object);
        String ifaceName = MethodInterface.class.getName();

        Interface iface = object.getInterface(ifaceName);
        assertNotNull(iface);

        // Get the method
        Member member = iface.getMember("inputComplexStruct");
        assertNotNull(member);

        // Get the Proxy object
        ProxyBusObject proxy = mSession.getProxy(ObjectPath);
        assertNotNull(proxy);

        // Construct the struct argumnet
        StructArgument structArg = (StructArgument) ArgumentFactory.getArgument
                (member.getInputSignature(), member.getName(), DIRECTION.IN);

        // Prepopulate our struct
        ComplexStruct s = new ComplexStruct();
        s.mName = this.getClass().getSimpleName() + " Complext Struct test";

        Argument<?>[] innerArgs = structArg.getInternalMembers();
        StringArgument stringArg = (StringArgument)innerArgs[0];
        ObjectArrayArgument structArray = (ObjectArrayArgument)innerArgs[1];
        DictionaryArgument dictArgument = (DictionaryArgument)innerArgs[2];

        // Set the simple value
        stringArg.setValue(s.mName);
        Object[] objectArray = new Object[s.mInnerStructs.length];
        for (int i = 0; i < objectArray.length; ++i) {
            objectArray[i] = s.mInnerStructs[i].toObjectArray();
        }
        structArray.setValue(objectArray);
        dictArgument.setValue(s.mMap);

        mInArgs = new Object[3];
        mInArgs[0] = stringArg.getValue();
        mInArgs[1] = structArray.getValue();
        mInArgs[2] = dictArgument.getValue();

        Object result = TriumphCPPAdapter.callMethod(mBus, proxy, ifaceName, 
                member.getName(), member.getInputSignature(), new Object[] { mInArgs });
        assertTrue("Result should be instance of a string", result instanceof String);
        assertNotNull("Result for method '" + member.getName() + "' must not be null", result);
        assertEquals(s.toString(), ((String)result).toString());
    }

    @Test
    public void inputVariantTest() {
        //TODO 
    }

    // Set up 
    private Object callMethod(String memberName) throws TriumphException {
        AJObject object = privTestHasObjectByObjectPath();
        assertNotNull(object);
        String ifaceName = MethodInterface.class.getName();

        Interface iface = object.getInterface(ifaceName);
        assertNotNull(iface);

        // Get the method
        Member member = iface.getMember(memberName);
        assertNotNull(member);

        // Get the Proxy object
        ProxyBusObject proxy = mSession.getProxy(ObjectPath);
        assertNotNull(proxy);

        Object result = TriumphCPPAdapter.callMethod(mBus, proxy, ifaceName, 
                memberName, member.getInputSignature(), 
                new Object[] {"Call method for output"});
        return result;
    }

    @Test
    public void outputByte() throws TriumphException {
        Object result = callMethod("outputByte");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof Byte);
        assertEquals(0, ((Byte)result).byteValue());
    }

    @Test
    public void outputBoolean() throws TriumphException {
        Object result = callMethod("outputBoolean");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof Boolean);
        assertEquals(false, ((Boolean)result).booleanValue());
    }

    @Test
    public void outputShort() throws TriumphException {
        Object result = callMethod("outputShort");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof Short);
        assertEquals(0, ((Short)result).shortValue());
    }

    @Test
    public void outputInt() throws TriumphException {
        Object result = callMethod("outputInt");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof Integer);
        assertEquals(0, ((Integer)result).intValue());
    }

    @Test
    public void outputLong() throws TriumphException {
        Object result = callMethod("outputLong");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof Long);
        assertEquals(0, ((Long)result).longValue());
    }

    @Test
    public void outputDouble() throws TriumphException {
        Object result = callMethod("outputDouble");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof Double);
        assertTrue(Math.abs(0 - ((Double)result).doubleValue()) < .001);
    }

    @Test
    public void outputString() throws TriumphException {
        Object result = callMethod("outputString");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof String);
        assertEquals("Call method for output", (String)result);
    }

    @Test
    public void outputSignature() throws TriumphException {
        Object result = callMethod("outputSignature");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof String);
        assertEquals("Call method for output", (String)result);
    }

    @Test
    public void outputObjectPath() throws TriumphException {
        Object result = callMethod("outputObjectPath");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof String);
        assertEquals("Call method for output", (String)result);
    }

    @Test
    public void outputDictionary() throws TriumphException {
        Object result = callMethod("outputDictionary");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof Map);
        Map<?,?> map = (Map<?,?>)result;
        Set<?> keys = map.keySet();
        Set<Object> values = new HashSet<Object>( map.values());
        assertEquals(5, map.keySet().size());

        for (int i = 1; i < 6; i++) {
            assertTrue("Keyset contains string '" + i + "'", keys.contains("" + (i)));
            assertTrue("Values contains string '" + i + "'", values.contains(new Integer(i)));
        }
    }

    @Test
    public void outputArrayByte() throws TriumphException {
        Object result = callMethod("outputArrayByte");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof byte[]);
        byte[] array = (byte[]) result;
        assertEquals(5, array.length);

        for (int i = 0; i < 5; i++) {
            assertEquals("Array value at index '" + i + "' is correct", i + 1, array[i]);
        }
    }

    @Test
    public void outputArrayBoolean() throws TriumphException {
        Object result = callMethod("outputArrayBoolean");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof boolean[]);
        boolean[] array = (boolean[]) result;
        assertEquals(5, array.length);

        for (int i = 0; i < 5; i++) {
            assertEquals("Array value at index '" + i + "' is correct", i % 2 == 0, array[i]);
        }
    }

    @Test
    public void outputArrayShort() throws TriumphException {
        Object result = callMethod("outputArrayShort");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof short[]);
        short[] array = (short[]) result;
        assertEquals(5, array.length);

        for (int i = 0; i < 5; i++) {
            assertEquals("Array value at index '" + i + "' is correct", i + 1, array[i]);
        }
    }

    @Test
    public void outputArrayInt() throws TriumphException {
        Object result = callMethod("outputArrayInt");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof int[]);
        int[] array = (int[]) result;
        assertEquals(5, array.length);

        for (int i = 0; i < 5; i++) {
            assertEquals("Array value at index '" + i + "' is correct", i + 1, array[i]);
        }
    }

    @Test
    public void outputArrayLong() throws TriumphException {
        Object result = callMethod("outputArrayLong");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof long[]);
        long[] array = (long[]) result;
        assertEquals(5, array.length);

        for (int i = 0; i < 5; i++) {
            assertEquals("Array value at index '" + i + "' is correct", i + 1, array[i]);
        }
    }

    @Test
    public void outputArrayDouble() throws TriumphException {
        Object result = callMethod("outputArrayDouble");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof double[]);
        double[] array = (double[]) result;
        assertEquals(5, array.length);

        for (int i = 0; i < 5; i++) {
            assertTrue("Array value at index '" + i + "' is correct", Math.abs(i + 1 - array[i]) < .001);
        }
    }

    @Test
    public void outputArrayString() throws TriumphException {
        Object result = callMethod("outputArrayString");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof Object[]);
        Object[] array = (Object[]) result;
        assertEquals(5, array.length);

        for (int i = 0; i < 5; i++) {
            assertEquals("Array value at index '" + i +
                    "' is correct", "" + (i + 1), array[i].toString());
        }
    }

    @Test
    public void outputStruct() throws TriumphException {
        Object result = callMethod("outputStruct");
        assertTrue("Result is not null", result != null);
        assertTrue("Result is of correct Type", result instanceof Object[]);
        Object[] array = (Object[]) result;
        assertEquals(3, array.length);
        assertEquals("test string", (String)array[0]);
        assertEquals(1, ((Integer)array[1]).intValue());
        assertTrue(Math.abs(2 - ((Double)array[2]).doubleValue()) < .001);
    }

}
