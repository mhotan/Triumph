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

import java.util.List;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.SignalEmitter.GlobalBroadcast;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.ifaces.DBusProxyObj;
import org.alljoyn.triumph.TriumphCPPAdapter;
import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.model.components.Interface;
import org.alljoyn.triumph.model.components.AJObject;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.Member;
import org.alljoyn.triumph.model.components.TriumphAJParser;
import org.alljoyn.triumph.model.components.EndPoint.SERVICE_TYPE;
import org.alljoyn.triumph.model.session.Session;
import org.alljoyn.triumph.model.session.SessionManager;
import org.alljoyn.triumph.test.ifaces.SignalInterface;
import org.alljoyn.triumph.test.ifaces.SignalReceiver;
import org.alljoyn.triumph.test.ifaces.SignalSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SignalTest {

	/**
	 * The exposed well known name 
	 */
	private static final String ServiceWellKnownName = SignalTest.class.getName();
	private final static String ReceiverPath = "/test/service";
	private final static String SourcePath = "/test/source";
	private final short PORT = 0;

	static {
		System.loadLibrary("alljoyn_java");
		System.loadLibrary("triumph");
	}

	private static BusAttachment mBus;
	private static SessionManager mSessionManager;
	private static Session mSession;

	SignalReceiver mReceiver;
	SignalSource mSource;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mBus = new BusAttachment(SignalTest.class.getName());

		Status status = mBus.connect();
		Assert.assertEquals("Connection status is Ok", Status.OK, status);
		Assert.assertTrue("Bus is connected", mBus.isConnected());

		DBusProxyObj control = mBus.getDBusProxyObj();
		DBusProxyObj.RequestNameResult res = control.RequestName(ServiceWellKnownName, 
				DBusProxyObj.REQUEST_NAME_NO_FLAGS);

		mSessionManager = new SessionManager(mBus,null);

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

		Status status = mBus.registerBusObject(mReceiver, ReceiverPath);
		Assert.assertEquals(Status.OK, status);
		mReceiver = new SignalReceiver();
		status = mBus.registerSignalHandlers(mReceiver);
		Assert.assertEquals(Status.OK, status);
		
		mSource = new SignalSource();
		status = mBus.registerBusObject(mSource, SourcePath);
		Assert.assertEquals(Status.OK, status);

		EndPoint ep = new EndPoint(ServiceWellKnownName, SERVICE_TYPE.REMOTE);
        mSession = mSessionManager.createNewSession(ep, PORT);
		assertNotNull("Session created", mSession);
	}

	@After
	public void tearDown() throws Exception {
		Status status = mSession.closeConnection();
		Assert.assertEquals(Status.OK, status);
		mBus.unregisterBusObject(mReceiver);
		mBus.unregisterBusObject(mSource);
	}

	private AJObject privTestHasObjectByObjectPath() throws TriumphException {
		TriumphAJParser parser = new TriumphAJParser(mSession);
		EndPoint service = new EndPoint(ServiceWellKnownName, SERVICE_TYPE.REMOTE);
		
		assertTrue(parser.parseIntrospectData());
        List<AJObject> objects = mSession.getEndPoint().getObjects();
		
		// There is always the the DBus peer object for this well known name
		// Then there is our registered object.  Therefore there is exactly two objects that exist
		assertEquals("Has only one objects", 3, objects.size());
		
		// Get the object with the name
		AJObject object = service.getObject(ReceiverPath);
		assertNotNull("Doesn't have Object Path " + ReceiverPath, object);
		return object;
	}
	
	/**
	 * Generic Call method
	 * 
	 * @param signalName name of the signal.
	 * @param args Input arguments
	 * @throws TriumphException
	 */
	private void emitSignal(String signalName, Object[] args) throws TriumphException {
		AJObject object = privTestHasObjectByObjectPath();
		assertNotNull(object);
		String ifaceName = SignalInterface.class.getName();
		
		Interface iface = object.getInterface(ifaceName);
		assertNotNull(iface);
		
		// Get the method
		Member member = iface.getMember(signalName);
		assertNotNull(member);
		
		// Get the Proxy object
		ProxyBusObject proxy = mSession.getProxy(ReceiverPath);
		assertNotNull(proxy);
		
		String argumentSignature = member.getOutputSignature();
		
		SignalEmitter emitter = new SignalEmitter(
				mSource, ServiceWellKnownName, mSession.getSessionID(), GlobalBroadcast.Off);
		TriumphCPPAdapter.emitSignal(emitter, ifaceName, signalName, argumentSignature, args);
	}
	
	@Test
	public void testEmitInt() throws TriumphException {
		emitSignal("receiveInt", new Object[] {new Integer(2)});
		assertEquals("Integer reciever updated value to 2", 2, mReceiver.mInt);
	}
	
}
