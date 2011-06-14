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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;

import question.Question;
import question.QuestionPool;
import question.QuestionType;

import jquestions.JQuestions;
import jquestionsgui.AboutDialog;
import utils.JQSFileFilter;
import utils.MultiLineTextInputDialog;


/**
 * This class defines the main user interface components and methods 
 * for the JQuestions application. This is an 'exam simulator' type
 * program which reads files created and edited using the JQuestions 
 * Editor program. 
 * 
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see JQuestions
 * @see DragAndDropQuestionScreen
 * @see MultiChoiceQuestionScreen
 * 
 */
@SuppressWarnings("serial")
public class JQuestionsGUI extends JPanel {

	// File name of quiz for when running in 'demo' mode.
	private static final String DEMO_FILE_NAME = "demo-1.jqs"; 

	// A reference to the main application.
	private final JQuestions jQuestions;
	
	// Configuration Settings variables for this Question pool.
	private Boolean isChangeAllowed;
	private Boolean isExam;
	private Boolean isTimed;
	private Integer timeLimit;   
	private Boolean displayFinalScore;
	private Boolean displayPrintOption;
	private String studentName;
	private Integer nQuestions;	
	private Date startTime; 	

	// Various state variables.
	private JFileChooser fc = null;
	private int currentRecordIndex = -1;
	private Question currentlyDisplayedQuestion;
	private String hintText;
	private String explainText;
    private ArrayList<UserAnswer> userAnswers;
    private ArrayList<Integer> questionIds; 
    private int qIdsIndex;
    public boolean isApplet = false;
    private boolean isDemo = false;
    private boolean pressedOK = false;
    

    // Main UI compponents.	
	private MultiChoiceQuestionScreen multiChoicePanel;
	private DragAndDropQuestionScreen dndPanel;	
	private JPanel blankPanel, startPanel, northPanel, southPanel;
	private CardLayout outerCardLayout;
	private JPanel outerCardPanel;
	private CardLayout innerCardLayout;
	private JPanel innerCardPanel;
	private JPanel contentPanel;
	private JMenuBar menuBar1;
	private JMenu menu1,  menu2;
	private JMenuItem menuFileOpen, menuExit, menuAbout;
	private JLabel countLabel, correctnessLabel, fileNameLabel;
	private ClockField clockField;
	private JButton nextButton, previousButton, hintButton, explainButton;
    private ConfigurationWindow configWindow;
	private ReportSummaryWindow reportWindow;

	/**
	 * Constructor for the GUI. The code for the GUI and the main
	 * application is kept separated into two different classes
	 * This means you can change the UI without making changes 
	 * to the underlying application.
	 * 
	 * If the application is running as an applet then the UI 
	 * sometimes needs to be aware of this.
	 * 
	 * @param jq - Reference to main JQuestions application.
	 * @param isApplet - Is running as an applet ?
	 * @param isDemo - Is running in 'Demo' mode ?
	 */	
	public JQuestionsGUI(JQuestions jq, boolean isApplet, boolean isDemo) {
		jQuestions = jq;	
		this.isApplet = isApplet;
		this.isDemo = isDemo;
		configWindow = new ConfigurationWindow(this);

		isExam = jQuestions.getQuestionPool().getIsExam();
		isChangeAllowed = jQuestions.getQuestionPool().getIsChangeAllowed();

		if (!isApplet) {
		   // Create a file chooser
		   fc = new JFileChooser();
		   fc.addChoosableFileFilter(new JQSFileFilter());
		   fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }
        
		// Construct GUI
		initComponents();		
		setFieldsEditable(false);
		
	    if (isDemo) {
	    	openDemoFile(DEMO_FILE_NAME);
	    }
	}

	/**
	 * Update the file name label.
	 * @param fName
	 */	
	private void updateFileNameLabel(String fName) {
		fileNameLabel.setText(fName);
	}

	private void updateCountLabel() {
        int size = 0;
		try {
			size = jQuestions.getQuestionPool().getSize();
		} catch (NullPointerException npe) {
			// No question set currently loaded.
			// so just return.
			return;
		} catch (Exception e) {
			System.out.println("Error: updating Count Label");
			e.printStackTrace();
			return;
		}

		// ASSERT: A question set has been loaded.
		if (nQuestions <= 0) {
			countLabel.setText("Question " + (qIdsIndex + 1) + 
					" of "	+ size);
			return;			
		}
		else 
		if (nQuestions > 0) {
			countLabel.setText("Question " + (qIdsIndex + 1) + 
					" of "	+ nQuestions);
			return;			
		} 
	}

