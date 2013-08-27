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

#include "org_alljoyn_triumph_TriumphCPPAdapter.h"

#include <jni.h>
#include <stdio.h>
// Namespaces of alljoyn and standard use applications.
using namespace std;

/** The cached JVM pointer, valid across all contexts. */
static JavaVM* jvm = NULL;

/** org/alljoyn/bus */
static jclass CLS_MsgArg = NULL;
static jclass CLS_Signature = NULL;
static jclass CLS_ProxyBusObject = NULL;
static jclass CLS_SignalEmitter = NULL;

/* Method declarations */
static jmethodID MID_MsgArg_marshal = NULL;
static jmethodID MID_MsgArg_marshal_array = NULL;
static jmethodID MID_MsgArg_unmarshal = NULL;
static jmethodID MID_MsgArg_unmarshal_array = NULL;
static jmethodID MID_Signature_split = NULL;
static jmethodID MID_ProxyBusObject_methodCall = NULL;
static jmethodID MID_ProxyBusObject_setProperty = NULL;
static jmethodID MID_ProxyBusObject_getProperty = NULL;
static jmethodID MID_SignalEmitter_signal = NULL;

/* Field declarations */
static jfieldID FID_SignalEmitter_source = NULL;
static jfieldID FID_SignalEmitter_destination = NULL;
static jfieldID FID_SignalEmitter_sessionId = NULL;
static jfieldID FID_SignalEmitter_timeToLive = NULL;
static jfieldID FID_SignalEmitter_flags = NULL;
static jfieldID FID_SignalEmitter_msgContext = NULL;

static const jint DEFAULTCALLTIMEOUT = 25000;
static const jint FLAGS = 0;
static jclass OBJECT_CLASS = NULL;

