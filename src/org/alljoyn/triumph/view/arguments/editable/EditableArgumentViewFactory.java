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

package org.alljoyn.triumph.view.arguments.editable;

import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.model.components.arguments.BooleanArgument;
import org.alljoyn.triumph.model.components.arguments.BooleanArrayArgument;
import org.alljoyn.triumph.model.components.arguments.ByteArgument;
import org.alljoyn.triumph.model.components.arguments.ByteArrayArgument;
import org.alljoyn.triumph.model.components.arguments.DictionaryArgument;
import org.alljoyn.triumph.model.components.arguments.DictionaryEntryArgument;
import org.alljoyn.triumph.model.components.arguments.DoubleArgument;
import org.alljoyn.triumph.model.components.arguments.DoubleArrayArgument;
import org.alljoyn.triumph.model.components.arguments.IntegerArgument;
import org.alljoyn.triumph.model.components.arguments.IntegerArrayArgument;
import org.alljoyn.triumph.model.components.arguments.LongArgument;
import org.alljoyn.triumph.model.components.arguments.LongArrayArgument;
import org.alljoyn.triumph.model.components.arguments.ObjectArrayArgument;
import org.alljoyn.triumph.model.components.arguments.ShortArgument;
import org.alljoyn.triumph.model.components.arguments.ShortArrayArgument;
import org.alljoyn.triumph.model.components.arguments.StringArgument;
import org.alljoyn.triumph.model.components.arguments.StructArgument;
import org.alljoyn.triumph.model.components.arguments.VariantArgument;
import org.alljoyn.triumph.util.AJConstant;

/**
 * An Argument view factory that is used to generate views for specific argument
 * types.  These views are editable in that they can 
 * @author mhotan
 */
public class EditableArgumentViewFactory {

    /**
     * Cannot instantiate.
     */
    private EditableArgumentViewFactory() {}
    
    /**
     * Given an Argument produce the view that corresponds to that argument.
     * This makes a view that allows the ability to change the value of the argument.
     * 
     * @param argument Argument to the generate the view from
     * @return 
     * <br>The corresponding Argument View for the argument
     * <br>Null if the argument is null 
     */
    public static ArgumentView<?> produceView(Argument<?> argument) {
        if (argument == null) return null;
        
        String sig = argument.getDBusSignature();
        char first = sig.charAt(0);
        switch (first) {
        case AJConstant.ALLJOYN_BYTE:
            return new ByteArgumentView((ByteArgument) argument);
        case AJConstant.ALLJOYN_BOOLEAN:
            return new BooleanArgumentView((BooleanArgument) argument);
        case AJConstant.ALLJOYN_UINT16:
        case AJConstant.ALLJOYN_INT16:
            return new ShortArgumentView((ShortArgument) argument);
        case AJConstant.ALLJOYN_UINT32:
        case AJConstant.ALLJOYN_INT32:
        case AJConstant.UNIX_FD:
            return new IntegerArgumentView((IntegerArgument) argument);
        case AJConstant.ALLJOYN_UINT64:
        case AJConstant.ALLJOYN_INT64:
            return new LongArgumentView((LongArgument) argument);
        case AJConstant.ALLJOYN_DOUBLE:
            return new DoubleArgumentView((DoubleArgument) argument);
        case AJConstant.ALLJOYN_STRING:
        case AJConstant.ALLJOYN_SIGNATURE:
        case AJConstant.ALLJOYN_OBJECT_PATH:
            return new StringArgumentView((StringArgument) argument);
        case AJConstant.ALLJOYN_ARRAY:
            // identify the type of the internal arguments
            // This could be a primitive type or a more complicated type
            // IE
            //  a{ ... } Some sort of Dictionary
            // or a simple array of other types
            char elementTypeId = sig.charAt(1);
            if (AJConstant.ALLJOYN_DICT_ENTRY_OPEN == elementTypeId) {
                // Handle the dictionaryl
                return new DictionaryArgumentView((DictionaryArgument) argument);
            }
            // Handle Returning arrays
            switch (elementTypeId) {
            case AJConstant.ALLJOYN_BYTE:
                return new ByteArrayArgumentView((ByteArrayArgument) argument);
            case AJConstant.ALLJOYN_BOOLEAN:
                return new BooleanArrayArgumentView((BooleanArrayArgument) argument);
            case AJConstant.ALLJOYN_UINT16:
            case AJConstant.ALLJOYN_INT16:
                return new ShortArrayArgumentView((ShortArrayArgument) argument);
            case AJConstant.ALLJOYN_UINT32:
            case AJConstant.UNIX_FD:
            case AJConstant.ALLJOYN_INT32:
                return new IntegerArrayArgumentView((IntegerArrayArgument) argument);
            case AJConstant.ALLJOYN_UINT64:
            case AJConstant.ALLJOYN_INT64:
                return new LongArrayArgumentView((LongArrayArgument) argument);
            case AJConstant.ALLJOYN_DOUBLE:
                return new DoubleArrayArgumentView((DoubleArrayArgument) argument);
            default:
                return new ObjectArrayArgumentView((ObjectArrayArgument) argument);
            }
        case AJConstant.ALLJOYN_STRUCT_OPEN:
            // IF we came across a struct then we know that 
            // this argument is only a single struct.
            // This means the beginning and end character of the signature
            // is a ( and ) respectively
            // we need to extract all the components internally 
            return new StructArgumentView((StructArgument) argument);
        case AJConstant.ALLJOYN_VARIANT:
            return new VariantArgumentView((VariantArgument) argument); 
        case AJConstant.ALLJOYN_DICT_ENTRY_OPEN:
            return new DictionaryElemArgumentView((DictionaryEntryArgument) argument);
        default:
            throw new RuntimeException(EditableArgumentViewFactory.class.getSimpleName() 
                    + ": Unsupported Argument signature " + sig);}
    }
    
}
