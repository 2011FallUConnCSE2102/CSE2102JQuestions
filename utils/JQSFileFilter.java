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

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 * A file-extension filter used in the JFileChooser.
 * Allows user to see all the JQS files in a directory. 
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see ImageFileFilter
 */


public class JQSFileFilter extends FileFilter {

    //Accept all directories and all .jqs files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals("jqs")) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }


    /**
     * Get the extension of a filename.
     * 
     * @param f - the file.
     * @return - the extension.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }   
    
    //The description of this filter
    public String getDescription() {
        return "JQuestions files";
    }
}


