package jquestionsgui;

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



import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTextField;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;

import question.Question;
import question.QuestionType;

/**
 * This class holds all the details and methods for each user
 * answer to a question. We need to store the state of a 
 * user answer to each question so as to allow the user to return 
 * to a question and change their answer, should they wish to.
 * 
 * Used in JQuestionsGUI object.
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see JQuestionsGUI
 * 
 */
public class UserAnswer {

	private ArrayList<Boolean> answerBoxes;
	private ArrayList<String> answerFields;
	private Question question;    // Each UserAnswer is 
	                              // associated with exactly 1 Question.
	private int questionIndex;
	private QuestionType questionType;
	private boolean correct = false;   // Is this answer correct ?
	private int options = 0;

	
	/**
	 * Construct a UserAnswer object for a given Question object
	 * and its index poosition.
	 *  
	 * @param q - The Question object.
	 * @param index - The index position of the Question. 
	 */
	
	public UserAnswer(Question q, int index) {
		question = q;
		questionIndex = index;
		questionType = question.getQuestionType();
		
		if (questionType == QuestionType.MULTI_CHOICE) {
			options = question.getPossibleAnswers().size();
			answerBoxes = new ArrayList<Boolean>();
			for (int i=0; i<options; i++) {
			    answerBoxes.add(new Boolean(false));
			}
		}
		else 
		if (questionType == QuestionType.DRAG_N_DROP) {
			options = question.getPossibleAnswers().size();
			answerFields = new ArrayList<String>();			
			DefaultStyledDocument doc = question.getQuestionDoc();
			if (doc == null) {
				System.out.println("doc is null");
				return;
			}
			
			for (int i = 0; i < doc.getLength(); i++) {
				Component comp = StyleConstants.getComponent(doc
						.getCharacterElement(i).getAttributes());
				if (comp != null && (comp instanceof JTextField)) {
                    answerFields.add(new String());					
				}
			}						
		}		
	}

	/**
	 * Various getter and setter methods required to store state, 
	 */
	
	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question q) {
		question = q;
	}
	
	public void setCorrectness(boolean correctness) {
		correct = correctness;
	}

	public boolean isCorrect() {
		return correct;
	}

	public Boolean getAnswerBox(int index) {
		return answerBoxes.get(index);
	}

	public void setAnswerBox(int index, Boolean b) {
		answerBoxes.set(index, b);
	}
	
	public String getAnswerField(int index) {
		return answerFields.get(index);
	}

	public void setAnswerField(int index, String str) {
		answerFields.set(index, str);
	}	
}
