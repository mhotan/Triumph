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

package org.alljoyn.triumph.util.loaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * A Class that loads Native libraries based on the OS architecture
 * 
 * @author mhotan@quiinc.com, Michael Hotan
 */
public class NativeLoader {

    public static final Logger LOG = Logger.getGlobal();

    /**
     * String representation for Windows x86 architecture
     */
    private static final String ARCHITECTURE_X86 = "x86";

    /**
     * String representation for Linux AMD 64 bit architecture
     */
    private static final String ARCHITECTURE_AMD64 = "amd64";

    /**
     * String representation for 64-bit linux on itanium architecture
     */
    private static final String ARCHITECTURE_IA64 = "ia64";

    /**
     * String representation for Linux AMD 32 bit architecture
     */
    private static final String ARCHITECTURE_I386 = "i386";

    /**
     * String representation for the native library directory for Windows x86 architecture.
     * <p>  This string is vital important for this loader.
     */
    public static final String DIRECTORY_X86 = "win-x86";

    /**
     * String representation for the native library directory for Linux AMD 64 bit architecture.
     * <p>  This string is vital important for this loader.
     */
    public static final String DIRECTORY_AMD64 = "linux-amd64";

    /**
     * String representation for the native library directory for 64-bit linux on itanium architecture
     * <p>  This string is vital important for this loader.
     */
    public static final String DIRECTORY_IA64 = "linux-ia64";

    /**
     * String representation for the native library directory for Linux AMD 32 bit architecture
     * <p>  This string is vital important for this loader.
     */
    public static final String DIRECTORY_I386 = "linux-x86";

    private static final String LINUX_SUFFIX = ".so";
    private static final String WINDOWS_SUFFIX = ".dll";
    private static final String WINDOWS_PREFIX = "win";
    private static final String LINUX_PREFIX = "linux";
    
    private final String mLibPath;

    /**
     * Loads the library relative to the location of this
     * NativeLoader.class location.
     */
    public NativeLoader() {
        mLibPath = "/lib/";
    }

    /**
     * Given a path to a directory that is organized as
     * <br>
     * <br>--<project folder>/libraryPaths
     * <br>----NativeLoader.DIRECTORY_X86 or win-x86
     * <br>----NativeLoader.DIRECTORY_AMD64 or linux-amd64
     * <br>----NativeLoader.DIRECTORY_IA64 or linux-ia64
     * <br>----NativeLoader.DIRECTORY_I386 or linux-x86
     * 
     * <p>
     * This creates a Native loader that loads native libraries found within
     * the directory.  It is up to the client and developer to make sure the correct
     * so files are found in the respective directory
     * 
     * @param libraryPaths Paths to the OS local folders
     * @throws IllegalArgumentException
     */
    public NativeLoader(String libraryPaths) throws IllegalArgumentException {
        if (libraryPaths == null) 
            throw new NullPointerException("Null library path");
        mLibPath = libraryPaths;

        // check that the path leads to a directory.
        File file = new File(mLibPath);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Library path " + mLibPath);
        }
    }

    /**
     * Attempts to load the library with name 'libName'.  
     * 
     * @param libName Name of the library to load.
     * @return Returns self reference for easy chaining.
     */
    public NativeLoader loadLibrary(String libName) {
        try {
            System.load(saveLibrary(libName));
        } catch (IOException e) {
            LOG.warning("Could not find library " + libName +
                    " as resource, trying fallback lookup through System.loadLibrary");
            System.loadLibrary(libName);
        } 
        return this;
    }

    /**
     * Returns the library name with respect to the os architecture.
     * <br>Clients can specify to include the path.  If the path is not included then
     * the path will be defaulted based on the execution enviroment.
     * 
     * <p>NOTE: Ensure the name does not have any arhcitecture specific prefixes or suffixes.
     * 
     * @param library Bare bones library name
     * @param includePath true to include the predefined path, false otherwise.
     * @return The path to the library from the base path inputted at NativeLoader init time.
     */
    private String getOSSpecificName(String library, boolean includePath) {
        String osArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name").toLowerCase();
        String name;
        String path;

        if (osName.startsWith(WINDOWS_PREFIX)) {
            if (osArch.equalsIgnoreCase(ARCHITECTURE_X86)) {
                name = library + WINDOWS_SUFFIX;
                path = DIRECTORY_X86;
            } else {
                throw new UnsupportedOperationException("Platform " + osName + "_" + osArch + " not supported");
            }
        } else if (osName.startsWith(LINUX_PREFIX)) {
            if (osArch.equalsIgnoreCase(ARCHITECTURE_AMD64)) {
                name = "lib" + library + LINUX_SUFFIX;
                path = DIRECTORY_AMD64;
            } else if (osArch.equalsIgnoreCase(ARCHITECTURE_IA64)) {
                name = "lib" + library + LINUX_SUFFIX;
                path = DIRECTORY_IA64;
            } else if (osArch.equalsIgnoreCase(ARCHITECTURE_I386)) {
                name = "lib" + library + LINUX_SUFFIX;
                path = DIRECTORY_I386;
            } else {
                throw new UnsupportedOperationException("Platform " + osName + "_" + osArch + " not supported");
            }
        } else {
            throw new UnsupportedOperationException("Platform " + osName + "_" + osArch + " not supported");
        }

        // Symbolize the file is a directory
        path += "/";
        return includePath ? path + name : name;
    } 

    /**
     * Saves the library defined by lib path
     * 
     * @param libPath Path to library
     * @return Return the temporary path of the library.
     * @throws IOException
     */
    private String saveLibrary(String libPath) throws IOException {

        InputStream in = null;
        OutputStream out = null;
        try {
            // 
            String libraryName = getOSSpecificName(libPath, true);
            in = getClass().getResourceAsStream(mLibPath + libraryName);

            // This is the case where the library path does not point to the library folder.
            if (in == null) {
                throw new FileNotFoundException("File at " + mLibPath + libraryName + ". Please make sure the " +
                        "that the libraries are placed in the correct lib folder");
            }
            
            int cnt;
            byte buf[] = new byte[16 * 1024];

            // Retrieve the temporary directory name
            String tmpDirName = System.getProperty("java.io.tmpdir");
            File tmpDir = new File(tmpDirName);
            if (!tmpDir.exists()) {
                tmpDir.mkdir();
            }
            File file = File.createTempFile(libPath + "-", ".tmp", tmpDir);
            // Clean up the file when exiting
            file.deleteOnExit();
            out = new FileOutputStream(file);

            // copy until done.
            while ((cnt = in.read(buf)) >= 1) {
                out.write(buf, 0, cnt);
            }
            return file.getAbsolutePath();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

}