#ifdef __cplusplus
extern "C" {
#endif

//////////////////////////////////////////////////////////////////////////
//// Private Helper Method begin
//////////////////////////////////////////////////////////////////////////

/**
 * Helper function to throw an exception
 */
static void Throw(JNIEnv* env, const char* name, const char* msg)
{
	jclass clazz = env->FindClass(name);
	if (clazz) {
		env->ThrowNew(clazz, msg);
	}
}

//////////////////////////////////////////////////////////////////////////
//// Private Helper Method end
//////////////////////////////////////////////////////////////////////////

/*
 * Class:     org_alljoyn_triumph_TriumphCPPAdapter
 * Method:    callMethod
 * Signature: (Lorg/alljoyn/bus/BusAttachment;Lorg/alljoyn/bus/ProxyBusObject;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_alljoyn_triumph_TriumphCPPAdapter_callMethod
(JNIEnv *env, jclass, jobject busAttachment, jobject proxyBusObj, jstring iface,
		jstring methodName, jstring inputStr, jobject type, jobjectArray args) {

	// Get the reply time out and flag variables from the proxy objects
	jint timeout, flags;

	// Get time out instance field
	jfieldID fidNumber = env->GetFieldID(CLS_ProxyBusObject, "replyTimeoutMsecs", "I");
	if (fidNumber == NULL) {
		Throw(env, "java/lang/IllegalStateException", "Unable to find 'replyTimeoutMsecs' field in ProxyBusObject");
	}
	timeout = env->GetIntField(proxyBusObj, fidNumber);
	if (timeout <= 0) {
		timeout = DEFAULTCALLTIMEOUT;
	}

	// Get Flag instance field
	fidNumber = env->GetFieldID(CLS_ProxyBusObject, "flags", "I");
	if (fidNumber == NULL) {
		Throw(env, "java/lang/IllegalStateException", "Unable to find 'flags' field in ProxyBusObject");
	}
	flags = env->GetIntField(proxyBusObj, fidNumber);
	if (flags < 0) {
		flags = FLAGS;
	}

	return env->CallObjectMethod(proxyBusObj, MID_ProxyBusObject_methodCall, busAttachment, iface,
			methodName, inputStr,
			type, args,
			timeout, flags);
}

/*
 * Class:     org_alljoyn_triumph_TriumphCPPAdapter
 * Method:    splitSignature
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_org_alljoyn_triumph_TriumphCPPAdapter_splitSignature
(JNIEnv *env, jclass, jstring signature) {
	return reinterpret_cast<jobjectArray>(env->CallStaticObjectMethod(CLS_Signature, MID_Signature_split, signature));
}

/*
 * Class:     org_alljoyn_triumph_TriumphCPPAdapter
 * Method:    emitSignal
 * Signature: (Lorg/alljoyn/bus/SignalEmitter;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_org_alljoyn_triumph_TriumphCPPAdapter_emitSignal
(JNIEnv *env, jclass, jobject emitter, jstring interfaceName,
		jstring signalName, jstring arg_signature, jobjectArray args) {

	// Extract the fields of the emitter.
	// BusObject - source
	// String - destination
	// int - sessionId
	// int - timeToLive
	// int - flags
	// MessageContext - msgContext

	jobject busObject, messageContext;
	jstring destination;
	jint sessionId, timeToLive, flags;

	busObject = env->GetObjectField(emitter, FID_SignalEmitter_source);
	if (!busObject) {
		Throw(env, "java/lang/IllegalStateException", "Unable to find 'source' field in SignalEmitter");
	}
	messageContext = env->GetObjectField(emitter, FID_SignalEmitter_msgContext);
	if (!messageContext) {
		Throw(env, "java/lang/IllegalStateException", "Unable to find 'msgContext' field in SignalEmitter");
	}
	destination = reinterpret_cast<jstring>(env->GetObjectField(emitter, FID_SignalEmitter_destination));
	if (!destination) {
		Throw(env, "java/lang/IllegalStateException", "Unable to find 'destination' field in SignalEmitter");
	}
	sessionId = env->GetIntField(emitter, FID_SignalEmitter_sessionId);
	timeToLive = env->GetIntField(emitter, FID_SignalEmitter_timeToLive);
	flags = env->GetIntField(emitter, FID_SignalEmitter_flags);

	// Call the method declared in signal Emitter Java bindings
	env->CallVoidMethod(emitter, // The instance object
			MID_SignalEmitter_signal, // The Method id of 'signal'
			busObject, // The Bus object that will be the source
			destination, // The destination well known name of the signal
			sessionId, // The session ID to assign to the signal
			interfaceName, // Interface name of the signal.
			signalName, // The literal signal name, how it is identified within the interface
			arg_signature, // Signature of the arguments
			args, // References to arguments
			timeToLive,
			flags,
			messageContext);
}

/*
 * Class:     org_alljoyn_triumph_TriumphCPPAdapter
 * Method:    setProperty
 * Signature: (Lorg/alljoyn/bus/BusAttachment;Lorg/alljoyn/bus/ProxyBusObject;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_org_alljoyn_triumph_TriumphCPPAdapter_setProperty
(JNIEnv *env, jclass, jobject bus, jobject proxy, jstring iface_name,
		jstring property_name, jstring signature, jobject value)
{
	env->CallVoidMethod(proxy, MID_ProxyBusObject_setProperty, bus, iface_name, property_name, signature, value);
}

/*
 * Class:     org_alljoyn_triumph_TriumphCPPAdapter
 * Method:    getPropertyPriv
 * Signature: (Lorg/alljoyn/bus/BusAttachment;Lorg/alljoyn/bus/ProxyBusObject;Ljava/lang/String;Ljava/lang/String;)Lorg/alljoyn/bus/Variant;
 */
JNIEXPORT jobject JNICALL Java_org_alljoyn_triumph_TriumphCPPAdapter_getPropertyPriv
(JNIEnv *env, jclass, jobject bus, jobject proxy, jstring iface_name, jstring property_name)
{
	return env->CallObjectMethod(proxy, MID_ProxyBusObject_getProperty, bus, iface_name, property_name);
}


// Called when the static library is called.
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm,
		void* reserved)
{
	jvm = vm;
	JNIEnv* env;
	if (jvm->GetEnv((void**)&env, JNI_VERSION_1_2)) {
		return JNI_ERR;
	}
	jclass clazz;
	clazz = env->FindClass("org/alljoyn/bus/MsgArg");
	if (!clazz) {
		return JNI_ERR;
	}
	CLS_MsgArg = (jclass)env->NewGlobalRef(clazz);
	MID_MsgArg_marshal = env->GetStaticMethodID(CLS_MsgArg, "marshal", "(JLjava/lang/String;Ljava/lang/Object;)V");
	if (!MID_MsgArg_marshal) {
		return JNI_ERR;
	}
	MID_MsgArg_marshal_array = env->GetStaticMethodID(CLS_MsgArg, "marshal", "(JLjava/lang/String;[Ljava/lang/Object;)V");
	if (!MID_MsgArg_marshal_array) {
		return JNI_ERR;
	}
	MID_MsgArg_unmarshal = env->GetStaticMethodID(CLS_MsgArg, "unmarshal", "(JLjava/lang/reflect/Type;)Ljava/lang/Object;");
	if (!MID_MsgArg_unmarshal) {
		return JNI_ERR;
	}
	MID_MsgArg_unmarshal_array = env->GetStaticMethodID(CLS_MsgArg, "unmarshal", "(Ljava/lang/reflect/Method;J)[Ljava/lang/Object;");
	if (!MID_MsgArg_unmarshal_array) {
		fprintf(stderr, "Unable to find Method ID for 'MsgArg.unmarshalArray'");
		return JNI_ERR;
	}

	// Attempt to find the signature class
	clazz = env->FindClass("org/alljoyn/bus/Signature");
	if (!clazz) {
		return JNI_ERR;
	}
	CLS_Signature = (jclass)env->NewGlobalRef(clazz);
	// Find the split method for the signature class
	MID_Signature_split = env->GetStaticMethodID(CLS_Signature, "split", "(Ljava/lang/String;)[Ljava/lang/String;");
	if (!MID_Signature_split) {
		fprintf(stderr, "Unable to find Method ID for 'Signature.split'");
		return JNI_ERR;
	}

	// Attempt to find the ProxyBusObject class
	clazz = env->FindClass("org/alljoyn/bus/ProxyBusObject");
	if (!clazz) {
		fprintf(stderr, "Unable to find Class ProxyBusObject");
		return JNI_ERR;
	}
	// Find ProxyBusObject's implemented java methods
	CLS_ProxyBusObject = (jclass)env->NewGlobalRef(clazz);
	MID_ProxyBusObject_methodCall = env->GetMethodID(CLS_ProxyBusObject, "methodCall",
			"(Lorg/alljoyn/bus/BusAttachment;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/reflect/Type;[Ljava/lang/Object;II)Ljava/lang/Object;");
	if (!MID_ProxyBusObject_methodCall) {
		fprintf(stderr, "Unable to find Method ID for 'methodCall'");
		return JNI_ERR;
	}
	MID_ProxyBusObject_setProperty = env->GetMethodID(CLS_ProxyBusObject, "setProperty",
			"(Lorg/alljoyn/bus/BusAttachment;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V");
	if (!MID_ProxyBusObject_setProperty) {
		fprintf(stderr, "Unable to find Method ID for 'setProperty'");
		return JNI_ERR;
	}
	MID_ProxyBusObject_getProperty = env->GetMethodID(CLS_ProxyBusObject, "getProperty",
			"(Lorg/alljoyn/bus/BusAttachment;Ljava/lang/String;Ljava/lang/String;)Lorg/alljoyn/bus/Variant;");
	if (!MID_ProxyBusObject_getProperty) {
		fprintf(stderr, "Unable to find Method ID for 'getProperty'");
		return JNI_ERR;
	}

	// Find the signal emitter
	clazz = env->FindClass("org/alljoyn/bus/SignalEmitter");
	if (!clazz) {
		fprintf(stderr, "Unable to find Class SignalEmitter");
		return JNI_ERR;
	}
	CLS_SignalEmitter = (jclass)env->NewGlobalRef(clazz);

	// Get the signal method
	MID_SignalEmitter_signal = env->GetMethodID(CLS_SignalEmitter, "signal",
			"(Lorg/alljoyn/bus/BusObject;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;IILorg/alljoyn/bus/MessageContext;)V");
	if (!MID_SignalEmitter_signal) {
		fprintf(stderr, "Unable to find Method ID for 'SignalEmitter.signal()'");
		return JNI_ERR;
	}

	// Get the field ids of the SignalEmmiter
	FID_SignalEmitter_source = env->GetFieldID(CLS_SignalEmitter, "source", "Lorg/alljoyn/bus/BusObject;");
	if (!FID_SignalEmitter_source) {
		fprintf(stderr, "Unable to find Field ID for 'SignalEmitter field 'source'");
		return JNI_ERR;
	}
	FID_SignalEmitter_destination = env->GetFieldID(CLS_SignalEmitter, "destination", "Ljava/lang/String;");
	if (!FID_SignalEmitter_destination) {
		fprintf(stderr, "Unable to find Field ID for 'SignalEmitter field 'destination'");
		return JNI_ERR;
	}
	FID_SignalEmitter_sessionId = env->GetFieldID(CLS_SignalEmitter, "sessionId", "I");
	if (!FID_SignalEmitter_sessionId) {
		fprintf(stderr, "Unable to find Field ID for 'SignalEmitter field 'sessionId'");
		return JNI_ERR;
	}
	FID_SignalEmitter_timeToLive = env->GetFieldID(CLS_SignalEmitter, "timeToLive", "I");
	if (!FID_SignalEmitter_timeToLive) {
		fprintf(stderr, "Unable to find Field ID for 'SignalEmitter field 'timeToLive'");
		return JNI_ERR;
	}
	FID_SignalEmitter_flags = env->GetFieldID(CLS_SignalEmitter, "flags", "I");
	if (!FID_SignalEmitter_flags) {
		fprintf(stderr, "Unable to find Field ID for 'SignalEmitter field 'flags'");
		return JNI_ERR;
	}
	FID_SignalEmitter_msgContext = env->GetFieldID(CLS_SignalEmitter, "msgContext", "Lorg/alljoyn/bus/MessageContext;");
	if (!FID_SignalEmitter_msgContext) {
		fprintf(stderr, "Unable to find Field ID for 'SignalEmitter field 'msgContext'");
		return JNI_ERR;
	}

	OBJECT_CLASS = env->FindClass("java/lang/Object");
	if (!OBJECT_CLASS) {
		fprintf(stderr, "Unable to find Object .class file");
		return JNI_ERR;
	}

	//	QCC_UseOSLogging(true);
	return JNI_VERSION_1_2;
}

#ifdef __cplusplus
}
#endif
