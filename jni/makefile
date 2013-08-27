# TODO export this into a Enviroment Variable
ALLJOYN_DIR=/home/mhotan/alljoyn
#ALLJOYN_DIR=/home/mathewm/work2/alljoyn

# Create the base variables
CLASS_PATH=../bin:$(ALLJOYN_DIR)/alljoyn_java/bin/jar/alljoyn.jar
CC=g++
LIBS=-lrt -lstdc++ -lpthread -lcrypto -lssl -lm
LFLAGS=-shared

# Define a virtual path for .class in the bin directory
vpath %.class $(CLASS_PATH)

all : lib

java : ../bin/org/alljoyn/triumph/TriumphCPPAdapter.class
	@echo Generating header file for JNI
	javah -classpath $(CLASS_PATH) org.alljoyn.triumph.TriumphCPPAdapter

# Define the enviroment variables based on the building platform
ifeq ($(OS), Windows_NT)
	ALLJOYN_INC=$(ALLJOYN_DIR)/alljoyn_core/build/win7/x86_64/debug/dist/cpp/inc
	CFLAGS=-c -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/win32 -I$(ALLJOYN_INC) -fPIC -DQCC_OS_GROUP_WINDOWS
	ALLJOYN_LIBS=$(ALLJOYN_DIR)/alljoyn_core/build/win7/x86_64/debug/dist/cpp/lib/BundledDaemon.obj \
	$(ALLJOYN_DIR)/alljoyn_core/build/win7/x86_64/debug/dist/cpp/lib/ajdaemon.lib \
	$(ALLJOYN_DIR)/alljoyn_core/build/win7/x86_64/debug/dist/cpp/lib/alljoyn.lib
else
	
endif

ALLJOYN_INC=$(ALLJOYN_DIR)/build/linux/x86_64/debug/dist/cpp/inc
CFLAGS=-c -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I$(ALLJOYN_INC) -fPIC -DQCC_OS_GROUP_POSIX
ALLJOYN_LIBS=$(ALLJOYN_DIR)/build/linux/x86_64/debug/dist/cpp/lib/BundledDaemon.o \
	$(ALLJOYN_DIR)/build/linux/x86_64/debug/dist/cpp/lib/libajdaemon.a \
	$(ALLJOYN_DIR)/build/linux/x86_64/debug/dist/cpp/lib/liballjoyn.a

lib : java
	@echo Compiling the native code...
	$(CC) ${CFLAGS} TriumphCPPAdapter.cpp
	@echo Making shared library...
	$(CC) ${LFLAGS} -o libtriumph.so TriumphCPPAdapter.o $(ALLJOYN_LIBS) $(LIBS)
	@echo libtriumph.so Complete!

clean:
	@rm -f *.o
	@rm -f *.so
	@rm -f *.class
	@rm -f *.h
	@rm -f *.dll
