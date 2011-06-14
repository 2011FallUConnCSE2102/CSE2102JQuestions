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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultStyledDocument;

import question.PossibleAnswer;
import question.Question;


/**
 * This class holds all the details and methods for the screen 
 * to display MULTI_CHOICE type questions.
 *
 * This is a simplified version of the MultiChoiceQuestionScreen 
 * defined in them JQuestionsEditor. No editing functions are 
 * defined. 
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see QuestionScreen
 * @see DragAndDropQuestionScreen
 * 
 */
@SuppressWarnings("serial")
public class MultiChoiceQuestionScreen extends QuestionScreen {

	// Question panels.
	private JTextPane mainTextPane;
	private JTextArea questionTextArea;
	
	// Main GUI components.
	private List<JTextArea> answerText = new ArrayList<JTextArea>();
	private List<StringBuilder> explainPossibleAnswers = new ArrayList<StringBuilder>();
    private List<JPanel> choicePanels = new ArrayList<JPanel>();
	private JScrollPane scrollPane, questionTextScrollPane;
    private List<JLabel> labels = new ArrayList<JLabel>();
	private List<JCheckBox> answerBox = new ArrayList<JCheckBox>();
    private List<JScrollPane> scrollPanes = new ArrayList<JScrollPane>();
	private int options;
    
    final private Integer MAX_NUMBER_OF_OPTIONS = 50;   // A to Z and beyond.   


    /**
     * Constructor simply takes references to other panels and calls main
     * component create method.
     * 
     * @param headerPanel
     * @param buttonPanel
     */        
	MultiChoiceQuestionScreen(JPanel headerPanel, JPanel buttonPanel) {
		super(headerPanel, buttonPanel);
		createMultiChoicePanel();       
	}

	/**
	 * Create main panel components.
	 * The user interface 'view' of the Panel for a MULTI_CHOICE question.
	 */	
	public void createMultiChoicePanel() {

		// ======== multiChoicePane ========
		setLayout(new GridBagLayout());

		// ======== scrollPane ========
        scrollPane = new JScrollPane();
        
		// ---- mainTextArea ----
		mainTextPane = new JTextPane();
		mainTextPane.setText("");
		scrollPane.setViewportView(mainTextPane);

		JPanel choicesBoxPanel = new JPanel();
		choicesBoxPanel.setLayout(new BoxLayout(choicesBoxPanel, BoxLayout.Y_AXIS));

		add(scrollPane, new GridBagConstraints(0, 0, 10, 16,
				1.0, 0.5, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 5), 0, 70));
		
		// ======== scrollPane2 ========
		questionTextScrollPane = new JScrollPane();
	
