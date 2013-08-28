package org.alljoyn.triumph.model;

import java.text.DateFormat;
import java.util.Date;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.alljoyn.triumph.model.TransactionLogger.Transaction.TYPE;
import org.alljoyn.triumph.model.components.AllJoynComponent;
import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.Signal;
import org.alljoyn.triumph.model.components.arguments.Argument;

/**
 * Logs transactions made during application runtime.
 */
public class TransactionLogger {
    
    /**
     * Internal list of all the transaction.
     * <br>
     */
    private final ObservableList<Transaction> mList;
    
    private static TransactionLogger mInstance;
    
    private static final DateFormat DATE_FORMATTER =  DateFormat.getDateInstance(DateFormat.SHORT);
    private static final DateFormat TIME_FORMATTER = DateFormat.getTimeInstance(DateFormat.FULL);
    
    private TransactionLogger() {
        mList = FXCollections.observableArrayList();
    }

    public static TransactionLogger getInstance() {
        if (mInstance == null)
            mInstance = new TransactionLogger();
        return mInstance;
    }
    
    public ObservableList<Transaction> getTransactions() {
        return mList;
    }
    
    /**
     * 
     * 
     * @param method Method that was invoked
     * @param inputArgs 
     * @param outArg
     */
    void logMethodInvocation(Method method, Argument<?>[] inputArgs, Argument<?> outArg) {
        Transaction t = new MethodTransaction(method, inputArgs, outArg);
        mList.add(t);
    }
    
    /**
     * 
     */
    void logSignalEmition(Signal signal, Argument<?>[] args) {
        Transaction t = new SignalTransaction(signal, args);
        mList.add(t);
    }
    
    void logPropertyGet(Property prop, Argument<?> arg) {
        Transaction t = new PropertyTransaction(prop, arg, TYPE.PROPERTY_GET);
        mList.add(t);
    }
    
    void logPropertySet(Property prop, Argument<?> arg) {
        Transaction t = new PropertyTransaction(prop, arg, TYPE.PROPERTY_SET);
        mList.add(t);
    }
    
    /**
     * Class that encapsulates 
     */
    public static abstract class Transaction {
        
        private final TYPE mType;
        
        private final Date mDate;
        
        public final BooleanProperty available;
        
        private String mDescription;
        
        public enum TYPE {
            METHOD_INVOKE, SIGNAL_EMIT, PROPERTY_GET, PROPERTY_SET
        }
        
        protected Transaction(TYPE type) {
            mType = type;
            mDate = new Date();
            available = new SimpleBooleanProperty(true);
        }
        
        protected String getDateTimeStamp() {
            return DATE_FORMATTER.format(mDate) + " " + TIME_FORMATTER.format(mDate);
        }
        
        public TYPE getType() {
            return mType;
        }
        
        public Date getTimeOccurred() {
            return mDate;
        }
        
        public String toString() {
            return getDescription();
        }
        
        public String getDescription() {
            if (mDescription == null) {
                mDescription = getDescriptionPrivate();
            }
            return mDescription;
        }
        
        public abstract String getDescriptionPrivate();
        
    }
    
    public static class MethodTransaction extends Transaction {
        
        public final Method mMethod;
        
        public final Argument<?>[] mInputArgs;
        
        public final Argument<?> mOutputArg;
        
        private MethodTransaction(Method method, Argument<?>[] inputArgs, Argument<?> outArg) {
            super(TYPE.METHOD_INVOKE);
            this.mMethod = method;
            this.mInputArgs = inputArgs;
            this.mOutputArg = outArg;
        }

        @Override
        public String getDescriptionPrivate() {
            return getDateTimeStamp() + " Method: " + mMethod.toString();
        }
        
    }
    
    public static class SignalTransaction extends Transaction {
        
        public final Signal mSignal;
        
        public final Argument<?>[] mArguments;
        
        private SignalTransaction(Signal signal, Argument<?>[] arguments) {
            super(TYPE.SIGNAL_EMIT);
            this.mSignal = signal;
            this.mArguments = arguments;
        }
        
        @Override
        public String getDescriptionPrivate() {
            return getDateTimeStamp() + " Signal: " + mSignal.toString();
        }
    }
    
    public static class PropertyTransaction extends Transaction {
        
        public final Property mProperty;
        
        public final Argument<?> mArgument;
        
        private PropertyTransaction(Property prop, Argument<?> value, TYPE type) {
            super(type);
            mProperty = prop;
            mArgument = value;
        }
        
        @Override
        public String getDescriptionPrivate() {
            return getDateTimeStamp() + " Property: " + mProperty.toString();
        }
        
    }
    
}
