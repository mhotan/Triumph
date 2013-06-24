ALLJOYN_DIR=/home/mhotan/alljoyn
#ALLJOYN_DIR=/home/mathewm/work2/alljoyn

# Define a variable for classpath
CLASS_PATH=../bin:$(ALLJOYN_DIR)/alljoyn_java/bin/jar/alljoyn.jar
CC=g++
#JAVA_HOME=/usr/lib/jvm/jdk1.7.0_25/
ALLJOYN_INC=$(ALLJOYN_DIR)/build/linux/x86_64/debug/dist/cpp/inc
ALLJOYN_LIBS=$(ALLJOYN_DIR)/build/linux/x86_64/debug/dist/cpp/lib/BundledDaemon.o \
	$(ALLJOYN_DIR)/build/linux/x86_64/debug/dist/cpp/lib/libajdaemon.a \
	$(ALLJOYN_DIR)/build/linux/x86_64/debug/dist/cpp/lib/liballjoyn.a
LIBS=-lrt -lstdc++ -lpthread -lcrypto -lssl -lm
# LIBS=/usr/lib/x86_64-linux-gnu
CFLAGS=-c -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I$(ALLJOYN_INC) -fPIC -DQCC_OS_GROUP_POSIX
LFLAGS=-shared

# Define a virtual path for .class in the bin directory
vpath %.class $(CLASS_PATH)

all : lib

java : ../bin/org/alljoyn/triumph/TriumphCPPAdapter.class
	@echo Generating header file for JNI
	javah -classpath $(CLASS_PATH) org.alljoyn.triumph.TriumphCPPAdapter

lib : java
	echo Compiling the native code...
	$(CC) ${CFLAGS} TriumphCPPAdapter.cpp
	echo Making shared library...
	$(CC) ${LFLAGS} -o libtriumph.so TriumphCPPAdapter.o $(ALLJOYN_LIBS) $(LIBS)
	echo libtriumph.so Complete!

clean:
	@rm -f *.o
	@rm -f *.so
	@rm -f *.class
	@rm -f *.h
