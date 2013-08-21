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

import java.io.FileNotFoundException;
import java.io.InputStream;

import javafx.scene.image.Image;

/**
 * Class that loads images stored in the directory 'images' directory
 * relative to the project root.
 * 
 * @author mhotan
 */
public class ImageLoader {

    private static final String mRootPath = "/images/";
    
    /**
     * Cannot instantiate.
     */
    private ImageLoader() {}
    
    /**
     * Loads image from images directory which is relative to root.
     * 
     * @param imageName Name of the image to load
     * @return Image associated with the name
     * @throws FileNotFoundException Image not found
     */
    public static Image loadImage(String imageName) throws FileNotFoundException {
        if (imageName.startsWith("/")) {
            imageName = imageName.substring(1);
        }
        InputStream is = ImageLoader.class.getResourceAsStream(mRootPath + imageName);
        if (is == null) {
            throw new FileNotFoundException("Image '" + imageName + "' not found in directory " + mRootPath);
        }
        return new Image(is);
    }
    
}
