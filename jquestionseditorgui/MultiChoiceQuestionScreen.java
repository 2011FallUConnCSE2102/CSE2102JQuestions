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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultStyledDocument;

import question.PossibleAnswer;
import question.Question;
import question.QuestionType;
import utils.MultiLineTextInputDialog;


/**
 * This class holds all the details and methods for the screen 
 * to display MULTI_CHOICE type questions.
 * 
 * This includes main JPanels to display question text,
 * possible answers, labels, and JSpinners for setting the number
 * of possible answers.
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see EditorQuestionScreen
 * @see DragAndDropQuestionScreen
 * 
 */
@SuppressWarnings("serial")
public class MultiChoiceQuestionScreen extends EditorQuestionScreen {
	
	// Question panels
	private JTextPane mainTextPane;
	private JTextArea questionTextArea;

	// Main GUI components.
	private List<JLabel> labels = new ArrayList<JLabel>();
	private List<JCheckBox> answerBox = new ArrayList<JCheckBox>();
    private List<JScrollPane> scrollPanes = new ArrayList<JScrollPane>();
    private List<JPanel> choicePanels = new ArrayList<JPanel>();
	private List<JTextArea> answerText = new ArrayList<JTextArea>();
	private List<StringBuilder> explainPossibleAnswers = new ArrayList<StringBuilder>();
    private JScrollPane scrollPane, questionTextScrollPane;
    private JPanel optionsPanel;
	private JLabel howManyLabel;
	private JSpinner numPossAnswers;
	private SpinnerNumberModel spinModel;

	// The number of possible answers.	
	private Integer options = new Integer(4);

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

		// ======== scrollPane1 ========
        scrollPane = new JScrollPane();
		
		// ---- mainTextArea ----
		mainTextPane = new JTextPane();
		mainTextPane.setText("");

		scrollPane.setViewportView(mainTextPane);