	/**
	 * The user may get the option to change the configuration settings
	 * for the exam or quiz.
	 * @param qp
	 */	
	private void updateConfSettings(QuestionPool qp) {
		isChangeAllowed = qp.getIsChangeAllowed();
		isExam = qp.getIsExam();
		isTimed = qp.getTimed();
		timeLimit = qp.getTimeLimit();
		displayFinalScore = qp.getDisplayFinalScore();
		displayPrintOption = qp.getDisplayPrintOption();
		studentName = qp.getStudentName();
		nQuestions = qp.getNumberOfQuestions();

		// Update display for EXAM or QUIZ mode.
		if (isExam) {
			hintButton.setVisible(false);
			explainButton.setVisible(false);			
		}
		else {   // it's a Quiz
			hintButton.setVisible(true);
			explainButton.setVisible(true);			
		}	
		
		// Display countdown clock.
		if (isTimed) {			
			clockField.setText(timeLimit + ":00");
			clockField.setVisible(true);
		}
	}
	
	/**
	 * Display the given Question object. Choose the correct display panel 
	 * based on the QuestionType. Restore hint and explain text for the 
	 * Question which will be available to the user in 'Quiz' mode but not
	 * 'Exam' mode.
	 *  
	 * @param question - The Question to display.
	 */		
	private void displayQuestionRecord(Question question) {
		currentlyDisplayedQuestion = question;
		UserAnswer currentAnswer = userAnswers.get(currentRecordIndex);
		
		if (question.getQuestionType() == QuestionType.MULTI_CHOICE) {
			hintText = question.getHintText();
			explainText = question.getExplainText();
		    multiChoicePanel.displayQuestionRecord(question, currentAnswer);
		    
	        innerCardLayout.show(innerCardPanel, "multiChoicePanel");		    
		} 
		else 
		if (question.getQuestionType() == QuestionType.DRAG_N_DROP) {		
			hintText = question.getHintText();
			explainText = question.getExplainText();

			dndPanel.displayQuestionRecord(question, null);
	        innerCardLayout.show(innerCardPanel, "dndPanel");		    			
		}		
		
		updateCountLabel();		
	}
	

