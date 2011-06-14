package question;
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





/**
 * Every Question will have one or more possible answers.
 * For a multiple-choice type question the possible answers have 
 * check-boxes and are located underneath the main text.
 * For a drag-n-drop type question they are located down 
 * the right-hand-side of the main text.
 * 
 * Encapsulation of possible answers into PossibleAnswer objects 
 * make shuffling much easier.
 * 
 * Retaining order is necessary for drag-n-drop type questions 
 * to maintain association with the position of the correct answer.
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see Question
 * 
 */


public class PossibleAnswer  {
	private String theAnswer;
	private boolean correctness;
	private StringBuilder answerExplanation;
    private int order;

    /**
     * PossibleAnswer constructor.
     *   
     * Different constructors are called from different points
     * in the program, depending on context.   
     */

    public PossibleAnswer() {
        theAnswer = "";
        correctness = false;
		answerExplanation = new StringBuilder("");
		order = 0;        
    }

    /**
     * PossibleAnswer constructor.
     *   
     * @param str
     *            the text of the possible answer.
     * @param b
     *            is the possible answer correct (true) or not ?
     *            
     * @param i
     *            the position of the possible answer within the list
     *            of possible answers (only needed for d-n-d-  type questions). 
     * @param sb
     *            any explanation text associated with the possible answer.           
     */
       
	public PossibleAnswer(String str, boolean b, int i, StringBuilder sb) {
		theAnswer = str;
		correctness = b;
		order = i;
		answerExplanation = sb;
	}

	public PossibleAnswer(String str, boolean b) {
		theAnswer = str;
		correctness = b;
		answerExplanation = new StringBuilder("");
		order = 0;
	}
	
	public String getTheAnswer() {
		return theAnswer;
	}

	public void setCorrectness(boolean b) {
		correctness = b;
	}

	public boolean isCorrect() {
		return correctness;
	}

	public StringBuilder getAnswerExplanation() {
		return answerExplanation;
	}

	public void setAnswerExplanation(StringBuilder sb) {
		answerExplanation = sb;
	}

	public void setOrder(int i) {
		order = i;
	}
	
	public int getOrder() {
		return order;
	}

	
    /**
     * Test for equality of two PossibleAnswer objects.
     * Equality is true if BOTH theAnswerText and answerExplanation match,
     * return false otherwise.
     *   
     * @param pa
     *            Test if this PossibleAnswer object is equal to 
     *            the paramater 'pa'.              
     */            
	
	
	public boolean equals(PossibleAnswer pa) {
		boolean retVal = false;

		if ((theAnswer.compareTo(pa.getTheAnswer()) == 0)
			&& (answerExplanation.toString().compareTo(pa.getAnswerExplanation().toString()) == 0)) {
			retVal = true;
		}
		return retVal;
	}
		
	public String toString() {
		return theAnswer + "  " + correctness + " " + order;
	}
}

