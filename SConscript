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
Import('env')

def PrependDir(dir, filelist):
    return [os.path.join(dir,x) for x in filelist]

platform = sys.platform
if platform.startswith('linux'): 
    platform = 'linux'
elif platform.startswith('win32') or platform.startswith('cygwin'):
    platform = 'windows'
elif platform.startswith('darwin'):
    platform = 'darwin'
else:
    print "Unsupported platform: " + sys.platform
    Exit(0)

# Generate all the class files
third_party_jars_dir = env.Dir('libs/jars')
third_party_jars = ['alljoyn.jar', 'jfxrt.jar']

# Create the concatonated string of the 3rd party jars to add to classpath
# This is the argument flag -cp or -classpath with arg values of
# jar1:jar2... for linux and jar1;jar2... for windows.
concat_jars = ""
for jar in third_party_jars:
    if platform == 'windows':
        concat_jars += ';'
    else:
        concat_jars += ':'
    concat_jars += third_party_jars_dir.abspath + "/" + jar
concat_jars = concat_jars[1:]

# Append the classpath to the enviroment
env.Append(JAVACLASSPATH = concat_jars)    

# compile java classes into platform independent 'classes' directory
jni_classes = env.Java('classes', 'src')
jni_headers = env.JavaH('jni', jni_classes)

# compile native classes into platform dependent 'lib-XXX' directory
# NOTE: javah dependencies do not appear to work if SConscript was called
# with a build_dir argument, so we take care of the build_dir here

native_dir = platform
native_src = PrependDir(native_dir, env.Split("""TriumphCPPAdapter.cpp"""))
env.VariantDir(native_dir, 'jni', duplicate=0)
env.SharedLibrary('libs/lib/' + native_dir + '/triumph', native_src)

# Attempt to load the AllJoyn library and jar
# this is dependent if the ALLJOYN_HOME directory is set.
aj_home = os.environ.get('ALLJOYN_HOME')
if not aj_home:
    print "Environment Variable ALLJOYN_HOME not found, build may not be complete"
