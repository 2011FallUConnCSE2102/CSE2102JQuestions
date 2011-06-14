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


package jquestionseditor;


import question.QuestionPool;
import jquestionseditorgui.JQuestionsEditorGUI;

/**
 * This is just a place-holder class for the JQuestionsEditor application 
 * object.
 * It stores the details associated with each instance of the application
 * in particular the Frame for the GUI and the pool of questions.
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see JQuestions
 * 
 */

public class JQuestionsEditor {

	private JQuestionsEditorGUI frame;
	private QuestionPool questionPool;
    
	JQuestionsEditor() {
		// Init new pool for questions
		questionPool = new QuestionPool();
	}
		
	public void setFrame(final JQuestionsEditorGUI guiInterface) {
		frame = guiInterface;
	}
	
	public JQuestionsEditorGUI getFrame() {
		return frame;
	}
	
	public QuestionPool getQuestionPool() {
		return questionPool;
	}	
	
	public void setQuestionPool(final QuestionPool qp) {
		questionPool = qp;
	}		
}

