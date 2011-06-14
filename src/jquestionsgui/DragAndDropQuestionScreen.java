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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
//import javax.swing.DebugGraphics;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;

import question.PossibleAnswer;
import question.Question;

/**
 * This class holds all the details and methods for the screen 
 * to display DRAG_N_DROP type questions.
 *
 * This is a simplified version of the DragAndDropQuestionScreen 
 * defined in them JQuestionsEditor. No editing functions are 
 * defined. 
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see QuestionScreen
 * @see MultiChoiceQuestionScreen
 * 
 */
@SuppressWarnings("serial")
public class DragAndDropQuestionScreen extends QuestionScreen {

	private JTextPane mainTextPane;
    private JTextArea questionTextArea;
	private ArrayList<JTextField> optionsFields = new ArrayList<JTextField>();
    private List<JPanel> choicePanels = new ArrayList<JPanel>();
    private JScrollPane scrollPane, optionsScrollPane;
	private int options;
    
    final private Integer MAX_NUMBER_OF_OPTIONS = 50;   // A to Z and beyond.   

	/**
	 * Construct the DRAG_N_DROP panel between the headerPanel 
	 * (filename, question X of Y) and the buttonPanel (next, 
	 * previous, new, delete, etc).
	 * @param headerPanel
	 * @param buttonPanel
	 */		
	DragAndDropQuestionScreen(JPanel headerPanel, JPanel buttonPanel) {
		super(headerPanel, buttonPanel);
		createDnDPanel();	
    }

	/**
	 * Construct the user interface 'view' of the Panel.
	 */	
	public void createDnDPanel() {

		// ======== dndPane ========
		setLayout(new GridBagLayout());
        setVisible(false);

		// ======== scrollPane ======== 
	    mainTextPane = new JTextPane();
		mainTextPane.setText("");
        //mainTextArea.setDebugGraphicsOptions(DebugGraphics.LOG_OPTION);
    
		mainTextPane.setFont(new Font("Courier New", Font.PLAIN, 11));
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(mainTextPane);

		// x y w h wtx wty anchor fill T L B R padx pady
		add(scrollPane, new GridBagConstraints(0, 0, 8, 5, 0.5, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
						10, 10, 10, 5), 300, 0));

		// ======== scrollPane ========
		// ---- dndQuestionText ----
	    questionTextArea = new JTextArea();
		questionTextArea.setText("");
		questionTextArea.setBackground(Color.lightGray);
		questionTextArea.setLineWrap(true);
		questionTextArea.setWrapStyleWord(true);
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(questionTextArea);

