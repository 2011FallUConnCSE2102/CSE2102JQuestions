/*
 * Licensed to the Free Software Foundation (FSF) under one or more
 * contributor license agreements. The FSF licenses this file to You under 
 * the GNU General Public License, Version 3.0 (the "License"); you may 
 * not use this file except in compliance with the License.  You may obtain 
 * a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;


// ImageUtils
//-----------------------------------------------------------------------
/**
 * <p>ImageUtils instances should NOT be constructed in standard programming.
 * The methods have been declared as <code>static</code> therefore, the class 
 * methods should be used as <code>ImageUtils.getArrayFromImage(image, width, height)
 * </code>.</p>
 * 
 * <p>File for general utilities for processing images.</p>
 * <ul>
 * <li>Convert a java.awt.Image to an array of pixels.
 * <li>Convert an array of pixels to an java.awt.Image.
 * <li>Throw exceptions where necessary.
 * </ul>
 *
 * <p>Each method documents its behaviour in more detail.</p>
 *
 * @author Ken Williams <jquestions@truerandomness.com>
 */

public class ImageUtils {

	/**
     * 
     * <p>This constructor is public and only exists to permit tools that require 
     * a JavaBean instance to operate.</p>
     */
    public ImageUtils() {
        super();
    }

    // getArrayFromImage
    //-----------------------------------------------------------------------
    /**
     * <p>Extracts the pixels from a given Image and returns them as an array 
     * of primitive ints.</p>
     *
     * @param img  the Image to extract pixels from 
     * @param width   the image width
     * @param height  the image height 
     * @return a int[] with the extracted pixels
     * @throws InterruptedException (runtime) if the pixel grabber fails
     */    
	public static int[] getArrayFromImage(Image img, int width, int height)
			throws InterruptedException {
		int[] pixels = new int[width * height];
		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, 
				pixels, 0, width);
		pg.grabPixels();
		return pixels;
	} 


    // getImageFromArray
    //-----------------------------------------------------------------------
    /**
     * <p>To convert the array back into an instance of Image, use the
	 * java.awt.image.MemoryImageSource class along with java.awt.Toolkit.
     * Returns a java.awt.Image object based on a given set of pixels 
     * and the expected image width and height.</p>
     *
     * @param pixels  an array of pixels to be converted into an Image object    
     * @param width   the image width
     * @param height  the image height
     * @return a java.awt.Image objected constructed from the array of pixels 
     */    

	public static Image getImageFromArray(int[] pixels, int width, int height) {
		MemoryImageSource mis = new MemoryImageSource(width, height, 
				pixels, 0, width);
		Toolkit tk = Toolkit.getDefaultToolkit();
		return tk.createImage(mis);
	} 
}

