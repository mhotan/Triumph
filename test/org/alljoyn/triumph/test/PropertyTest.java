package org.alljoyn.triumph.test;

import static org.junit.Assert.*;

import java.util.List;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.ifaces.DBusProxyObj;
import org.alljoyn.triumph.TriumphCPPAdapter;
import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.model.components.AllJoynInterface;
import org.alljoyn.triumph.model.components.AllJoynObject;
import org.alljoyn.triumph.model.components.AllJoynService;
import org.alljoyn.triumph.model.components.TriumphAJParser;
import org.alljoyn.triumph.model.session.Session;
import org.alljoyn.triumph.model.session.SessionManager;
import org.alljoyn.triumph.test.ifaces.MemberService;
import org.alljoyn.triumph.test.ifaces.MethodInterface;
import org.alljoyn.triumph.test.ifaces.PropertyInterface;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PropertyTest {

	private final static String ServiceWellKnownName = "org.alljoyn.triumph.test.PropertyTest";
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
	private static MyPropertyService mService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mBus = new BusAttachment(PropertyTest.class.getName());
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
	public static void tearDownAfterClass() throws Exception {
		DBusProxyObj control = mBus.getDBusProxyObj();
		DBusProxyObj.ReleaseNameResult res = control.ReleaseName(ServiceWellKnownName);
		Assert.assertEquals(DBusProxyObj.ReleaseNameResult.Released, res);
		mSessionManager.destroy(); 
		mBus.disconnect();
		mBus = null;
	}

	@Before
	public void setUp() throws Exception {
		mBus.connect();

		mService = new MyPropertyService();
		Status status = mBus.registerBusObject(mService, ObjectPath);
		Assert.assertEquals(Status.OK, status);

		mSession = mSessionManager.createNewSession(ServiceWellKnownName, PORT);
		assertNotNull("Session created", mSession);
	}

	@After
	public void tearDown() throws Exception {
		Status status = mSession.closeConnection();
		Assert.assertEquals(Status.OK, status);
		mBus.unregisterBusObject(mService);
		mInArgs = null;
	}

	private AllJoynObject privTestHasObjectByObjectPath() throws TriumphException {
		TriumphAJParser parser = new TriumphAJParser(mSessionManager);
		AllJoynService service = new AllJoynService(ServiceWellKnownName);

		service = parser.parseIntrospectData(service, PORT);
		List<AllJoynObject> objects = service.getObjects();

		// There is always the the DBus peer object for this well known name
		// Then there is our registered object.  Therefore there is exactly two objects that exist
		assertEquals("Has only one objects", 2, objects.size());

		// Get the object with the name
		AllJoynObject object = service.getObject(ObjectPath);
		assertNotNull("Doesn't have Object Path " + ObjectPath, object);
		return object;
	}

	// Should have the interface it actually implements.
	@Test
	public void testHasObjectHasInterface() throws TriumphException {
		AllJoynObject object = privTestHasObjectByObjectPath();
		String name = PropertyInterface.class.getName();
		Assert.assertTrue("Has interface with name " + name, object.hasInterface(name));
		Assert.assertNotNull("Has interface with name " + name, object.getInterface(name));
		AllJoynInterface iface = object.getInterface(name);
		Assert.assertEquals("Name is correct", name, iface.getName());
	}



	@Test
	public void testReadWrite() throws BusException {
		AllJoynObject remoteObject = privTestHasObjectByObjectPath();
		AllJoynInterface iface = remoteObject.getInterface(PropertyInterface.class.getName());
		
		// Get the Proxy object
		ProxyBusObject proxy = mSession.getProxy(ObjectPath);
		assertNotNull(proxy);
		
		String checkValue = "This is a test set";
		TriumphCPPAdapter.setProperty(mBus, proxy, iface.getName(), "StringProp", "s", checkValue);
		assertEquals("Service String should be set to the correct value",checkValue, mService.mString);
		
		int iValue = 100000000;
		TriumphCPPAdapter.setProperty(mBus, proxy, iface.getName(), "IntProp", "i", new Integer(iValue));
		assertEquals("Service Int should be set to the correct value", iValue, mService.mInt);
		
		Object o = TriumphCPPAdapter.getProperty(mBus, proxy, iface.getName(), "IntProp");
		assertTrue("Instance of Integer check", o instanceof Integer);
		assertEquals(iValue, ((Integer)o).intValue());
	}

	private static class MyPropertyService implements PropertyInterface, BusObject {

		public String mString = "Hello";
		public int mInt = 1;
		public int mWriteOnlyInt;
		public final int mReadOnlyInt;


		public MyPropertyService() {
			reset();
			mReadOnlyInt = -1;
		}

		public void reset() {
			mWriteOnlyInt = 0;
			mString = "Hello";
			mInt = 1;
		}

		@Override
		@BusProperty
		public String getStringProp() throws BusException {
			return mString;
		}

		@Override
		@BusProperty
		public void setStringProp(String value) throws BusException {
			mString = value;
		}

		@Override
		@BusProperty
		public int getIntProp() throws BusException {
			return mInt;
		}

		@Override
		@BusProperty
		public void setIntProp(int value) throws BusException {
			mInt = value;
		}

		@Override
		@BusProperty
		public int getReadOnlyInt() {
			return mReadOnlyInt;
		}

		@Override
		public void setWriteOnlyInt(int i) {
			mWriteOnlyInt = i;
		}

	}

}