		// ---- questionText ----
        questionTextArea = new JTextArea();
		questionTextArea.setText("");
		questionTextArea.setBackground(Color.lightGray);
		questionTextArea.setLineWrap(true);
		questionTextScrollPane.setViewportView(questionTextArea);
		add(questionTextScrollPane, new GridBagConstraints(0, 17, 10, 4, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
						10, 10, 10, 5), 0, 50));
				
		JPanel mainChoicePanel = new JPanel(new GridBagLayout());
		
		// Main loop to create however many possible answers are needed.
		for (int i=0; i<MAX_NUMBER_OF_OPTIONS; i++) {
		    JPanel choicePanel = new JPanel(new GridBagLayout());
		    choicePanels.add(i, choicePanel);

			// Init Label field.
            JLabel newLabel = new JLabel(generateLabel(i));
			newLabel.setHorizontalAlignment(SwingConstants.CENTER);
			newLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		    labels.add(i, newLabel);

			// ---- Init answerText field ----
		    JTextArea jta = new JTextArea();
		    answerText.add(i, jta);		    
			scrollPane = new JScrollPane();
			scrollPane.setBorder(null);
			jta.setText("");
			jta.setBackground(Color.LIGHT_GRAY);
			jta.setLineWrap(true);
			jta.setBorder(null);
			scrollPane.setViewportView(jta);
			scrollPanes.add(i, scrollPane);

		    answerBox.add(i, new JCheckBox());

			explainPossibleAnswers.add(i, new StringBuilder());

		    		    
		    // x y w h wtx wty anchor fill T L B R padx pady
			choicePanel.add(newLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
							5, 5, 5), 0, 0));
			choicePanel.add(answerBox.get(i), new GridBagConstraints(1, 0, 1, 1, 0.0,
					0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
					new Insets(5, 5, 5, 5), 0, 0));
			choicePanel.add(scrollPane, new GridBagConstraints(2, 0, 10, 1, 1.0,
					1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
					new Insets(5, 5, 5, 5), 0, 0));

			mainChoicePanel.add(choicePanel, new GridBagConstraints(0, i, 10, 1, 1.0, 1.0,
        			GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
					5, 5, 5), 0, 0));

			choicePanel.setVisible(false);
		}		

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(mainChoicePanel);

		add(scrollPane, new GridBagConstraints(0, 21, 10, 10, 1.0, 0.5,
		GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
				5, 5, 5), 0, 0));
		
		// Dynamically update layout.
		updateLayout();
	}

	/**
	 * Dynamically update layout depending on number of possible answers
	 * (options) defined for the question.
	 */	
	public void updateLayout() {

		int i=0;
		while (i<options) {
			choicePanels.get(i++).setVisible(true);
		}
		while (i<MAX_NUMBER_OF_OPTIONS) {
			choicePanels.get(i++).setVisible(false);
		}
	}
			
	public void setFieldsEditable(boolean b) {
		mainTextPane.setEditable(b);
		questionTextArea.setEditable(b);
		for (JTextArea jta: answerText) {
			jta.setEditable(b);		
		}
	}
	
	/**
	 * Display the question record and all its possible answers.
	 * 
	 * @param question
	 */	
	public void displayQuestionRecord(Question question, UserAnswer currentAnswer) {			
		DefaultStyledDocument dsd = question.getQuestionDoc();
		mainTextPane.setDocument(dsd);
		String qText = question.getQuestionText();
		List<PossibleAnswer> pAnswers = question.getPossibleAnswers();
		
		questionTextArea.setText(qText);
		questionTextScrollPane.setVisible(true);
		
		setOptions(pAnswers.size());
		
		for (int i=0; i<options; i++) {
			answerBox.get(i).setSelected(currentAnswer.getAnswerBox(i));			
			answerText.get(i).setText(pAnswers.get(i).getTheAnswer());
			explainPossibleAnswers.set(i, pAnswers.get(i).getAnswerExplanation());
		}
	}

	
	public List<StringBuilder> getAllAnswerExplanations() {
		return explainPossibleAnswers;
	}
	
	
	public JCheckBox getAnswerBox(int i) {
		return answerBox.get(i);
	}
	
	/**
	 * When the user presses 'next' button the question screen 
	 * is evaluated to see if the correct answer(s) have been given.
	 *
	 * @param question - The current question
	 * @param answer - The current user answer
	 * @return - A boolean value indicating if the current user
	 *           answer is correct for the current question.
	 */
	protected boolean isCorrect(Question question, UserAnswer answer) {		
		boolean selected;
		boolean contains;
		List<Integer> correctAnswers = question.getCorrectAnswers();

		
		for (int i=0; i<options; i++) {
			selected = answerBox.get(i).isSelected();
			contains = correctAnswers.contains(i+1);
	        if ((selected == true) && (contains == false)) return false;
	        else if ((selected == false) && (contains == true)) return false;			
		}
		
        return true;
	}

	protected void clearDisplay() {
		mainTextPane.setText("");
		questionTextArea.setText("");

		for (JTextArea jta: answerText) {
			jta.setText("");
		}
		
		for (JCheckBox jcb: answerBox) {
			jcb.setSelected(false);
		}
	}

	/**
	 * Generate a label for a given integer 'i'.
	 * 
	 * This method is usually called to generate labels 
	 * for possible answers. 
	 * 
	 * Integers 1 to 26 generate labels 'A' to 'Z'.
	 * Integers 27 to 52 generate labels 'A1' to 'Z1'
	 * Integers 53 to 78 generate labels 'A2' to 'Z2'
	 *                      .... and so on...	  
	 * @param i - Integer
	 * @return - The String for the Label.
	 */
	public static String generateLabel(int i) {
		String sb;
		if (i<26) 
			sb = "" + (char)(i+65);
		else 
            sb = "" + ((char)(i%26+65)) + (i/26);
		
		return sb;
	}

	/**
	 * Set the number of possible answers
	 * and update layout dynamically.
	 */		
	public void setOptions(Integer options) {	
		this.options = options;
		
		updateLayout();
	}

	public Integer getOptions() {
		return options;
	}	
}
