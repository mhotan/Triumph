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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.triumph.model.components.Attribute;
import org.alljoyn.triumph.model.components.arguments.Argument.DIRECTION;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.model.components.arguments.BooleanArgument;
import org.alljoyn.triumph.model.components.arguments.BooleanArrayArgument;
import org.alljoyn.triumph.model.components.arguments.DictionaryArgument;
import org.alljoyn.triumph.model.components.arguments.SignatureArgument;
import org.alljoyn.triumph.model.components.arguments.StructArgument;
import org.alljoyn.triumph.model.components.arguments.VariantArgument;
import org.alljoyn.triumph.util.AJConstant;
import org.alljoyn.triumph.util.ArgumentStorage;
import org.alljoyn.triumph.util.loaders.NativeLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test serialization of arguments
 * @author mhotan
 */
public class SerializationArgumentTest {

 // Statically load All Joyn java library
    static {
        NativeLoader loader = new NativeLoader();
        loader.loadLibrary("alljoyn_java");
        loader.loadLibrary("triumph");
    }
    
    static BooleanArgument booleanArg1, booleanArg2;
    static BooleanArrayArgument boolArrayArg1, boolArrayArg2;
    static SignatureArgument sigArg1;
    static StructArgument structArg;
    static DictionaryArgument dictArg;
    static VariantArgument varArg;
    Object[] structFields = new Object[] {Byte.valueOf((byte)2), "Hello", Integer.valueOf(23)};
    static Map<String, Integer> dictValues;
    Path tmpFile;
    static Attribute attr1, attr2;
    String structArgName;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Set up one argument with a value
        booleanArg1 = (BooleanArgument) ArgumentFactory.getArgument("" + AJConstant.ALLJOYN_BOOLEAN, "booleanArg1", DIRECTION.IN);
        booleanArg1.addAttribute(attr1 = new Attribute("test 1", "attribute 1"));
        booleanArg1.addAttribute(attr2 = new Attribute("test 2", "attribute 2"));
        
        booleanArg2 = (BooleanArgument) ArgumentFactory.getArgument("" + AJConstant.ALLJOYN_BOOLEAN, "booleanArg2", DIRECTION.IN);
        booleanArg2.setValue(Boolean.TRUE);
        
        boolArrayArg1 = (BooleanArrayArgument) ArgumentFactory.getArgument(
                "" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_BOOLEAN, "Boolean Array Arg1", DIRECTION.IN);
        boolArrayArg2 = (BooleanArrayArgument) ArgumentFactory.getArgument(
                "" + AJConstant.ALLJOYN_ARRAY + AJConstant.ALLJOYN_BOOLEAN, "Boolean Array Arg2", DIRECTION.IN);
        boolArrayArg2.setValue(new boolean[] {false, true});
        
