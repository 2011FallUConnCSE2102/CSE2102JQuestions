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


package question;

import java.awt.Component;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.text.StyleConstants;


/**
 * Each .jqs file contains one QuestionPool. Each QuestionPool contains a list 
 * of one or more Question objects. This class stores the details and methods 
 * associated with each Question.
 * 
 * Note that a question may be currently one of two types - MULTI_CHOICE or
 * DRAG_N_DROP (as defined by the enum QuestionType). It should be easy to 
 * add other types in the future.
 *   
 * MULTI_CHOICE questions are displayed using the MultiChoiceQuestionsScreen.
 * DRAG_N_DROP questions are displayed using the DragAndDropQuestionsScreen. 
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see QuestionPool
 * @see QuestionType
 * 
 */

public class Question {  

	// String version of main question text.
	private String questionText;

	// Demo text (secondary display box) of question.
	private String demoText;
	
	// List of possible answer objects.
	private List<PossibleAnswer> possibleAnswers;
	
	// List of index positions of correct answers.
	private List<Integer> correctAnswers;   
	
	// The question type.
	private QuestionType questionType;
	
	// The actual main display document. May contain text, images or objects.
	private DefaultStyledDocument questionDoc;

	// Text for hints and explanations.
	private String hintText;
	private String explainText;
	
	// Is question currently marked ?
	private Boolean marked;
	
	
	/**
	 * Question constructors.
	 */
	
	public Question() {} 	
		
	public Question(String demoStr, String qText, List<PossibleAnswer> pAnswers, 
			List<Integer> cAnswers, QuestionType qType, 
			DefaultStyledDocument qDoc,	String hText, String eText, Boolean isMarked) {
		demoText = demoStr;
	    questionText = qText;
	    possibleAnswers = pAnswers;
	    correctAnswers = cAnswers;
	    questionType = qType;
	    questionDoc = qDoc;
	    hintText = hText;
	    explainText = eText;
	    marked = isMarked;
	}

	/**
	 * Mainly getters and setters type methods. 
	 */
	
	public String getDemoText() {
		return demoText;
	}

