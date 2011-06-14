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

package jquestionseditorgui;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * This class defines the listener for the JSpinners used
 * in the editing functions. They listen for changes to the JSpinner objects 
 * and update the number of options (i.e. the number of possible answers)
 * for the Panel. They do this by calling the Panels' setOptions() method
 * which will update the panel layout dynamically.    
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see EditorQuestionScreen
 * @see DragAndDropQuestionScreen
 * @see MultiChoiceQuestionScreen
 * 
 */
public class SpinnerListener implements ChangeListener {
	
	  private EditorQuestionScreen parent;
	
	  SpinnerListener(EditorQuestionScreen eqs) {
		  super();
		  parent = eqs;		  
	  }
	
	  public void stateChanged(ChangeEvent evt) {
	    JSpinner spinner = (JSpinner) evt.getSource();

	    // Get the new value 
	    String value = spinner.getValue().toString();
	    Integer options = Integer.parseInt(value);

	    // Notify your parent!
	    parent.setOptions(options);
	  }
}