		JPanel choicesPanel = new JPanel();
		choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));

		// x y w h wtx wty anchor fill T L B R padx pady
		add(scrollPane, new GridBagConstraints(0, 0, 10, 16, 1.0, 0.5,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
						10, 10, 10, 5), 0, 70));

		// ======== scrollPane2 ========
		questionTextScrollPane = new JScrollPane();
		
		// ---- questionText ----
		questionTextArea = new JTextArea();
		questionTextArea.setText("");
		questionTextArea.setBackground(Color.lightGray);
		questionTextArea.setLineWrap(true);
		questionTextArea.setWrapStyleWord(true);

		questionTextScrollPane.setViewportView(questionTextArea);
		add(questionTextScrollPane, new GridBagConstraints(0, 17, 10, 4, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
						10, 10, 10, 5), 0, 50));
		

		optionsPanel = new JPanel(new FlowLayout());
		howManyLabel = new JLabel("How many possible answers? (Max. " + MAX_NUMBER_OF_OPTIONS + ") ");
		spinModel = new SpinnerNumberModel(options.intValue(), 1, (int) MAX_NUMBER_OF_OPTIONS, 1);
		numPossAnswers = new JSpinner(spinModel);
		numPossAnswers.addChangeListener(new SpinnerListener(this));
		optionsPanel.add(howManyLabel); 		
		optionsPanel.add(numPossAnswers); 
		// x y w h wtx wty anchor fill T L B R padx pady
		add(optionsPanel, new GridBagConstraints(0, 21, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
				new Insets(10, 10, 10, 5), 0, 0));   

		
		JPanel mainChoicePanel = new JPanel(new GridBagLayout());

		// Main loop to create however many possible answers are needed.
		for (int i=0; i<MAX_NUMBER_OF_OPTIONS; i++) {
		    JPanel choicePanel = new JPanel(new GridBagLayout());
		    choicePanels.add(i, choicePanel);

			// Init Label field.
		    String label = generateLabel(i);
            JLabel newLabel = new JLabel(label);
            newLabel.setToolTipText("Possible answer " + label);
			newLabel.setHorizontalAlignment(SwingConstants.CENTER);
			newLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		    labels.add(i, newLabel);

			// ---- Init answerText field ----
		    JTextArea jta = new JTextArea();
		    jta.setToolTipText("Enter text for possible answer " + label);
		    answerText.add(i, jta);		    
			scrollPane = new JScrollPane();
			scrollPane.setBorder(null);
			jta.setText("");
			jta.setBackground(Color.LIGHT_GRAY);
			jta.setLineWrap(true);
			jta.setWrapStyleWord(true);
			jta.setBorder(null);
			scrollPane.setViewportView(jta);
			scrollPanes.add(i, scrollPane);

			JCheckBox newCheckBox = new JCheckBox();
			newCheckBox.setToolTipText("Set possible answer " + label + " to be correct or wrong");
		    answerBox.add(i, newCheckBox);

		    // ---- Explain text button ----
		    JButton explainButton = new JButton("Explain");
			explainPossibleAnswers.add(i, new StringBuilder(""));
		    explainButton.setToolTipText("Add some explanation text for this possible answer");
		    final int index = i;
			explainButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					explainButtonActionPerformed(e, index);
				}
			});
		    
		    		    
		    // x y w h wtx wty anchor fill T L B R padx pady
			choicePanel.add(newLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
							5, 5, 5), 0, 0));
			choicePanel.add(newCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0,
					0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
					new Insets(5, 5, 5, 5), 0, 0));
			choicePanel.add(scrollPane, new GridBagConstraints(2, 0, 6, 1, 1.0,
					1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
					new Insets(5, 5, 5, 5), 0, 0));
			choicePanel.add(explainButton, new GridBagConstraints(9, 0, 1, 1, 0.0,
					0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
					new Insets(5, 5, 5, 5), 0, 0));
			
			
			mainChoicePanel.add(choicePanel, new GridBagConstraints(0, i, 10, 1, 1.0, 1.0,
        			GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
					5, 5, 5), 0, 0));

			choicePanel.setVisible(false);
		}		

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(mainChoicePanel);

		add(scrollPane, new GridBagConstraints(0, 22, 10, 10, 1.0, 0.5,
		GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
				5, 5, 5), 0, 0));

		// Dynamically update layout.
		updateLayout();
	}


	/**
	 * Giver user ability to enter some explanation text for a particular 
	 * posssible answer (indexed by 'i').
	 * 
	 * @param e
	 * @param i - Index for a particular possible answer.
	 */
	private void explainButtonActionPerformed(ActionEvent e, int i) {
		MultiLineTextInputDialog multiDialog = new MultiLineTextInputDialog();
		StringBuilder sb = explainPossibleAnswers.get(i);
		
		System.out.println("i = " + i + " - " +  sb);
		
		String response = multiDialog.showInputDialog("Enter some explanation text", 
				sb.toString(), true);
		if (response == null) {   // User pressed 'Cancel'
			return;   // explanation text (if any) remains unchanged. 
		}
		else {
			explainPossibleAnswers.set(i, new StringBuilder(response));
		}			
	}

	/**
	 * Dynamically update layout depending on number of possible answers
	 * (options) defined for the question.
	 */
	public void updateLayout() {
		numPossAnswers.setValue(options);
		
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
	 * Once it has been edited....save the details from the screen to
	 * create a new Question record.
	 * 
	 * @param hintText - Hint text associated with the question.
	 * @param explainText - Explanatory text for the question.
	 * @param isMarked - Is the question marked or not.
	 * @return - new Question object.
	 */
	public Question saveQuestionRecord(String hintText, String explainText, Boolean isMarked) {
		Question newQuestion = null;
		
		//String mainStr = mainTextArea.getText();
		DefaultStyledDocument qDoc = (DefaultStyledDocument) mainTextPane.getDocument();
 		String questionStr = questionTextArea.getText();
 		
		List<PossibleAnswer> possibleAnswers = new LinkedList<PossibleAnswer>();
		for (int i=0; i < answerText.size() ; i++) {
			JTextArea jta = answerText.get(i);
			if ((jta.isVisible()) && (jta.getText().length() > 0)) {
			    possibleAnswers.add(new PossibleAnswer(jta.getText(), false, 0, explainPossibleAnswers.get(i)));
			}
		}		
		
		List<Integer> correctAnswers = new LinkedList<Integer>();
		for (int i=0; i < answerBox.size() ; i++) {
			JCheckBox jcb = answerBox.get(i);
			if ((jcb.isVisible()) &&(jcb.isSelected())) {
				correctAnswers.add(i);
			}
		}

				
		newQuestion = new Question(null, questionStr,
				possibleAnswers, 
				correctAnswers, QuestionType.MULTI_CHOICE, 
				qDoc, hintText, explainText, isMarked);

		return newQuestion;
	}

	/**
	 * Display the question record and all its possible answers.
	 * 
	 * @param question
	 */
	public void displayQuestionRecord(Question question) {			
		DefaultStyledDocument dsd = question.getQuestionDoc();
		mainTextPane.setDocument(dsd);
		String qText = question.getQuestionText();
		List<PossibleAnswer> pAnswers = question.getPossibleAnswers();
		
		questionTextArea.setText(qText);
		questionTextScrollPane.setVisible(true);
		
		int i = 0;
		for (; i<pAnswers.size(); i++) {
			answerText.get(i).setText(pAnswers.get(i).getTheAnswer());
			explainPossibleAnswers.set(i, pAnswers.get(i).getAnswerExplanation());
		}
		for (; i<MAX_NUMBER_OF_OPTIONS; i++) {
			answerText.get(i).setText("");
			explainPossibleAnswers.set(i, new StringBuilder(""));
		}
						
		
		List<Integer> cAnswers = question.getCorrectAnswers();
	
		for (JCheckBox jcb: answerBox) {
		    jcb.setSelected(false);	
		}

		for (Integer c: cAnswers) {		
    	    answerBox.get(c).setSelected(true);
		}

		setOptions(pAnswers.size());
	}
	
	@Override
	protected void clearDisplay() {
		mainTextPane.setText("");
		questionTextArea.setText("");

		for (int i=0; i<MAX_NUMBER_OF_OPTIONS; i++) {
			explainPossibleAnswers.set(i, new StringBuilder(""));
		}
		
		for (JTextArea jta: answerText) {
			jta.setText("");
		}
		
		for (JCheckBox jcb: answerBox) {
			jcb.setSelected(false);
		}
	}

	/**
	 * Perform error checking before saving the record.
	 * Ensure that all essential fields are filled
	 * and that fields contain the correct type of data
	 * - see code for details.
	 */
	@Override
	protected boolean errorChecking() {
		boolean retVal = true;


        // 1. Check main question panel contains something
		//    (i.e. either text or at least one component, such as an image.
		if ((mainTextPane.getComponentCount() <= 0) 
			&& (mainTextPane.getText().length() <= 0)) {
			JOptionPane.showMessageDialog(null,
				    "ERROR: Main question panel cannot be empty",
				    "Error saving Question",
				    JOptionPane.ERROR_MESSAGE);
			return false;    // don't bother checking any further
		}				
		
		// 2. Check that all possible answers have strings.
		for (int i=0; i<options; i++) {
			if (answerText.get(i).getText().length() <= 0) {
				JOptionPane.showMessageDialog(null,
					    "ERROR: Possible answer '" + labels.get(i).getText() + "' cannot be empty",
					    "Error saving Question",
					    JOptionPane.ERROR_MESSAGE);
				return false;    // don't bother checking any further
			}
		}
		
		// 3. Check that at least one correct answer has been set.
		boolean flag = false;
		for (int i=0; i<options; i++) {
			if (answerBox.get(i).isSelected()) {
				flag = true;
				break;
			}
		}
		if (flag == false) {
			JOptionPane.showMessageDialog(null,
				    "ERROR: Must tick at least one Checkbox to set a correct answer",
				    "Error saving Question",
				    JOptionPane.ERROR_MESSAGE);
			return false;    // don't bother checking any further			
		}
		
		return retVal;
	}	


	/***
	 * Various get and set methods defined.
	 */
	
	public JTextPane getMainTextArea() {
		return mainTextPane;
	}

	public void setMainTextArea(JTextPane mainTextArea) {
		this.mainTextPane = mainTextArea;
	}

	public JTextArea getQuestionText() {
		return questionTextArea;
	}

	public void setQuestionText(JTextArea questionText) {
		this.questionTextArea = questionText;
	}

	
	public List<StringBuilder> getAllAnswerExplanations() {
		return explainPossibleAnswers;
	}
	
	
	public List<String> getAllAnswerTexts() {
		List<String>strs = new ArrayList<String>();
		for (int i=0; i<options; i++) {
			JTextArea jtf = answerText.get(i);
			if (jtf.isVisible()) {
	            strs.add(jtf.getText());				
			}
		}					
		return strs;
	}

	public List<Integer> getAllCorrectAnswers() {
		List<Integer>correctAnswers = new ArrayList<Integer>();
		for (int i=0; i<options; i++) {
			JCheckBox jcb = answerBox.get(i);
			if (jcb.isVisible() && (jcb.isSelected()) ) {
	            correctAnswers.add(i);				
			}
		}		
		return correctAnswers;
	}

	
	public void insertIcon(Icon icon) {
		mainTextPane.insertIcon(icon);
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



