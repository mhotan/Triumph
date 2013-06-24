package org.alljoyn.triumph.test.ifaces;

import java.util.Map;

import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;

@BusInterface
public interface MethodInterface {

	/*
	 * Create a test method for every type of input argument
	 * and output argument.
	 */
	
	// Simple method that input the the marshaling
	
	@BusMethod
	public String inputByte(byte value);
	
	@BusMethod
	public String inputBoolean(boolean value);
	
	@BusMethod
	public String inputShort(short value);
	
	@BusMethod
	public String inputInt(int value);
	
	@BusMethod
	public String inputLong(long value);
	
	@BusMethod
	public String inputDouble(Double value);
	
	@BusMethod
	public String inputString(String value);
	
	@BusMethod(signature="g", replySignature="s")
	public String inputSignature(String value);
	
	@BusMethod(signature="o", replySignature="s")
	public String inputObjectPath(String value);
	
	@BusMethod
	public String inputDictionary(Map<String, Integer> value);
	
	@BusMethod
	public String inputArrayByte(byte[] value);
	
	@BusMethod
	public String inputArrayBoolean(boolean[] value);
	
	@BusMethod
	public String inputArrayShort(short[] value);
	
	@BusMethod
	public String inputArrayInt(int[] value);
	
	@BusMethod
	public String inputArrayLong(long[] value);
	
	@BusMethod
	public String inputArrayDouble(double[] value);
	
	@BusMethod
	public String inputArrayString(String[] value);
	
	@BusMethod
	public String inputStruct(Struct value);
	
	@BusMethod
	public String inputComplexStruct(ComplexStruct value);
	
	@BusMethod
	public String inputVariant(Variant value);
	
	// Simple method that input the the unmarshaling
	
	@BusMethod
	public byte outputByte(String prefix);
	
	@BusMethod
	public boolean outputBoolean(String prefix);
	
	@BusMethod
	public short outputShort(String prefix);
	
	@BusMethod
	public int outputInt(String prefix);
	
	@BusMethod
	public long outputLong(String prefix);
	
	@BusMethod
	public double outputDouble(String prefix);
	
	@BusMethod
	public String outputString(String prefix);
	
	@BusMethod
	public String outputSignature(String prefix);
	
	@BusMethod
	public String outputObjectPath(String prefix);
	
	@BusMethod
	public Map<String, Integer> outputDictionary(String prefix);
	
	@BusMethod
	public byte[] outputArrayByte(String prefix);
	
	@BusMethod
	public boolean[] outputArrayBoolean(String prefix);
	
	@BusMethod
	public short[] outputArrayShort(String prefix);
	
	@BusMethod
	public int[] outputArrayInt(String prefix);
	
	@BusMethod
	public long[] outputArrayLong(String prefix);
	
	@BusMethod
	public double[] outputArrayDouble(String prefix);
	
	@BusMethod
	public String[] outputArrayString(String prefix);
	
	@BusMethod
	public Struct outputStruct(String prefix);
	
	@BusMethod
	public Variant outputVariant(String prefix);
	
}
