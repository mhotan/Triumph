 #******************************************************************************
 #* Copyright 2013, Qualcomm Innovation Center, Inc.
 #*
 #*    All rights reserved.
 #*    This file is licensed under the 3-clause BSD license in the NOTICE.txt
 #*    file for this project. A copy of the 3-clause BSD license is found at:
 #*
 #*        http://opensource.org/licenses/BSD-3-Clause.
 #*
 #*    Unless required by applicable law or agreed to in writing, software
 #*    distributed under the license is distributed on an "AS IS" BASIS,
 #*    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 #*    See the license for the specific language governing permissions and
 #*    limitations under the license.
 #******************************************************************************/

import os
import sys

# Returns a list of all the subdirectory from this
# root directory
def getAllDirs(path):
	# Adds all the directories of the same level to this.
	def addDirectories(paths, dirName, names):
		for name in names:
			fle = os.path.join(dirName, name)
			if os.path.isdir(fle):
				paths.append(fle)
	paths = [path]
	os.path.walk(path, addDirectories, paths)
	return paths

def ConfigureJNI(env):
	"Configure the given enviroment for compiling JNI"

	if not env.get('JAVAC'):
		print "The Java Compiler must be installed and in the current path."
		return 0

	# first look for a shell variable called JAVA_HOME
	java_root = os.environ.get('JAVA_HOME')
	if not java_root:
		if sys.platform == 'darwin':
			# Apple's OS X has its own special java base directory
			java_root = '/System/Library/Frameworks/JavaVM.framework'
		else:
			# Search for the java compiler
			print "JAVA_HOME environment variable is not set. Searching for java... ",
			javaCDir = os.path.dirname(env.WhereIs('javac'))
			if not javaCDir:
				print "JAVA_HOME not found."
				return 0
			# assuming the compiler found is in some directory like
			# /usr/jdkX.X/bin/javac, java's home directory is /usr/jdkX.X
			java_root = os.path.join(javaCDir, "..")
			print "found."

	if sys.platform == 'cygwin':
		# Handle the different names 
		java_root = os.popen("cygpath -up '"+java_root+"'").read().replace('\n', '')

	if sys.platform == 'darwin':
		# Apple does not use Sun's naming convention
		java_headers = [os.path.join(java_root, 'Headers')]
		java_libs = [os.path.join(java_root, 'Libraries')]
	else:
		# windows and linux
		java_headers = [os.path.join(java_root, 'include')]
		java_libs = [os.path.join(java_root, 'lib')]
		# Sun's windows and linux JDKs keep system-specific header
		# files in a sub-directory of include
		if java_root == '/usr' or java_root == '/usr/local':
 			# too many possible subdirectories. Just use defaults
			java_headers.append(os.path.join(java_headers[0], 'win32'))
			java_headers.append(os.path.join(java_headers[0], 'linux'))
			java_headers.append(os.path.join(java_headers[0], 'solaris'))
		else:
			# add all subdirs of 'include'. The system specific headers
			# should be in there somewhere
			java_headers = getAllDirs(java_headers[0])

	# add Java's include and lib directory to the environment
	env.Append(CPPPATH = java_headers)
	env.Append(LIBPATH = java_libs)
	
	# add any special platform-specific compilation or linking flags
	if sys.platform == 'darwin':
		env.Append(SHLINKFLAGS = '-dynamiclib -framework JavaVM')
		env['SHLIBSUFFIX'] = '.jnilib'
	elif sys.platform == 'cygwin':
		env.Append(CCFLAGS = '-mno-cygwin')
		env.Append(SHLINKFLAGS = '-mno-cygwin -Wl,--kill-at')
		
	# Add extra potentially useful environment variables
	env['JAVA_HOME'] = java_root
	env['JNI_CPPPATH'] = java_headers
	env['JNI_LIBPATH'] = java_libs
	return 1