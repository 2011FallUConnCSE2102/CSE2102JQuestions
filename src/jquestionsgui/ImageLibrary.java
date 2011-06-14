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

package jquestionsgui;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import javax.swing.*;

/**
 * This class defines some simple images programatically. 
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * 
 */

public class ImageLibrary {

	private static JFrame jFrame = new JFrame();
	private static final long serialVersionUID = 1L;

	// Create some shortcut constants for yellow, black, and white
    final private static int y = Color.yellow.getRGB();
    final private static int b = Color.black.getRGB();
    final private static int w = Color.white.getRGB();
    final private static int g = Color.green.getRGB();
    final private static int r = Color.red.getRGB();
    final private static int x = Color.white.getRGB();


    //Define an array of pixel values. The pixels will be converted
    //into a 16x16 image.
    
    final private static int tickImageData[] = {
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, x, g, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, g, x, x, x, x,
           x, x, x, x, x, x, x, x, x, x, g, x, x, x, x, x,
           x, x, x, x, x, x, x, x, x, g, x, x, x, x, x, x,
           x, x, g, x, x, x, x, x, g, x, x, x, x, x, x, x,
           x, x, x, g, x, x, x, g, x, x, x, x, x, x, x, x,
           x, x, x, x, g, x, g, x, x, x, x, x, x, x, x, x,
           x, x, x, x, x, g, x, x, x, x, x, x, x, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x
      };
    

    final private static int crossImageData[] = {
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,
           x, x, x, r, x, x, x, x, x, x, x, x, r, x, x, x,
           x, x, x, x, r, x, x, x, x, x, x, r, x, x, x, x,
           x, x, x, x, x, r, x, x, x, x, r, x, x, x, x, x,
           x, x, x, x, x, x, r, x, x, r, x, x, x, x, x, x,
           x, x, x, x, x, x, x, r, r, x, x, x, x, x, x, x,
           x, x, x, x, x, x, x, r, r, x, x, x, x, x, x, x,
           x, x, x, x, x, x, r, x, x, r, x, x, x, x, x, x,
           x, x, x, x, x, r, x, x, x, x, r, x, x, x, x, x,
           x, x, x, x, r, x, x, x, x, x, x, r, x, x, x, x,
           x, x, x, r, x, x, x, x, x, x, x, x, r, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,
           x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x
      };

    private static Image greenTickInstance = null;
    private static Image redCrossInstance = null;
    private static ImageIcon greenTickIconInstance = null;
    private static ImageIcon redCrossIconInstance = null;

    // private constructor.
 	private ImageLibrary() {}

 	/**
 	 * Create a Singleton image from the array of pixels
 	 * 
 	 * @return the Image
 	 */
 	public static Image getGreenTickImage() {
 		if (greenTickInstance == null) {         
 	        greenTickInstance = jFrame.createImage(new MemoryImageSource(16, 16, tickImageData, 0, 16)); 			
 		}
 		
 		return greenTickInstance;
 	}

 	/**
 	 * Create a Singleton image from the array of pixels
 	 * 
 	 * @return the Image
 	 */
 	public static Image getRedCrossImage() {
 		if (redCrossInstance == null) {         
 	        redCrossInstance = jFrame.createImage(new MemoryImageSource(16, 16, crossImageData, 0, 16)); 			
 		}
 		
 		return redCrossInstance;
 	}

 	/**
 	 * Create a Singleton ImageIcon from the array of pixels
 	 * 
 	 * @return the ImageIcon
 	 */	
 	public static ImageIcon getGreenTickIcon() {
 		if (greenTickIconInstance == null) {         
 	        greenTickIconInstance = new ImageIcon(jFrame.createImage(new MemoryImageSource(16, 16, tickImageData, 0, 16))); 			
 		}
 		
 		return greenTickIconInstance;
 	}
 	
 	/**
 	 * Create a Singleton ImageIcon from the array of pixels
 	 * 
 	 * @return the ImageIcon
 	 */	 	
 	public static ImageIcon getRedCrossIcon() {
 		if (redCrossIconInstance == null) {
 	        redCrossIconInstance = new ImageIcon(jFrame.createImage(new MemoryImageSource(16, 16, crossImageData, 0, 16))); 			
 		}
 		return redCrossIconInstance;
 	}	
}