		// Need to disable dropping onto this JTextArea
		DropTarget d = new DropTarget();
		d.setActive(false);
		questionTextArea.setDropTarget(d);
		
		
		add(scrollPane, new GridBagConstraints(0, 5, 8, 5, 0.5, 0.3,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
						10, 10, 10, 5), 0, 70));

		JPanel optionsPanel = new JPanel(); 

	
        final Box vBox = Box.createVerticalBox();
		optionsPanel.add(vBox, new GridBagConstraints(0, 0, 2, 10, 1.0,
				0.5, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 5), 0, 0));
		vBox.add(Box.createVerticalGlue());
	
		
		
		for (int i=0; i<MAX_NUMBER_OF_OPTIONS; i++) {
		    JPanel choicePanel = new JPanel();  
		    choicePanels.add(i, choicePanel);
		
			// ---- optionField ----
			final JTextField jtf = new JTextField();
		    optionsFields.add(i, jtf);
			jtf.setText("");
			// Add helpful tool-tip
			jtf.setToolTipText("<HTML>Double-click to highlight text <br>and drag and drop text into boxes.</HTML>");
			jtf.setHorizontalAlignment(SwingConstants.CENTER);
			jtf.setFont(new Font("Courier New", Font.PLAIN, 11));
			jtf.setMaximumSize(new Dimension(100, 2147483647));
			jtf.setDragEnabled(true);
			jtf.setBorder(LineBorder.createBlackLineBorder());
			jtf.setBackground(Color.cyan);
			jtf.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					codeFieldActionPerformed(e);
				}
			});
			jtf.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					codeFieldMouseClicked(e);
				}

				@Override
				public void mousePressed(MouseEvent e) {
					codeFieldMousePressed(e, jtf);
				}
			});

			// Need to set sizes in a Box Layout.
			//                                  w   h
			jtf.setPreferredSize(new Dimension(150, 25));
			jtf.setMinimumSize(getPreferredSize());
			jtf.setMaximumSize(getPreferredSize());


			choicePanel.add(jtf);			
			
			vBox.add(choicePanel);
			vBox.add(Box.createVerticalGlue());
						
			choicePanel.setVisible(false);
		}

		optionsScrollPane = new JScrollPane();
		optionsScrollPane.setViewportView(optionsPanel);
		add(optionsScrollPane, new GridBagConstraints(8, 0, 2, 10, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
						10, 10, 10, 5), 0, 0));

		updateLayout();
		
	}	

	/**
	 * Update the layout for each question.
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
	
	private void codeFieldActionPerformed(ActionEvent e) {}
	private void codeFieldMouseClicked(MouseEvent e) {}
	private void codeFieldMousePressed(MouseEvent e, JTextField jtf) {
		String str = jtf.getText();
		jtf.setSelectionStart(0);
		jtf.setSelectionEnd(str.length());
	}

	
	public void setFieldsEditable(boolean b) {
		mainTextPane.setEditable(b);
		questionTextArea.setEditable(b);
		for (JTextField jtf: optionsFields) {
			jtf.setEditable(b);
		}
	}
	
	/**
	 * Display the question record and all its possible answers.
	 * Remember, the user may come back to a question and change their 
	 * answer(s), so for each question we have to save the state of the users'
	 * answer. 
	 * 
	 * @param question - The current question
	 * @param currentAnswer - The current user answer to the current question.
	 */
	
	public void displayQuestionRecord(Question question, UserAnswer currentAnswer) {
		List<PossibleAnswer> pAnswers = question.getPossibleAnswers();
		
		setOptions(pAnswers.size());
		
		for (int i=0; i < options; i++) {
			optionsFields.get(i).setText(pAnswers.get(i).getTheAnswer());
		}
		
		DefaultStyledDocument dsd = (DefaultStyledDocument) question.getQuestionDoc();
		mainTextPane.setDocument(dsd);
		questionTextArea.setText(question.getQuestionText());
	}
		
	/**
	 * When the user presses 'next' button the question screen is 
	 * evaluated to see if the correct answer(s) have been given.
	 *
	 * @param question - The current question
	 * @param answer - The current user answer
	 * @return - A boolean value indicating if the current user
	 *           answer is correct for the current question.
	 */
	protected boolean isCorrect(Question question, UserAnswer answer) {		
		
		DefaultStyledDocument doc = question.getQuestionDoc();
		if (doc == null) {
			//System.out.println("questionDoc is null!");
			return true;
		}

		List<PossibleAnswer> possibleAnswers = question.getPossibleAnswers();
		List<Integer> correctAnswers = question.getCorrectAnswers();
		int fieldCount = 1;
		
		// Check value in each JTextField in panel.
		for (int i1 = 0; i1 < doc.getLength(); i1++) {        				        				
			// Identify each JTextField
			Component comp = StyleConstants.getComponent(doc
					.getCharacterElement(i1).getAttributes());
			if (comp != null && (comp instanceof JTextField)) {
				JTextField jtf = (JTextField) comp;
		        String answerStr = jtf.getText();
		        int correctAnswerIndex = correctAnswers.get(fieldCount-1);
		        String correctStr = possibleAnswers.get(correctAnswerIndex-1).getTheAnswer();

		        // Compare answer string to correct answer.
		        if (answerStr.compareTo(correctStr) != 0) {
		        	return false;
		        }
		        
		        fieldCount += 1;			        
			}
		}

		return true;
	}

	/**
	 * Clear the current screen.
	 * Make sure all text boxes are set to empty, etc.
	 */
	protected void clearDisplay() {
		mainTextPane.setText("");
		questionTextArea.setText("");

		for (JTextField jtf: optionsFields) {
			jtf.setText("");
		}		
	}

	/**
	 * Set the options value based on the number of possible 
	 * answers for the question. Update the display immediately.
	 */
	public void setOptions(Integer options) {	
		this.options = options;
		
		updateLayout();
	}

	/**
	 * Get the number of possible answers for this question.
	 * @return
	 */
	public Integer getOptions() {
		return options;
	}	
}
