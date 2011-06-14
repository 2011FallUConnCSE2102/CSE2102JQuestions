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

import java.util.LinkedList;
import java.util.List;


/**
 * Each .jqs file contains one QuestionPool. Each QuestionPool contains a list 
 * of one or more Question objects. This class stores the details and methods 
 * associated with each QuestionPool.
 *
 * The Question 'Pool' is essentially a List.
 * In JQuestions the questions are shuffled and displayed in random order.
 * In JQuestions Editor there is no shuffling and questions and answers are 
 * displayed in the order in which they were created.    
 *
 * Along with the list of questions, each .jqs file also stores various 
 * configuration settings, e.g. is there a time-limit, how long, are
 * hints available ('Quiz' mode) or not ('Exam mode), amongst others.    
 * These are also stored in with the QuestionPool. 
 *   
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see Question
 * @see IOUtils
 * 
 */

public class QuestionPool {    

	// The question 'pool' is essentially a List. 
	private List<Question> qList;  

	// Current Configuration Settings for this Question pool.
	private String descriptionText;
	private Boolean isChangeAllowed;
	private Boolean isExam;
	private Boolean timed;
	private Integer timeLimit;   
	private Boolean displayFinalScore;
	private Boolean displayPrintOption;
	private String studentName;
	private Integer numberOfQuestions;	
	
	public QuestionPool() {
		qList = new LinkedList<Question>();
		initConfigurationSettings();     // init to default values.
	}
	
	/** 
	 * @param newQuestion - the new Question to be added to the pool.
	 * @return result - did the add succeed or fail ?
	 */
	public boolean addQuestion(Question newQuestion) {		
		boolean result = false;		
		result = qList.add(newQuestion);			
		return result;
	}

	/**
	 * 
	 * @param theQuestion - the Question to be deleted from the pool.
	 * @return result - did the add succeed or fail ?
	 */
	public boolean deleteQuestion(Question theQuestion) {
		boolean result = false;		
		result = qList.remove(theQuestion);		
		return result;
	}

	/**
	 * Get a question from the List based on its position (index).
	 * 
	 * @param index
	 * @return The Question record return null if a problem.
	 */
	public Question getQuestion(int index) {
		Question q = null;
		
		try {
			q = qList.get(index);
		} catch (IndexOutOfBoundsException obe) {
            q = null;    // Return null.			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return q;
	}
	
	public int getSize() {
		return qList.size();
	}
	

	/**
	 * Initialize the configuration values to various default settings. 
	 */
	
	private void initConfigurationSettings() {
		// Init config settings to arbitrary default values.
		descriptionText = new String ("This is a pool of questions.");
		isChangeAllowed = new Boolean(true); // Will user be allowed to change config?
		isExam = new Boolean(false);     // Default setting is to Quiz.
		timed = new Boolean (false);     // Default setting is no time limit.
		timeLimit = new Integer(0);
        displayFinalScore = new Boolean(true);   
        displayPrintOption = new Boolean(true);
        studentName = "Student Name";
        numberOfQuestions = new Integer(0);   // zero means 'All' questions.		
	}
	
	/**
	 * Remove all Questions from the List.
     */
	public void removeAllQuestions() {		
        qList.clear();
	}
	

	// Getters and Setters.
	public String getDescriptionText() {
		return descriptionText;
	}

	public void setDescriptionText(String descriptionText) {
		this.descriptionText = descriptionText;
	}

	public Boolean getIsChangeAllowed() {
		return isChangeAllowed;
	}

	public void setIsChangeAllowed(Boolean isChangeAllowed) {
		this.isChangeAllowed = isChangeAllowed;
	}
	
	public Boolean getIsExam() {
		return isExam;
	}

	public void setIsExam(Boolean isExam) {
		this.isExam = isExam;
	}

	public Boolean getTimed() {
		return timed;
	}

	public void setTimed(Boolean timed) {
		this.timed = timed;
	}

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}

	public Boolean getDisplayFinalScore() {
		return displayFinalScore;
	}

	public void setDisplayFinalScore(Boolean displayFinalScore) {
		this.displayFinalScore = displayFinalScore;
	}

	public Boolean getDisplayPrintOption() {
		return displayPrintOption;
	}

	public void setDisplayPrintOption(Boolean displayPrintOption) {
		this.displayPrintOption = displayPrintOption;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public Integer getNumberOfQuestions() {
		return numberOfQuestions;
	}

	public void setNumberOfQuestions(Integer numberOfQuestions) {
		this.numberOfQuestions = numberOfQuestions;
	}	

	/**
	 * Print all questions in the current list.
	 * May also optionally print a detailed analysis of 
	 * each questions DefaultStyleDocument - this may be extensive.
	 * 
	 * Useful debug method.
	 * 
	 * @param doAnalyze
	 */
	public void printAllQuestions(boolean doAnalyze) {
		for (Question q: qList) {
			System.out.println(q.toString());
			if (doAnalyze) {
			    q.analyzeDoc();
			}
		}
	}
}
