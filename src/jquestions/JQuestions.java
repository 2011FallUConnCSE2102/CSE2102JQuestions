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

package jquestions; 

import java.util.ArrayList;

import jquestionsgui.JQuestionsGUI;
import jquestionsgui.UserAnswer;

import question.QuestionPool;
import utils.IOUtils;

/**
 * This is just a place-holder class for the JQuestions application object.
 * It stores the details associated with each instance of the application
 * in particular the Frame for the GUI, the pool of questions, the current
 * set of user answers (so far), and also the list of question Ids which 
 * will change once the questions have been shuffled into random order.
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see 
 * 
 */

public class JQuestions {
	
	// The GUI frame for the application. 
	private JQuestionsGUI frame;      
	
	// The pool from which questions are drawn.
	private QuestionPool questionPool; 

	// The ordered list of Ids (indices) of the questions asked.
	private ArrayList<Integer> questionIds;	

	// The list of user answers to the questions.
	private ArrayList<UserAnswer> userAnswers;

	
	JQuestions() {
		questionPool = new QuestionPool();
		userAnswers = new ArrayList<UserAnswer>();
		questionIds = new ArrayList<Integer>();
	}
	
	public Boolean isExam() {
		return questionPool.getIsExam();
	}	
	
	public int getNQuestions() {
		return questionPool.getNumberOfQuestions();
	}
	
	
	public void setFrame(final JQuestionsGUI guiInterface) {
		frame = guiInterface;
	}
	
	public JQuestionsGUI getFrame() {
		return frame;
	}
	
	public QuestionPool getQuestionPool() {
		return questionPool;
	}
	
	public ArrayList<UserAnswer> getUserAnswers() {
		return userAnswers;
	}
	
	public ArrayList<Integer> getQuestionIds() {
		return questionIds;
	}

	public void clearAll() {
		questionPool.removeAllQuestions();
		userAnswers.clear();
		questionIds.clear();
	}
	
	public void readResourceFile(final String fileName, Class classRef) {
        IOUtils.readFile(fileName, questionPool, classRef);
	}
	
	public void readFile(final String fileName) {
        IOUtils.readFile(fileName, questionPool, null);
	}
	
	public void writeFile(final String fileName) {
        IOUtils.writeFile(fileName, questionPool, false);
	}
}