	/**
	 * User clicked 'Previous' button. Implicitly save any changes to 
	 * the user answer to the current question (because the user may want 
	 * to later come back and change their answer), and then retrieve and 
	 * display previous record in the List (if one exists).
	 *  
	 * @param e
	 */	
	private void previousButtonActionPerformed(ActionEvent e) {
		QuestionPool qPool;
		try {
			qPool = jQuestions.getQuestionPool();
		} catch (NullPointerException npe) {
			// Question Pool doesn't exist !
			return;
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		// 1. save current UserAnswer state.
		UserAnswer currentAnswer = userAnswers.get(currentRecordIndex);
		
		if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.MULTI_CHOICE) {

			int options = currentlyDisplayedQuestion.getNumberOfPossibleAnswers();
		    for (int i=0; i<options; i++) {
				currentAnswer.setAnswerBox(i, multiChoicePanel.getAnswerBox(i).isSelected());		    	
		    }
		}
		else 
		if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.DRAG_N_DROP) {
			// The contents of the JTextFields in a DnD question are conveniently
			// stored 'automatically' in the JTextField object itself, so we don't 
			// need to update them every time they are displayed.
            //currentAnswer.setAnswerField(x, answerField1.isSelected());....						
		}
		
		
		// 2. Display previous record.
		if (qIdsIndex > 0) {
		    qIdsIndex -= 1;
		}
		int index = questionIds.get(qIdsIndex);				
		currentlyDisplayedQuestion = qPool.getQuestion(index);
		if (currentlyDisplayedQuestion != null) {
			currentRecordIndex = index;
			displayQuestionRecord(currentlyDisplayedQuestion);
			setFieldsEditable(false);
		} else { // No records.
			currentRecordIndex = -1;
		}		
	}
	

	/**
	 * User clicked 'Next' button. Implicitly save any changes to 
	 * the user answer to the current question (because the user may want 
	 * to later come back and change their answer), and then retrieve and 
	 * display next record in the List (if one exists).
	 *  
	 * @param e
	 */	
	private void nextButtonActionPerformed(ActionEvent e) {
		QuestionPool qPool;

		try {
			qPool = jQuestions.getQuestionPool();
		} catch (NullPointerException npe) {
			return;
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		
		// 1. save current UserAnswer state.
		UserAnswer currentAnswer = userAnswers.get(currentRecordIndex);
		
		if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.MULTI_CHOICE) {
            
			int options = currentlyDisplayedQuestion.getNumberOfPossibleAnswers();
		    for (int i=0; i<options; i++) {
				currentAnswer.setAnswerBox(i, multiChoicePanel.getAnswerBox(i).isSelected());		    	
		    }
		}
		else 
		if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.DRAG_N_DROP) {
			// The contents of the JTextFields in a DnD question are conveniently
			// stored 'automatically' in the JTextField object itself, so we don't 
			// need to update them every time they are displayed.
            //currentAnswer.setAnswerField(x, answerField1.isSelected());....						
		}
		
		// 2. isCorrect()
		boolean correct = isCorrect(currentlyDisplayedQuestion, currentAnswer);

		// 3. Store the correctness.
		currentAnswer.setCorrectness(correct);
				
		if (!isExam) {  // Give instant feedback
			displayCorrectness(correct); 
	    }
		
				
		if ((nQuestions == 0) && (qIdsIndex >= qPool.getSize()-1) 
			|| ((nQuestions > 0) && (qIdsIndex < nQuestions))) {
				
			finished();    
			            	    	
	    	return;
		}
		

		// 4. If not end, display next record.
		qIdsIndex += 1;

		
		int index = questionIds.get(qIdsIndex);				
		currentlyDisplayedQuestion = qPool.getQuestion(index);
		if (currentlyDisplayedQuestion != null) {
			currentRecordIndex = index;
			displayQuestionRecord(currentlyDisplayedQuestion);
			setFieldsEditable(false);
		} else { // No records.
			currentRecordIndex = -1;
		}
	}

	/**
	 * Determine the correctness of the current user answer to 
	 * the current question.
	 *  
	 * @param question - The current question.
	 * @param answer - The current user answer.
	 * @return - True if correct.
	 */
	private boolean isCorrect(Question question, UserAnswer answer) {
		boolean retVal = true;
		
		if (question.getQuestionType() == QuestionType.MULTI_CHOICE) {

			if (multiChoicePanel.isCorrect(question, answer) == false) {
				retVal = false;
			}			
		}
		else
		if (question.getQuestionType() == QuestionType.DRAG_N_DROP) {

			if (dndPanel.isCorrect(question, answer) == false) {
				retVal = false;
			}
		}
		
		return retVal;
	}
	
	/**
	 * Display the correctness of the current user answer to 
	 * the current question.
	 *  
	 * @param correctness - True if correct.
	 */
	private void displayCorrectness(boolean correctness) {
		if (correctness == true) {
			correctnessLabel.setText("CORRECT");
			// Need to paint it immediately!!
			correctnessLabel.paintImmediately(correctnessLabel.getVisibleRect());
			
			// Display feedback briefly...
			try {
			    Thread.sleep(500);
			} catch (InterruptedException ie) {
    			// don't care
			}
			// Reset text
			correctnessLabel.setText("");			
		}
		else
		if (correctness == false) {
			correctnessLabel.setText("INCORRECT");
			// Need to paint it immediately
			correctnessLabel.paintImmediately(correctnessLabel.getVisibleRect());

			// Display feedback briefly...
			try {
			    Thread.sleep(500);
			} catch (InterruptedException ie) {
				// don't care	
			}			
			// Reset text
			correctnessLabel.setText("");			
		}			
	}

	/**
	 * If user has answered last question then display the Report window.
	 */
	
	public void finished() {
		if (reportWindow == null) {
    	    reportWindow = new ReportSummaryWindow(this);
		}
    	reportWindow.updateDisplay();
    	reportWindow.setVisible(true);
	}

	/**
	 * Display any 'Hint' text available for the current question.
	 * @param e
	 */
	private void hintButtonActionPerformed(ActionEvent e) {
		final MultiLineTextInputDialog multiDialog = new MultiLineTextInputDialog();
		if ((hintText == null) || (hintText.length() <= 0)) {
			String displayHintText = null;
			displayHintText = "No hint text available.";
			String response = 
				multiDialog.showInputDialog("Hint text",	displayHintText, false);
		}
		else {
			String response = 
				multiDialog.showInputDialog("Hint text",	hintText, false);
		}
		
			
	}

	/**
	 * Display any 'Explain' text available for the current question.
	 * @param e
	 */
	private void explainButtonActionPerformed(ActionEvent e) {
		final MultiLineTextInputDialog multiDialog = new MultiLineTextInputDialog();
		
		if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.MULTI_CHOICE) {

			List<Integer> correct = currentlyDisplayedQuestion.getCorrectAnswers();
			StringBuilder displayExplainText = new StringBuilder("Correct:");
            List<String> labels = new ArrayList<String>();
            
            // Display labels of correct answers first.
			for (Integer i : correct) {
				labels.add(multiChoicePanel.generateLabel(i - 1));
			}
			Collections.sort(labels);
			
			for (String s : labels) {
				displayExplainText.append(" " + s);
			}
			
			// Next, display explanation text for whole question (if any).
			displayExplainText.append('\n' + explainText + '\n');

			// Finally, display explanation text for each individual possible answer (if any).
			List<StringBuilder> explainAnswers = multiChoicePanel.getAllAnswerExplanations();
			int options = multiChoicePanel.getOptions();
			boolean allEmpty = true;
			
            for (int i=0; i<options; i++) {
            	String str = explainAnswers.get(i).toString();
            	if (str.length() > 0) {
            		allEmpty = false;
            		break;
            	}            	
            }			
			
            if (allEmpty) {
            	displayExplainText.append("\nNo further explanation available.");            	
            }
            else {
                for (int i=1; i<=options; i++) {
            	    String str = explainAnswers.get(i-1).toString();
            	    String correctness = null;
            	    if (correct.contains(i)) {
                		correctness = "is correct";
                	}
                	else {
                		correctness = "is incorrect";
            	    }
            	
            	    displayExplainText.append('\n' + multiChoicePanel.generateLabel(i-1) + " " + correctness + ": " + str);
                }
            }
			
			
			String response = multiDialog.showInputDialog(
					"Enter some explanation text", 
					        displayExplainText.toString(), false);
		}
		else 
		if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.DRAG_N_DROP) {	

			String response = multiDialog.showInputDialog(
					"Enter some explanation text", 
					        explainText.toString(), false);
		}
	}

	
	/**
	 * Define menu options.
	 * 
	 */
	
	private void menuAboutActionPerformed(ActionEvent e) {
		AboutDialog aboutDialog = AboutDialog.getInstance();
		aboutDialog.setVisible(true);
	}


	private void menuExitActionPerformed(ActionEvent e) {
		System.exit(0); // Terminate JVM without error.
	}


	private void openDemoFile(final String demoFileName) {		
		// Open the demo file - a resource file.
		jQuestions.readResourceFile(demoFileName, getClass());			

		initQuestionPool(demoFileName);		
	}
		

	/**
	 * File Open
	 */
	private void menuFileOpenActionPerformed(ActionEvent e) {
		// Handle open button action.
		int returnVal = fc.showOpenDialog(JQuestionsGUI.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {

            // Clear details of any currently open file.
            jQuestions.clearAll();    
                        
            updateFileNameLabel("");
    		countLabel.setText("Question 0 of 0");
            clockField.stop();
            clockField.setVisible(false);
            reportWindow = null;
    		
            
			// Open and read the selected file.
			File file = fc.getSelectedFile();
			
			if (!file.canRead()) {
				return;
			}
			
            String fileName = file.getPath();
			jQuestions.readFile(fileName);			

		    initQuestionPool(fileName);
		}
	}

	
	/**
	 * Read a pool of questions from the given file name.
	 * 
	 * @param fileName
	 */
	private void initQuestionPool(final String fileName) {
		// Get the pool of Questions
	    QuestionPool qPool = jQuestions.getQuestionPool();		
	    	    
		updateConfSettings(qPool);


		// Clear drag-n-drop question fields.
		for (int i=0; i < qPool.getSize(); i++) {
            Question question = qPool.getQuestion(i);

            if (question.getQuestionType() == QuestionType.DRAG_N_DROP) {
    			DefaultStyledDocument doc = question.getQuestionDoc();
    			if (doc == null) {
    				System.out.println("questionDoc is null!");
    				return;
    			}
    			
    			int fieldCount = 1;

    			for (int i1 = 0; i1 < doc.getLength(); i1++) {        				        				
    				// Identify any JTextFields
    				Component comp = StyleConstants.getComponent(doc
    						.getCharacterElement(i1).getAttributes());
    				if (comp != null && (comp instanceof JTextField)) {
    					JTextField jtf = (JTextField) comp;
                        // Clear field of text.
    					jtf.setText("");  
    					// Name text field for later referencing.
    					jtf.setName("answerField" + fieldCount++);
    					// Add drop listener
   					    new DropTarget(jtf, new JTextFieldDropTargetListener(jtf));

    					//String str = jtf.getName();
    				}
    			}
            }
		}					
		
		// Set config values from qPool of the .jqs file.
		configWindow.setValues(qPool.getDescriptionText(),				
				qPool.getIsExam(),
				qPool.getTimed(),
				qPool.getTimeLimit(),
				qPool.getDisplayFinalScore(),
				qPool.getDisplayPrintOption(),
				qPool.getNumberOfQuestions(),
				qPool.getStudentName());

				
		// Display the Config screen and allow user to change?
		if (isChangeAllowed) {
			configWindow.setVisible(true);

			
			// Get the response from the Config Window.
			// If 'OK'
			// - changes will have been stored with qPool
			// - we need to store the config vars,
			// else
			// - we use the Question Pool values from the .jqs file.
			// 
			if (pressedOK) {
				// updateConfig vars
				updateConfSettings(qPool);
				pressedOK = false;
			}
		}
				
		
        // Update some vars			
		updateFileNameLabel(fileName);

		
        questionIds = jQuestions.getQuestionIds();
        userAnswers = jQuestions.getUserAnswers();
       
        
        // Display the 'Start' screen...
        outerCardLayout.show(outerCardPanel, "startPanel");
        
        // Update menu options
        menuFileOpen.setEnabled(false);

        
		// Initialise answers ArrayList and shuffle how answers are displayed.
        userAnswers.clear();      
		for (int i=0; i < qPool.getSize(); i++) {
			Question question = qPool.getQuestion(i);
			UserAnswer newUserAnswer = new UserAnswer(question,i);				
			userAnswers.add(newUserAnswer);
			question.shuffleAnswers();
		}					

				
		// Questions are to be drawn of the Question Pool and asked
		// in a random order.
		getRandomQuestionIds(qPool.getSize(), nQuestions, questionIds);

				
		// Display first record (if one exists!)			
		if (qPool.getSize() > 0) {

			qIdsIndex = 0;
			int index = questionIds.get(qIdsIndex);				
			currentlyDisplayedQuestion = qPool.getQuestion(index);
			if (currentlyDisplayedQuestion != null) {
				currentRecordIndex = index;
				displayQuestionRecord(currentlyDisplayedQuestion);
				setFieldsEditable(false);
			} else { // No records.
				currentRecordIndex = -1;
			}
		}					
		
	}
		
	/**
	 * Create the random ordered list of questions we're going to ask.
	 * 
	 * @param size - How many questions are in the current pool ?
	 * @param nQuestions - How many questions are we going to asking from
	 *                     the current pool ?
	 * @param qIds - List of current question indices.
	 */	
	public void getRandomQuestionIds(int size, int nQuestions, ArrayList<Integer> qIds) {
		qIds.clear();

		int limit = 0;
		
		if (nQuestions == 0) {
			limit = size;
		}
		else {
			limit = nQuestions;
		}
		
		for (int i=0; i<limit; i++) {
		    int randVal;
		    do {
    		    randVal = (int)(Math.random()* size);
	    	} while (qIds.contains(randVal));
		    qIds.add(randVal);
		}		
	}


	public void clearDisplay() {
		multiChoicePanel.clearDisplay();
		dndPanel.clearDisplay();
	}

	public void setFieldsEditable(boolean b) {
		multiChoicePanel.setFieldsEditable(b);
		dndPanel.setFieldsEditable(b);
	}

	/**
	 * 
	 * Code to construct GUI components from here onwards.
	 * 
	 * 
	 */
	
	public JMenuBar getMenuBar() {
		return menuBar1;
	}

	public void setPressedOK(Boolean b) {
		pressedOK = b;
	}
	
	public JQuestions getJQuestions() {
		return jQuestions;
	}
	
	
	public Boolean getIsExam() {
		return isExam;
	}

	public Boolean getTimed() {
		return isTimed;
	}

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public Boolean getDisplayFinalScore() {
		return displayFinalScore;
	}

	public Boolean getDisplayPrintOption() {
		return displayPrintOption;
	}

	public String getStudentName() {
		return studentName;
	}

	public Integer getNQuestions() {
		return nQuestions;
	}
	
	public String getFileName() {
		return fileNameLabel.getText();
	}
	
	public Date getStartTime() {
	    return startTime;	
	}
	
    public ArrayList<UserAnswer> getUserAnswers() {
    	return userAnswers;
    }

    public ArrayList<Integer> getQuestionIds() {
    	return questionIds;
    }


	
	private void createMenuBar() {

		menuBar1 = new JMenuBar();
		menu1 = new JMenu();
		menuFileOpen = new JMenuItem();
		menuExit = new JMenuItem();
		menu2 = new JMenu();
		menuAbout = new JMenuItem();

		// ======== menuBar1 ========

		// ======== menu1 ========
		menu1.setText("File");

		// ---- menuFileOpen ----
		menuFileOpen.setText("File Open");
		menuFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFileOpenActionPerformed(e);
			}
		});
		menu1.add(menuFileOpen);

		if (isApplet) {
			menuFileOpen.setEnabled(false);
		}

		// ---- menuExit ----
		menuExit.setText("Exit");
		menuExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuExitActionPerformed(e);
			}
		});
		menu1.add(menuExit);

		if (isApplet) {
			menu1.setEnabled(false);
		}

		menuBar1.add(menu1);
		

		// ======== menu2 ========
		menu2.setText("Help");


		// ---- menuAbout ----
		menuAbout.setText("About JQuestions");
		menuAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAboutActionPerformed(e);
			}
		});
		menu2.add(menuAbout);
		
		menuBar1.add(menu2);		
	}
	
	private JPanel createStartPanel() {
		JPanel startPanel = new JPanel(new GridBagLayout());
		JButton startButton = new JButton("Start");

		//---- startButton ----
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Having pressed the 'Start' button, display
				// the contentPanel containing the innerCardLayout panel.
		        outerCardLayout.show(outerCardPanel, "contentPanel");

				
				// Get start time/date
		        startTime = new Date();
				
		        if (isTimed) {
    	            clockField.start(timeLimit);
	            }	
			}
		});
			
		//                                                  x  y  w  h  wtx  wty  anchor                   fill                                 T   L   B   R padx pady			
		startPanel.add(startButton, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 10, 0, 10, 0 ), 0, 0 ) );	
        startButton.setPreferredSize(new Dimension(150, 50));
        
        return startPanel;
	}

	
	public JPanel createNorthPanel() {
	    northPanel = new JPanel(new GridBagLayout());
		countLabel = new JLabel();
	    fileNameLabel = new JLabel();
		clockField = new ClockField(this);
		correctnessLabel = new JLabel();

	    
		//---- fileNameLabel ----
		fileNameLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        		

		// ---- clockField ----
		clockField.setText("");
		clockField.setHorizontalAlignment(SwingConstants.RIGHT);
		clockField.setVisible(false);
		
		// ---- correctnessLabel ----
		correctnessLabel.setForeground(Color.BLACK);
		correctnessLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		correctnessLabel.setText("");
		
		// ---- countLabel ----
		countLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		countLabel.setText("Question 0 of 0");

		//                                                    x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		northPanel.add(fileNameLabel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
		northPanel.add(clockField, new GridBagConstraints( 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );
		northPanel.add(correctnessLabel, new GridBagConstraints( 2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 25, 0 ) );
		northPanel.add(countLabel, new GridBagConstraints( 3, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );
		
		return northPanel;
	}

	
	public JPanel createSouthPanel() {
	    southPanel = new JPanel(new GridBagLayout());
        previousButton = new JButton();
        nextButton = new JButton();
        hintButton = new JButton();
        explainButton = new JButton();
	    
		// ---- previousButton ----
		previousButton.setText("Previous");
		previousButton
				.setToolTipText("<html>Display previous Question record (if one exists).</html>");
		previousButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				previousButtonActionPerformed(e);			
			}
		});

		
		//---- nextButton ----
		nextButton.setText("Next");
		nextButton.setToolTipText("<html>Display next Question record (if one exists).</html>");
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextButtonActionPerformed(e);
			}
		});
	
		
		//---- hintButton ----
		hintButton.setText("Hint");
		hintButton.setToolTipText("<html>Display any hint-text for current Question.</html>");
		hintButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hintButtonActionPerformed(e);
			}
		});
		

		//---- explainButton ----
		explainButton.setText("Explain");
		explainButton.setToolTipText("<html>Display answer and explanation text <br>for current Question.</html>");
		explainButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				explainButtonActionPerformed(e);
			}
		});

		//                                                     x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		southPanel.add(previousButton, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );	
		southPanel.add(nextButton, new GridBagConstraints( 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );
		southPanel.add(hintButton, new GridBagConstraints( 4, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );
		southPanel.add(explainButton, new GridBagConstraints( 5, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );		

	    return southPanel;
	}
	
	
	public JPanel createBlankPanel() {
		JPanel blankPanel = new JPanel();
		blankPanel.setBackground(Color.LIGHT_GRAY);
		return blankPanel;
	}
	
	private void initComponents() {
		
		//======== this ========
		final Container rootContainer = this;
		rootContainer.setLayout(new BorderLayout());
		
        outerCardLayout = new CardLayout();
        outerCardPanel = new JPanel(outerCardLayout);

		rootContainer.add(outerCardPanel, BorderLayout.CENTER);
		
        createMenuBar();		
        blankPanel = createBlankPanel();		
		startPanel = createStartPanel();

        
        northPanel = createNorthPanel();
        innerCardLayout = new CardLayout();        
        innerCardPanel = new JPanel(innerCardLayout);
        southPanel = createSouthPanel();                
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(northPanel, BorderLayout.NORTH);
        contentPanel.add(innerCardPanel, BorderLayout.CENTER);
        contentPanel.add(southPanel, BorderLayout.SOUTH);
        
        outerCardPanel.add(blankPanel, "blankPanel");
        outerCardPanel.add(startPanel, "startPanel");
        outerCardPanel.add(contentPanel, "contentPanel");

        
        multiChoicePanel = new MultiChoiceQuestionScreen(northPanel, southPanel);
        dndPanel = new DragAndDropQuestionScreen(northPanel, southPanel);
    
        innerCardPanel.add(multiChoicePanel, "multiChoicePanel");
        innerCardPanel.add(dndPanel, "dndPanel");        
	}
}


/*
 * This class defines the display for the Clock Field and controls
 * the timing thread.  
 */
@SuppressWarnings("serial")
class ClockField extends JTextField {
	JQuestionsGUI jqg;
    Timer m_t = null;
    int value;
    
    //================================================== constructor
    
    public void start(int i) {
    	value = i * 60;
    	
    	if (m_t == null) {
            //... Create a 1-second timer.
            m_t = new Timer(1000, new ClockTickAction());
    	}
    	m_t.start();  // Start the timer    	
    }
    
    public void stop() {
    	if (m_t != null) {
    	    m_t.stop();
    	}
    }
    
    public ClockField(JQuestionsGUI parent) {
        //... Set some attributes.
    	jqg = parent;
        setColumns(6);
        setFont(new Font("sansserif", Font.BOLD, 12));
        setOpaque(false);
        setEditable(false);
        setBorder(null);
    }

    /////////////////////////////////////////// inner class listener
    private class ClockTickAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {

        	// Decrement value.
        	value = value - 1;

        	int mins = (value / 60);
        	int secs = (value % 60);
        	if (secs < 10) {    // leading zero
        	    setText(mins + ":0" + secs);
        	}
        	else {
        	    setText(mins + ":" + secs);
        	}        	
        	
        	if (value == 0) { // Countdown has ended !!
                jqg.finished();  // Display Report window!!
                m_t.stop();
        	}
        }
    }
}