        dictValues = new HashMap<String, Integer>();
        for (int i = 0; i < 5; ++i) {
            dictValues.put("" + (i+1), i+1);
        }
    }

    @Before
    public void setup() throws IOException {
        tmpFile = Files.createTempFile("Temp", null);
        
        String sig = "(" + AJConstant.ALLJOYN_BYTE + AJConstant.ALLJOYN_STRING 
                + AJConstant.ALLJOYN_UINT32 + ")";
        structArgName = "struct argument";
        sigArg1 = (SignatureArgument) ArgumentFactory.getArgument("" + AJConstant.ALLJOYN_SIGNATURE, "signature argument", DIRECTION.IN);
        structArg = (StructArgument) ArgumentFactory.getArgument(sig, structArgName, DIRECTION.IN);
        dictArg = (DictionaryArgument) ArgumentFactory.getArgument("a{si}", "Dictionary Argument", DIRECTION.IN);
        
        dictValues = new HashMap<String, Integer>();
        for (int i = 0; i < 5; ++i) {
            dictValues.put("" + (i+1), i+1);
        }
        
        varArg = (VariantArgument) ArgumentFactory.getArgument("" + AJConstant.ALLJOYN_VARIANT, "Variant arg", DIRECTION.IN);
        
    }
    
    @After
    public void cleanUp() {
        if (tmpFile != null)
            try {
                Files.delete(tmpFile);
            } catch (IOException e) {
                // do nothing
            }
    }
    
    @Test
    public void testRegularNullwithAttributes() throws IOException, ClassNotFoundException {
        ObjectOutputStream os = new ObjectOutputStream(Files.newOutputStream(
                tmpFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
        os.writeObject(booleanArg1);
        os.close();
        
        ObjectInputStream is = new ObjectInputStream(Files.newInputStream(tmpFile));
        BooleanArgument arg = (BooleanArgument) is.readObject();
        is.close();
        
        assertNull("Argument must have null value", arg.getValue());
        assertEquals("Argument name is correct", "booleanArg1", arg.getName());
        
        List<Attribute> attrs = arg.getAttributes();
        assertTrue("Has correct attribute 1" , attrs.contains(attr1));
        assertTrue("Has correct attribute 2" , attrs.contains(attr2));
    }
    
    @Test
    public void testRegularWithValue() throws IOException, ClassNotFoundException {
        ObjectOutputStream os = new ObjectOutputStream(Files.newOutputStream(
                tmpFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
        os.writeObject(booleanArg2);
        os.close();
        
        ObjectInputStream is = new ObjectInputStream(Files.newInputStream(tmpFile));
        BooleanArgument arg = (BooleanArgument) is.readObject();
        is.close();
        
        assertEquals("Argument must have null value", Boolean.TRUE, arg.getValue());
        assertEquals("Argument name is correct", "booleanArg2", arg.getName());
    }
    
    @Test
    public void testMultipleArguments() throws IOException, ClassNotFoundException {
        ObjectOutputStream os = new ObjectOutputStream(Files.newOutputStream(
                tmpFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
        os.writeObject(booleanArg1);
        os.writeObject(booleanArg2);
        os.close();
        
        ObjectInputStream is = new ObjectInputStream(Files.newInputStream(tmpFile));
        Set<BooleanArgument> args = new HashSet<BooleanArgument>();
        while (true) {
            try {
                BooleanArgument arg = (BooleanArgument) is.readObject();
                args.add(arg);
            } catch (EOFException e) {
                break;
            }
        }
        is.close();
        
        assertEquals("Has both argument", 2, args.size());
        assertTrue("Has correct Argument 1" , args.contains(booleanArg1));
        assertTrue("Has correct Argument 2" , args.contains(booleanArg2));
    }
    
    @Test
    public void testBooleanArrayRegular() throws IOException, ClassNotFoundException {
        ObjectOutputStream os = new ObjectOutputStream(Files.newOutputStream(
                tmpFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
        os.writeObject(boolArrayArg2);
        os.close();
        
        ObjectInputStream is = new ObjectInputStream(Files.newInputStream(tmpFile));
        BooleanArrayArgument arg = (BooleanArrayArgument) is.readObject();
        is.close();
        
        boolean[] value =  arg.getValue();
        assertEquals("Must have 2 arguments",2, value.length);
        assertEquals("Argument must be false", false, value[0]);
        assertEquals("Argument must be true", true, value[1]);
        assertEquals("Argument name is correct", "Boolean Array Arg2", arg.getName());
    }
    
    @Test
    public void tesSignatureWithValue() throws IOException, ClassNotFoundException {
        ObjectOutputStream os = new ObjectOutputStream(Files.newOutputStream(
                tmpFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
        String sig = "(bis)";
        sigArg1.setValue(sig);
        os.writeObject(sigArg1);
        os.close();
        
        ObjectInputStream is = new ObjectInputStream(Files.newInputStream(tmpFile));
        SignatureArgument arg = (SignatureArgument) is.readObject();
        is.close();
        
        assertEquals("Argument must have correct value", sig, arg.getValue());
        assertEquals("Argument name is correct", "signature argument", arg.getName());
    }
    
    @Test
    public void testDictionaryWithValue() throws IOException, ClassNotFoundException {
        ObjectOutputStream os = new ObjectOutputStream(Files.newOutputStream(
                tmpFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
        dictArg.setValue(dictValues);
        os.writeObject(dictArg);
        os.close();
        
        ObjectInputStream is = new ObjectInputStream(Files.newInputStream(tmpFile));
        DictionaryArgument arg = (DictionaryArgument) is.readObject();
        is.close();
        
        assertEquals("Argument name is correct", "Dictionary Argument", arg.getName());
        
        Map<?, ?> afterSerialization = arg.getValue();
        assertEquals("Map has correct size", dictValues.keySet().size(), afterSerialization.keySet().size());
        for (Object o: afterSerialization.keySet()) {
            assertTrue("Static map has key: " + o, dictValues.containsKey(o));
            assertEquals(dictValues.get(o), afterSerialization.get(o));
        }
    }
    
    @Test
    public void testStructWithValue() throws IOException, ClassNotFoundException {
        ObjectOutputStream os = new ObjectOutputStream(Files.newOutputStream(
                tmpFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
        structArg.setValue(structFields);
        os.writeObject(structArg);
        os.close();
        
        ObjectInputStream is = new ObjectInputStream(Files.newInputStream(tmpFile));
        StructArgument arg = (StructArgument) is.readObject();
        is.close();
        
        assertEquals("Argument name is correct", structArgName, arg.getName());
        
        Object[] afterSerialization = arg.getValue();
        assertEquals("Correct number of fields", structFields.length, afterSerialization.length);
        for (int i = 0; i < structFields.length; ++i) {
            assertEquals("Incorrect Field " + i, structFields[i], afterSerialization[i]);
        }
    }
    
    @Test
    public void testVariantWithValue() throws IOException, ClassNotFoundException, BusException {
        ObjectOutputStream os = new ObjectOutputStream(Files.newOutputStream(
                tmpFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
        Integer i = Integer.valueOf(3);
        Variant v = new Variant(i, "i");
        varArg.setValue(v);
        os.writeObject(varArg);
        os.close();
        
        ObjectInputStream is = new ObjectInputStream(Files.newInputStream(tmpFile));
        VariantArgument arg = (VariantArgument) is.readObject();
        is.close();
        
        assertEquals("Argument name is correct", "Variant arg", arg.getName());
        
        Variant afterSerialization = arg.getValue();
        assertEquals("Correct signature", v.getSignature(), afterSerialization.getSignature());
        assertEquals("Correct signature", v.getObject(Object.class), afterSerialization.getObject(Object.class));
    }
    
    @Test
    public void createFileDir() {
        ArgumentStorage.getInstance();
        File f = new File("bin/data/args");
        assertTrue("Directory exists", f.exists());
        assertTrue("Directory is in fact a directory", f.isDirectory());
    }

}
