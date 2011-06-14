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
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;

import question.PossibleAnswer;
import question.Question;
import question.QuestionType;


/**
 * This class holds all the details and methods for the screen 
 * to display DRAG_N_DROP type questions.
 * 
 * This includes main JPanels to display question text,
 * possible answers, and JSpinners for setting the number
 * of possible answers.
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see EditorQuestionScreen
 * @see MultiChoiceQuestionScreen
 * 
 */
@SuppressWarnings("serial")
public class DragAndDropQuestionScreen extends EditorQuestionScreen {

	private JTextPane mainTextPane;
	private JTextArea questionTextArea;
	private ArrayList<JTextField> optionsFields = new ArrayList<JTextField>();
	private List<JPanel> choicePanels = new ArrayList<JPanel>();
	private JScrollPane scrollPane, optionsScrollPane;
	private JLabel howManyLabel, maxLabel;
	private JSpinner numPossAnswers;
	private SpinnerNumberModel spinModel;
	private Integer options = new Integer(6); // Set default number of options

	// This number is actually quite arbitrary.
	final private Integer MAX_NUMBER_OF_OPTIONS = 50;

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

		// ======== dndPanel ========
		setLayout(new GridBagLayout());

		// ======== mainTextPane ========
		scrollPane = new JScrollPane();
		mainTextPane = new JTextPane();
		mainTextPane.setText("");
		mainTextPane.setFont(new Font("Courier New", Font.PLAIN, 11));
		scrollPane.setViewportView(mainTextPane);

		new DropTarget(mainTextPane, new TextDropTargetListener(mainTextPane));

