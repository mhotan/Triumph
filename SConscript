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
from shutil import copyfile
Import('env')

def PrependDir(dir, filelist):
    return [os.path.join(dir,x) for x in filelist]

platform = sys.platform
if platform.startswith('linux'): 
    platform = 'linux'
elif platform.startswith('win32') or platform.startswith('cygwin'):
    platform = 'windows'
    env['ENV']['TMP'] = os.environ['TMP']
elif platform.startswith('darwin'):
    platform = 'darwin'
else:
    print "Unsupported platform: " + sys.platform
    Exit(0)

# Generate all the class files
third_party_jars_dir = env.Dir('libs/jars')
third_party_jars = ['alljoyn.jar', 'jfxrt.jar', 'alljoyn_about.jar']

# Create the concatonated string of the 3rd party jars to add to classpath
# This is the argument flag -cp or -classpath with arg values of
# jar1:jar2... for linux and jar1;jar2... for windows.
concat_jars = ""
for jar in third_party_jars:
    if platform == 'windows':
        concat_jars += ';'
        concat_jars += third_party_jars_dir.abspath + "\\" + jar
    else:
        concat_jars += ':'
        concat_jars += third_party_jars_dir.abspath + "/" + jar
concat_jars = concat_jars[1:]

# Append the classpath to the enviroment
env.Append(JAVACLASSPATH = concat_jars)    

# compile java classes into platform independent 'classes' directory
print 'Compiling java classes'
jni_classes = env.Java('classes', 'src')
print 'Creating java native interface headers'
jni_headers = env.JavaH('jni', ['classes/org/alljoyn/triumph/TriumphCPPAdapter.class'])

# compile native classes into platform dependent 'lib-XXX' directory
# NOTE: javah dependencies do not appear to work if SConscript was called
# with a build_dir argument, so we take care of the build_dir here

native_dir = platform
native_src = PrependDir(native_dir, env.Split("""TriumphCPPAdapter.cpp"""))
env.VariantDir(native_dir, 'jni', duplicate=0)
print 'Creating project shared library'
env['LIBS'] = ['stdc++']
env.SharedLibrary('libs/lib/' + native_dir + '/triumph', native_src)
native_libs = os.path.join(os.path.join('libs', 'lib'), native_dir)
print 'Native library location: ' + native_libs

# Attempt to load the AllJoyn library and jar
# this is dependent if the ALLJOYN_HOME directory is set.
aj_home = os.environ.get('ALLJOYN_HOME')
if not aj_home:
    print 'WARNING! Environment Variable ALLJOYN_HOME not found, build may not be complete.' \
    '  Possibly missing alljoyn.jar and alljoyn_java native library'
    Exit(1)

print 'Attempting to find related alljoyn files from ' + aj_home 
# Extract the alljoyn java subdirectory
aj_java = os.path.join(aj_home, "alljoyn_java")
if not aj_home:
    print 'WARNING! alljoyn_java directory not found!  Possibly missing alljoyn.jar and alljoyn_java native library'
    Exit(1)

# Extract all the standard directories for alljoyn java
# alljoyn_java
# --bin
# ----jar
# ------alljoyn.jar
# ----libs
# ------ liballjoyn.so or liballjoyn.dll
aj_java_bin = os.path.join(aj_java, "bin")
aj_java_jar_dir = os.path.join(aj_java_bin, "jar")
aj_java_libs = os.path.join(aj_java_bin, "libs")

# Make sure there is the correct directories
if not aj_java_jar_dir or not aj_java_libs:
    print 'WARNING! No Alljoyn java jars or libs'
    Exit(1)

# Check if the alljoyn.jar exists.
aj_java_jar = os.path.join(aj_java_jar_dir, "alljoyn.jar")
if not os.path.isfile(aj_java_jar):
    print "WARNING! Unable to find alljoyn.jar in " + aj_java_jar_dir
copyfile(aj_java_jar, 'libs/jars/alljoyn.jar')

# Count the number of native libraries
num_files = len([f for f in os.listdir(aj_java_libs)
                if os.path.isfile(os.path.join(aj_java_libs, f))])

# If the number of libraries is 0 notify the user
if num_files == 0:
    print "WARNING! No native libraries found at " + aj_java_libs
    Exit(1)

# Copy all the library related files to our project directory.
for f in os.listdir(aj_java_libs):
    f_path = os.path.join(aj_java_libs, f)
    new_path = os.path.join(native_libs, f)
    copyfile(f_path, new_path)