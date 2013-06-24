package org.alljoyn.triumph.test.ifaces;

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusMethod;

public class MemberService implements MethodInterface, BusObject {

	@Override
	@BusMethod
	public String inputByte(byte value) {
		return "Byte inputted " + value;
	}

	@Override
	@BusMethod
	public String inputBoolean(boolean value) {
		return "Boolean inputted " + value;
	}

	@Override
	@BusMethod
	public String inputShort(short value) {
		return "Short inputted " + value;
	}

	@Override
	@BusMethod
	public String inputInt(int value) {
		return "Int inputted " + value;
	}

	@Override
	@BusMethod
	public String inputLong(long value) {
		return "Long inputted " + value;
	}

	@Override
	@BusMethod
	public String inputDouble(Double value) {
		return "Double inputted " + value;
	}

	@Override
	@BusMethod
	public String inputString(String value) {
		return "String inputted " + value;
	}

	@Override
	@BusMethod
	public String inputSignature(String value) {
		return "Signature inputted " + value;
	}

	@Override
	@BusMethod
	public String inputObjectPath(String value) {
		return "Object Path inputted " + value;
	}

	@Override
	@BusMethod
	public String inputDictionary(Map<String, Integer> value) {
		return "Dictionary inputted " + value;
	}

	@Override
	@BusMethod
	public String inputArrayByte(byte[] value) {
		return "Byte array inputted " + value;
	}

	@Override
	@BusMethod
	public String inputArrayBoolean(boolean[] value) {
		return "boolean array inputted " + value;
	}

	@Override
	@BusMethod
	public String inputArrayShort(short[] value) {
		return "short array inputted " + value;
	}

	@Override
	@BusMethod
	public String inputArrayInt(int[] value) {
		return "int array inputted " + value;
	}

	@Override
	@BusMethod
	public String inputArrayLong(long[] value) {
		return "long array inputted " + value;
	}

	@Override
	@BusMethod
	public String inputArrayDouble(double[] value) {
		return "double array inputted " + value;
	}

	@Override
	@BusMethod
	public String inputArrayString(String[] value) {
		return "String array inputted " + value;
	}

	@Override
	@BusMethod
	public String inputStruct(Struct value) {
		return "Struct inputted " + value;
	}
	
	@Override
	@BusMethod
	public String inputComplexStruct(ComplexStruct value) {
		return value.toString();
	}

	@Override
	@BusMethod
	public String inputVariant(Variant value) {
		return "Variant inputted " + value;
	}

	@Override
	@BusMethod
	public byte outputByte(String prefix) {
		return 0;
	}

	@Override
	@BusMethod
	public boolean outputBoolean(String prefix) {
		return false;
	}

	@Override
	@BusMethod
	public short outputShort(String prefix) {
		return 0;
	}

	@Override
	@BusMethod
	public int outputInt(String prefix) {
		return 0;
	}

	@Override
	@BusMethod
	public long outputLong(String prefix) {
		return 0;
	}

	@Override
	@BusMethod
	public double outputDouble(String prefix) {
		return 0;
	}

	@Override
	@BusMethod
	public String outputString(String prefix) {
		return prefix;
	}

	@Override
	@BusMethod
	public String outputSignature(String prefix) {
		return prefix;
	}

	@Override
	@BusMethod
	public String outputObjectPath(String prefix) {
		return prefix;
	}

	@Override
	@BusMethod
	public Map<String, Integer> outputDictionary(String prefix) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("1", new Integer(1));
		map.put("2", new Integer(2));
		map.put("3", new Integer(3));
		map.put("4", new Integer(4));
		map.put("5", new Integer(5));
		return map;
	}

	@Override
	@BusMethod
	public byte[] outputArrayByte(String prefix) {
		return new byte[] {1,2,3,4,5};
	}

	@Override
	@BusMethod
	public boolean[] outputArrayBoolean(String prefix) {
		return new boolean[] {true,false,true,false,true};
	}

	@Override
	@BusMethod
	public short[] outputArrayShort(String prefix) {
		return new short[] {1,2,3,4,5};
	}

	@Override
	@BusMethod
	public int[] outputArrayInt(String prefix) {
		return new int[] {1,2,3,4,5};
	}

	@Override
	@BusMethod
	public long[] outputArrayLong(String prefix) {
		return new long[] {1,2,3,4,5};
	}

	@Override
	@BusMethod
	public double[] outputArrayDouble(String prefix) {
		return new double[] {1,2,3,4,5};
	}

	@Override
	@BusMethod
	public String[] outputArrayString(String prefix) {
		return new String[] {"1","2","3","4","5"};
	}

	@Override
	@BusMethod
	public Struct outputStruct(String prefix) {
		Struct s = new Struct("test string", 1, 2);
		return s;
	}

	@Override
	@BusMethod
	public Variant outputVariant(String prefix) {
		// TODO 
		return null;
	}

}