		// x y w h wtx wty anchor fill T L B R padx pady
		add(scrollPane, new GridBagConstraints(0, 0, 8, 5, 0.5, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
						10, 10, 10, 5), 300, 0));

		// ---- questionTextArea ----
		scrollPane = new JScrollPane();
		questionTextArea = new JTextArea();
		questionTextArea.setText("");
		questionTextArea.setBackground(Color.lightGray);
		questionTextArea.setLineWrap(true);
		questionTextArea.setWrapStyleWord(true);

		// Need to disable dropping onto this JTextArea
		DropTarget d = new DropTarget();
		d.setActive(false);
		questionTextArea.setDropTarget(d);

		scrollPane.setViewportView(questionTextArea);
		add(scrollPane, new GridBagConstraints(0, 5, 8, 5, 0.5, 0.3,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
						10, 10, 10, 5), 0, 70));

		// ---- optionsFields ----
		JPanel optionsPanel = new JPanel();

		JPanel howManyPanel = new JPanel();
		howManyLabel = new JLabel("How many possible answers?");
		howManyLabel.setHorizontalAlignment(SwingConstants.CENTER);
		howManyPanel.add(howManyLabel);
		maxLabel = new JLabel("(Max. " + MAX_NUMBER_OF_OPTIONS + ") ");
		spinModel = new SpinnerNumberModel(options.intValue(), 1,
				(int) MAX_NUMBER_OF_OPTIONS, 1);
		numPossAnswers = new JSpinner(spinModel);
		numPossAnswers.addChangeListener(new SpinnerListener(this));

		final Box vBox = Box.createVerticalBox();
		optionsPanel.add(vBox, new GridBagConstraints(0, 2, 2, 10, 1.0, 0.5,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						10, 10, 10, 5), 0, 0));

		vBox.add(howManyPanel);
		JPanel tempPanel = new JPanel();
		tempPanel.add(maxLabel);
		tempPanel.add(numPossAnswers);
		vBox.add(tempPanel);
		vBox.add(Box.createVerticalGlue());

		for (int i = 0; i < MAX_NUMBER_OF_OPTIONS; i++) {
			JPanel choicePanel = new JPanel();
			choicePanels.add(i, choicePanel);

			// ---- optionField ----
			final JTextField jtf = new JTextField();
			optionsFields.add(i, jtf);
			jtf.setText("");
			jtf.setHorizontalAlignment(SwingConstants.CENTER);
			jtf.setFont(new Font("Courier New", Font.PLAIN, 11));
			jtf.setDragEnabled(true);
			jtf.setBorder(LineBorder.createBlackLineBorder());
			jtf.setBackground(Color.cyan);
			jtf.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					codeFieldActionPerformed(e);
				}
			});

			// Need to set sizes in a Box Layout.
			// w h
			jtf.setPreferredSize(new Dimension(150, 25));
			jtf.setMinimumSize(getPreferredSize());
			jtf.setMaximumSize(getPreferredSize());

			jtf.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					optionsFieldMouseClicked(e);
				}

				@Override
				public void mousePressed(MouseEvent e) {
					optionsFieldMousePressed(e, jtf);
				}
			});

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
	 * Dynamically resize the layout as the number of possible
	 * answers is changed.
	 */	
	public void updateLayout() {
		numPossAnswers.setValue(options);

		int i = 0;
		while (i < options) {
			choicePanels.get(i++).setVisible(true);
		}
		while (i < MAX_NUMBER_OF_OPTIONS) {
			choicePanels.get(i++).setVisible(false);
		}
	}

	public void setFieldsEditable(boolean b) {
		mainTextPane.setEditable(b);
		for (JTextField jtf : optionsFields) {
			jtf.setEditable(b);
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
	public Question saveQuestionRecord(String hintText, String explainText,
			Boolean isMarked) {
		DefaultStyledDocument qDoc = (DefaultStyledDocument) mainTextPane
				.getDocument();
		String questionStr = questionTextArea.getText();

		List<PossibleAnswer> possibleAnswers = new LinkedList<PossibleAnswer>();
		for (int i=0; i < optionsFields.size() ; i++) {
			JTextField jtf = optionsFields.get(i);
			if ((jtf.isVisible()) && (jtf.getText().length() > 0)) {
			    possibleAnswers.add(new PossibleAnswer(jtf.getText(), false));
			}
		}		
		
		
		// Save number of options
		options = possibleAnswers.size();

		// Ensure the answer fields (however many there may be),
		// are named and numbered 'answerFieldX', where 'X'
		// is an integer from 0 onwards.
		List<Integer> correctAnswers = new LinkedList<Integer>();
		setCorrectAnswersForDnD(mainTextPane, possibleAnswers, correctAnswers);

		Question newQuestion = new Question(null, questionStr, possibleAnswers,
				correctAnswers, QuestionType.DRAG_N_DROP, qDoc, hintText,
				explainText, isMarked);

		return newQuestion;
	}

	/**
	 * Once you've defined the question you need to specify the correct
	 * answers, and save them.
	 * 
	 * @param txtPane - The main question text panel.
	 * @param possibleAnswers - A list of the possible answers.
	 * @param correctAnswers - The indices of which possible answers 
	 *                         are actually correct.
	 */
	
	public void setCorrectAnswersForDnD(JTextPane txtPane,
			List<PossibleAnswer> possibleAnswers, List<Integer> correctAnswers) {
		DefaultStyledDocument doc = (DefaultStyledDocument) txtPane
				.getDocument();

		if (doc == null) { // Yes, this shouldn't happen.
			System.out.println("doc is null");
			return;
		}

		for (int i = 0; i < doc.getLength(); i++) {
			Component comp = StyleConstants.getComponent(doc
					.getCharacterElement(i).getAttributes());
			if (comp != null && (comp instanceof JTextField)) {
				// System.out.println(">>>>> JTextField at location " + i);
				JTextField jtf = (JTextField) comp;
				String txtStr = jtf.getText();

				int findField = 0;
				for (int x=0; x<possibleAnswers.size(); x++) {
					if (txtStr.compareTo(possibleAnswers.get(x).getTheAnswer()) == 0) {
						findField = x;
						break;
					}
				}				
				
				if (findField >= 0) {
					correctAnswers.add(findField + 1);
				}
			}
		}
	}

	/**
	 * Display the question record and all its possible answers.
	 * 
	 * @param question
	 */
	public void displayQuestionRecord(Question question) {
		List<PossibleAnswer> pAnswers = question.getPossibleAnswers();

		int i = 0;
		for (; i < pAnswers.size(); i++) {
			optionsFields.get(i).setText(pAnswers.get(i).getTheAnswer());
		}
		for (; i < MAX_NUMBER_OF_OPTIONS; i++) {
			optionsFields.get(i).setText("");
		}

		DefaultStyledDocument dsd = question.getQuestionDoc();

		// Useful debug
		// question.analyzeDoc();

		mainTextPane.setDocument(dsd);
		questionTextArea.setText(question.getQuestionText());
		setOptions(pAnswers.size());
	}

	@Override
	protected void clearDisplay() {
		mainTextPane.setText("");
		questionTextArea.setText("");

		for (JTextField jtf : optionsFields) {
			jtf.setText("");
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
			if (optionsFields.get(i).getText().length() <= 0) {
				JOptionPane.showMessageDialog(null,
					    "ERROR: Possible answer fields cannot be empty",
					    "Error saving Question",
					    JOptionPane.ERROR_MESSAGE);
				optionsFields.get(i).requestFocusInWindow();
				return false;    // don't bother checking any further
			}
		}
		
		// 3. Check that all JTextFields in mainTextPane appear as
		//    possible answers.
		DefaultStyledDocument doc = (DefaultStyledDocument) mainTextPane.getDocument();
		for (int i = 0; i < doc.getLength(); i++) {
			String questionString = null;
			// Identify any JTextFields
			Component component = StyleConstants.getComponent(doc
					.getCharacterElement(i).getAttributes());
			if (component != null && (component instanceof JTextField)) {
				JTextField jtf = (JTextField) component;
				questionString = jtf.getText();
				
				// 3a. Check to ensure that the text string is not empty.
				if (questionString.compareTo("") == 0) {
					JOptionPane.showMessageDialog(null,
						    "ERROR: Question fields cannot be empty",
						    "Error saving Question",
						    JOptionPane.ERROR_MESSAGE);
					jtf.requestFocusInWindow();
					return false;
				}
				// 3b. Check to ensure that the text string appears as a possible answer.
				boolean flag = false;
				for (int i1=0; i1<options; i1++) {
					String possibleAnswer = optionsFields.get(i1).getText();
					if (questionString.compareTo(possibleAnswer) == 0) {
						flag = true;    // found a match
						break;
					}
				}
                if (flag == false) {  // no match was found
				    JOptionPane.showMessageDialog(null,
					    "ERROR: Question field does not appear as a Possible Answer.",
					    "Error saving Question",
					    JOptionPane.ERROR_MESSAGE);
				    jtf.requestFocusInWindow();
				    return false;    // don't bother checking any further
                }
			}
		}
		
		return retVal;
	}


	/**
	 * Get all possible answers as a List of String objects.
	 *
	 * @return
	 */
	public List<String> getAllAnswerTexts() {
		List<String> strs = new ArrayList<String>();

		for (int i = 0; i < options; i++) {
			JTextField jtf = optionsFields.get(i);
			if (jtf.isVisible()) {
				strs.add(jtf.getText());
			}
		}
		return strs;
	}

	private void codeFieldActionPerformed(ActionEvent e) {
	}

	private void optionsFieldMouseClicked(MouseEvent e) {
	}

	private void optionsFieldMousePressed(MouseEvent e, JTextField jtf) {
		String str = jtf.getText();
		jtf.setSelectionStart(0);
		jtf.setSelectionEnd(str.length());
	}

	public JTextPane getMainTextArea() {
		return mainTextPane;
	}

	public JTextArea getQuestionTextArea() {
		return questionTextArea;
	}

	/**
	 * Set the number of options and immediately update 
	 * the interface layout accordingly.
	 */
	public void setOptions(Integer options) {
		this.options = options;

		updateLayout();
	}

	/**
	 * Return the number of possible answers (options) for
	 * the current question.
	 * 
	 * @return - Options.
	 */
	public Integer getOptions() {
		return options;
	}

	// Insert an image icon.
	public void insertIcon(Icon icon) {
		mainTextPane.insertIcon(icon);
	}
}