	public void setDemoText(String dText) {
		demoText = dText;
	}
	
	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String qText) {
		questionText = qText;
	}

	public List<PossibleAnswer> getPossibleAnswers() {
		return possibleAnswers;
	}

	public void setPossibleAnswers(List<PossibleAnswer> pAnswers) {
	    possibleAnswers.clear();
	    possibleAnswers.addAll(pAnswers);
	}

	public int getNumberOfPossibleAnswers() {
		return possibleAnswers.size();
	}
	
	public List<Integer> getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(List<Integer> cAnswers) {
	    correctAnswers.clear();
	    correctAnswers.addAll(cAnswers);
	}
			
	public void setMarked(Boolean b) {
		marked = b;
	}
	
	public Boolean isMarked() {
		return marked;
	}
	
	public QuestionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionType qType) {
		questionType = qType;
	}

	public DefaultStyledDocument getQuestionDoc() {
		return questionDoc;
	}

	public void setQuestionDoc(DefaultStyledDocument qDoc) {
		questionDoc = qDoc;
	}
	
	public String getHintText() {
		return hintText;
	}
	
	public void setHintText(String hText) {
		hintText = hText;
	}
	
	public String getExplainText() {
		return explainText;
	}
	
	public void setExplainText(String eText) {
		explainText = eText;
	}
	
	/**
	 * The random shuffling of answers calls the appropriate method
	 * depending on question type.
	 * 
	 * For each Question, answers are randomly shuffled each time 
	 * JQuestions is started to prevent the user from 'visually' 
	 * learning answers based on their position. 
	 */
	
	public void shuffleAnswers() {
		if (questionType == QuestionType.MULTI_CHOICE) {
		    shuffleAnswersForMultiChoice();
		}
		else 		
		if (questionType == QuestionType.DRAG_N_DROP) {
		    shuffleAnswersForDnD();
		}
	}
	

	/**
	 * Use a Calendar to init the random number generator 
	 * based on current time, used to shuffle questions 
	 * and answers.  
	 * 
	 * @return a random integer.
	 */
	
	public int initRandomNumberGenerator() {
		int randVal = 0;
		// Shake up the random number generator.
		Calendar calendar = Calendar.getInstance();
		int secs = calendar.get(Calendar.SECOND);
		for (int count = 0; count < secs; count++)
			randVal = (int) Math.random();
		return randVal;
	}

	/**
	 * Shuffle answers of a DRAG_N_DROP type of question.
	 * 
	 * To maintain relationship with correct answers the indices
	 * of the correctAnswers list needs to be updated with the 
	 * new location of each shuffled PossibleAnswer.   
	 */

	public void shuffleAnswersForDnD() {

		// Warm up random number generator.
		int randVal = initRandomNumberGenerator();

		int pSize = possibleAnswers.size();

		// tempPossibleAnswers is a list of possible answers which will be shuffled.
		List<PossibleAnswer> tempPossibleAnswers = new ArrayList<PossibleAnswer>();
		
		// newPossibleAnswers is the list of new possible answers which we
		// will build up from the shuffled list of tempPossibleAnswers.
		List<PossibleAnswer> newPossibleAnswers = new ArrayList<PossibleAnswer>();
		
		// The newCorrectAnswers list will also be built from the shuffled
		// list of tempPossibleAnswers.
		List<Integer> newCorrectAnswers = new ArrayList<Integer>();

		for (PossibleAnswer p : possibleAnswers) {
			tempPossibleAnswers.add(new PossibleAnswer(p.getTheAnswer(), false));
			newPossibleAnswers.add(new PossibleAnswer());
		}

		for (Integer i : correctAnswers) {
			tempPossibleAnswers.get(--i).setCorrectness(true);
			newCorrectAnswers.add(new Integer(0));
		}

		/**
		 * The idea here is:
		 * - Go through each possible answer
		 * - Insert it into a random position in the new list.
		 * - If the answer is also a 'correct' then record its new
		 *   index position in correctAnswers.
		 */

		for (int i=1; i<=tempPossibleAnswers.size(); i++) {
			PossibleAnswer answer = tempPossibleAnswers.get(i-1);
			
			// Find an empty slot in the new list and insert the record.
			PossibleAnswer pa = null;
			int insertVal = 0;
			do {
				insertVal = (int) (Math.random() * pSize);
				pa = newPossibleAnswers.get(insertVal);
			} while (!pa.getTheAnswer().isEmpty());

			newPossibleAnswers.set(insertVal, answer); 

			// If answer is also correct then we must record 
			// its index position in the new list of correctAnswers.
			if (answer.isCorrect()) {
				for (int i2=1; i2<=newCorrectAnswers.size(); i2++) {
					if (i == correctAnswers.get(i2-1)) {
					    newCorrectAnswers.set(i2-1, insertVal+1);
					}
				}
			}
			
		}
		
		// Update the lists with new values.
		setPossibleAnswers(newPossibleAnswers);
		setCorrectAnswers(newCorrectAnswers);
	}	

	
	/**
	 * Shuffle answers of a MULTI_CHOICE type of question.
	 *
	 * Keeping the correctAnswers in order is not important.
	 */
	
	public void shuffleAnswersForMultiChoice() {
		int randVal = initRandomNumberGenerator();
		int pSize = possibleAnswers.size();

		List<PossibleAnswer> currentPossibleAnswers = new ArrayList<PossibleAnswer>();
		List<PossibleAnswer> newPossibleAnswers = new ArrayList<PossibleAnswer>();
		List<Integer> newCorrectAnswers = new ArrayList<Integer>();

		for (PossibleAnswer p : possibleAnswers) {
			currentPossibleAnswers.add(new PossibleAnswer(p.getTheAnswer(), false, 0, p.getAnswerExplanation()));
			newPossibleAnswers.add(new PossibleAnswer());
		}

		for (Integer i : correctAnswers) {
			currentPossibleAnswers.get(i).setCorrectness(true);
		}

		while (currentPossibleAnswers.size() > 0) {

			randVal = (int) (Math.random() * currentPossibleAnswers.size());

			PossibleAnswer answer = currentPossibleAnswers.remove(randVal);

			PossibleAnswer pa = null;
			do {
				randVal = (int) (Math.random() * pSize);
				pa = newPossibleAnswers.get(randVal);
			} while (!pa.getTheAnswer().isEmpty());

			newPossibleAnswers.set(randVal, answer);

			if (answer.isCorrect()) {
				newCorrectAnswers.add(new Integer(randVal + 1));
			}
		}

		// Update the lists with new values.
		setPossibleAnswers(newPossibleAnswers);
		setCorrectAnswers(newCorrectAnswers);
	}

	/**
	 * Return a String representation of the Question.
	 */
	
	public String toString() {
		StringBuilder sb = new StringBuilder("demoText = " + demoText + "\n" +
		       "questionText = " + questionText + "\n" +		       		       
		       "questionType = " + questionType + "\n" +
		       "questionDoc = " + questionDoc + "\n" +
		       "correctAnswers = " + correctAnswers + "\n" +
			   "possibleAnswers = " + possibleAnswers + "\n");

		for (PossibleAnswer pa: possibleAnswers) {
            sb.append(pa.getTheAnswer() + " | " + pa.getAnswerExplanation() + "\n");			
		}
				
		return sb.toString();
 
	}

	/**
	 * Useful debug code - analyzes the contents of a DefaultStyledDocument 
	 * object identifies all its Components (chars, Images, Components, etc). 
	 */	
	public void analyzeDoc() {
		DefaultStyledDocument doc = questionDoc;
		if (doc == null) {
			System.out.println("questionDoc is null!");
			return;
		}
		int len = doc.getLength();
		System.out.println("len = " + len);

		// Do a text dump of the DefaultStyledDocument.
		//doc.dump(System.out);
		
		for (int i = 0; i < doc.getLength(); i++) {
			
			Element e = doc.getCharacterElement(i);
			System.out.println("[" + i + "] = " + e + " XXX " + e.getClass().getName());
			
			int nleft = doc.getLength();			
			Segment text = new Segment();
			int offs = i;
			//text.setPartialReturn(true); 
			try {
	            doc.getText(offs, 1, text);
				System.out.println("Segment => " + text);
			} 
			catch (Exception ble) {
				System.out.println("caught BadLocationException" + ble);			
			}		

			
			// Identify any JTextFields
			Component comp = StyleConstants.getComponent(doc
					.getCharacterElement(i).getAttributes());
			if (comp != null) {
				System.out.println("Component = " + comp.getClass().getName());
			}			
			if (comp != null && (comp instanceof JTextField)) {
				System.out.println("Found a JTextField at location " + i);
				JTextField jtf = (JTextField) comp;
				String str = jtf.getText();
				System.out.println("   getText() = " + str);
				str = jtf.getName();
				System.out.println("   getName() = " + str);
			}
			
			// Identify any ImageIcons			
			Icon icon = StyleConstants.getIcon(doc
					.getCharacterElement(i).getAttributes());
	
			if (icon != null && (icon instanceof Icon)) {
				System.out.println("Found an Icon at location " + i);
				ImageIcon imageIcon = (ImageIcon) icon;
				int w = imageIcon.getIconWidth();
				int h = imageIcon.getIconHeight();
				Image image = imageIcon.getImage();
				String str = imageIcon.toString();
				System.out.println(" h w = " + h + " " + w);
				System.out.println(" str = " + str);				
			}		
		}
	}
}

