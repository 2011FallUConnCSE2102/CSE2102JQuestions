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


import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import jquestionseditor.JQuestionsEditor;
import question.PossibleAnswer;
import question.Question;
import question.QuestionPool;
import question.QuestionType;
import utils.IOUtils;
import utils.ImageFileFilter;
import utils.JQSFileFilter;
import utils.MultiLineTextInputDialog;



/**
 * This class defines the main user interface components and methods 
 * for the JQuestions Editor application.
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see JQuestionsEditor
 * @see DragAndDropQuestionScreen
 * @see MultiChoiceQuestionScreen
 */
@SuppressWarnings("serial")
public class JQuestionsEditorGUI extends JPanel {
	
	// Reference to JQuestionsEditor application.
	final private JQuestionsEditor jqEditor;

	// Default name for a new .jqs file.
	final private String DEFAULT_FILENAME = "New1.jqs";

	// Various state variables.
	final private JFileChooser fc;
    private boolean isCreatingNew = false;
    private int currentRecordId = -1;
    private Question currentlyDisplayedQuestion;
    private QuestionType newQuestionType;
    private String hintText;     // For current Question
    private String explainText;    // For current Question

    // GUI components.
    private ConfigurationWindow configWindow;   
	private MultiChoiceQuestionScreen multiChoicePanel;
	private DragAndDropQuestionScreen dndPanel;	
	private JPanel blankPanel, northPanel, contentPanel, southPanel;
	private CardLayout outerCardLayout;
	private JPanel outerCardPanel;
	private CardLayout innerCardLayout;
	private JPanel innerCardPanel;
	private JButton nextButton, previousButton, newButton, 
	     deleteButton, hintButton, explainButton;
	private JMenuBar menuBar1;
	private JMenu menu1, menu2;
	private JMenuItem menuFileNew, menuFileOpen, menuFileOpenAndAppend, menuFileSave, 
	     menuFileClose, menuFileSaveMarked, menuDeleteMarked, menuInsertImage, menuConfig, 
	     menuExit, menuAbout;
	private JLabel countLabel, fileNameLabel;
	private JCheckBox markQuestionBox;

	/**
	 * Constructor for the GUI. The code for the GUI and the main
	 * application is kept separated into two different classes
	 * This means you can change the UI without making changes 
	 * to the underlying application.
	 * 
	 * @param jqTool - Reference to the main application.
	 */    
	public JQuestionsEditorGUI(JQuestionsEditor jqTool) {
		jqEditor = jqTool;
	 			
		//Create a file chooser
        fc = new JFileChooser();
        fc.addChoosableFileFilter(new JQSFileFilter());
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);                
        
        // Construct GUI
        initComponents();
        
        // Set initial display.
        outerCardLayout.show(outerCardPanel, "blankPanel");
        
        // Set initial available menu options.
        menuFileSave.setEnabled(false);
        menuFileSaveMarked.setEnabled(false);
        menuDeleteMarked.setEnabled(false);
        menuFileClose.setEnabled(false);
        menuInsertImage.setEnabled(false);
        
        // The config window.
        configWindow = new ConfigurationWindow(this);
	}
	
	/**
	 * Update the file name label.
	 * @param fName
	 */
	private void updateFileNameLabel(String fName) {
		fileNameLabel.setText(fName);
	}
	
	
	private void updateCountLabel() {
		int size;
		try {
		    size = jqEditor.getQuestionPool().getSize();
		}
		catch (NullPointerException npe) {
			size = 0;
			countLabel.setText("Question 0 of 0");
			return;
		}
		catch (Exception e) {
            e.printStackTrace();
			return;
		}
		
		if (isCreatingNew) {
			if (size <= 0) {
				countLabel.setText("Question 1 of 1");
				return;
			}
			if (size >= 1) {
				countLabel.setText("Question " + (size+1) + " of " + (size+1));
				return;
			}			
		}
		else {		
			if (size <= 0) {
				countLabel.setText("Question 0 of 0");
				return;
		}
			if (size == 1) {
				countLabel.setText("Question 1 of 1");
				return;
		}
			if (size > 1) {
				countLabel.setText("Question " + (currentRecordId+1) + " of " + size);
				return;
			}
		}
	}

	/**
	 * Save the current question.
	 * 
	 * A Question record is implicitly saved each time the user presses 
	 * the 'Next' or 'Previous'  buttons. 
	 * 
	 * @return - The saved Question object.
	 */
	private Question saveQuestionRecord() {
		Question newQuestion = null;
		
		if (newQuestionType == QuestionType.MULTI_CHOICE) {
            newQuestion = multiChoicePanel.saveQuestionRecord(hintText, explainText, markQuestionBox.isSelected());
		}
		else
		if (newQuestionType == QuestionType.DRAG_N_DROP) {
			newQuestion = dndPanel.saveQuestionRecord(hintText, explainText, markQuestionBox.isSelected());			
		}
		return newQuestion;
	}

	/**
	 * Display the given Question object. Choose the correct display panel 
	 * based on the QuestionType. Restore hint and explain text for the 
	 * Question which may be edited by the user.
	 *  
	 * @param question - The Question to display.
	 */	
	private void displayQuestionRecord(Question question) {
		currentlyDisplayedQuestion = question;

		hintText = question.getHintText();
		explainText = question.getExplainText();
        markQuestionBox.setSelected(question.isMarked());
		
		if (question.getQuestionType() == QuestionType.MULTI_CHOICE) {
			innerCardLayout.show(innerCardPanel, "multiChoicePanel");			
			multiChoicePanel.displayQuestionRecord(question);
		}
		else 
		if (question.getQuestionType() == QuestionType.DRAG_N_DROP) {
			innerCardLayout.show(innerCardPanel, "dndPanel");
            dndPanel.displayQuestionRecord(question);
		}

        menuInsertImage.setEnabled(true);

		updateCountLabel();
	}

	/**
	 * Check currently displayed Question details and save any changes.
	 * 
	 */	
	private void checkIfChanged() {
		if (currentlyDisplayedQuestion == null) {
			return;   
		}
		
		String qHintText = currentlyDisplayedQuestion.getHintText();
		if (hintText == null) {
			hintText = "";
		}
		
		if (qHintText.compareTo(hintText) != 0) { 
			currentlyDisplayedQuestion.setHintText(hintText);
		}

		if (explainText == null) {
			explainText = "";
		}

		String qExplainText = currentlyDisplayedQuestion.getExplainText();
		if (qExplainText.compareTo(explainText) != 0) { 
			currentlyDisplayedQuestion.setExplainText(explainText);
		}

		Boolean isMarked = currentlyDisplayedQuestion.isMarked();
		if (markQuestionBox.isSelected() != isMarked) {
			currentlyDisplayedQuestion.setMarked(markQuestionBox.isSelected());
		}
		
		if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.MULTI_CHOICE) {
			DefaultStyledDocument qDoc = (DefaultStyledDocument) multiChoicePanel.getMainTextArea().getDocument();
			DefaultStyledDocument cdDoc = currentlyDisplayedQuestion.getQuestionDoc();
			currentlyDisplayedQuestion.setQuestionDoc(qDoc);			
			
			String questionStr = multiChoicePanel.getQuestionText().getText();
			String currentQuestionStr = currentlyDisplayedQuestion.getQuestionText();
			if (questionStr.compareTo(currentQuestionStr) != 0) { 
				currentlyDisplayedQuestion.setQuestionText(questionStr);
			}

			List<String> possibleAnswers = multiChoicePanel.getAllAnswerTexts();
			List<StringBuilder> explainPossibleAnswers = multiChoicePanel.getAllAnswerExplanations();
			List<PossibleAnswer> currentPossibleAnswers = currentlyDisplayedQuestion.getPossibleAnswers();
			
			// Save possible answers.  
			List<PossibleAnswer> newPossibleAnswers = new ArrayList<PossibleAnswer>();
			for (int i=0; i<possibleAnswers.size(); i++) {
				PossibleAnswer newPa = new PossibleAnswer(possibleAnswers.get(i), false, 0, explainPossibleAnswers.get(i));
				newPossibleAnswers.add(newPa);
			}				
			currentlyDisplayedQuestion.setPossibleAnswers(newPossibleAnswers);

			List<Integer> correctAnswers = multiChoicePanel.getAllCorrectAnswers();
			if (!correctAnswers.equals(currentlyDisplayedQuestion.getCorrectAnswers())) {
				currentlyDisplayedQuestion.setCorrectAnswers(correctAnswers);
			}
		}
		else
		if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.DRAG_N_DROP) {
            DefaultStyledDocument qDoc = (DefaultStyledDocument) dndPanel.getMainTextArea().getDocument();
            DefaultStyledDocument cdDoc = currentlyDisplayedQuestion.getQuestionDoc();
            currentlyDisplayedQuestion.setQuestionDoc(qDoc);

			String dndQuestionStr = dndPanel.getQuestionTextArea().getText();
			String currentQuestionStr = currentlyDisplayedQuestion.getQuestionText();
			if (dndQuestionStr.compareTo(currentQuestionStr) != 0) { 
				currentlyDisplayedQuestion.setQuestionText(dndQuestionStr);
			}
			
			// Answer fields....checkIfChanged...
			List<String> possibleAnswers = dndPanel.getAllAnswerTexts();

			List<PossibleAnswer> newPossibleAnswers = new ArrayList<PossibleAnswer>();
			for (int i=0; i<possibleAnswers.size(); i++) {
				PossibleAnswer newPa = new PossibleAnswer(possibleAnswers.get(i), false);
				newPossibleAnswers.add(newPa);
			}				
			currentlyDisplayedQuestion.setPossibleAnswers(newPossibleAnswers);
						
    		List<Integer> correctAnswers = new LinkedList<Integer>();
    		dndPanel.setCorrectAnswersForDnD(dndPanel.getMainTextArea(), newPossibleAnswers, correctAnswers);
			currentlyDisplayedQuestion.setCorrectAnswers(correctAnswers);
		}		
	}
	

	/**
	 * User clicked 'Previous' button. Implicitly save any changes to 
	 * current record, and then retrieve and display previous record
	 * in the List (if one exists).
	 *  
	 * @param e
	 */	
	private void previousButtonActionPerformed(ActionEvent e) {
		QuestionPool qPool; 
		try {
		    qPool = jqEditor.getQuestionPool();
		}
		catch (NullPointerException npe) {
			// Question Pool doesn't exist !
            npe.printStackTrace();
			return;
		}
		catch (Exception ex) {
            ex.printStackTrace();
			return;
		}		
	
		// Need to check for any errors before we can save Question object.
		if (doErrorChecking() == false) {
			return;
		}
					
		
		if (isCreatingNew == true) {   // save new record
			qPool.addQuestion(saveQuestionRecord());   // add to question pool
			currentRecordId = qPool.getSize()-1;   // point to last (new) record.
			currentlyDisplayedQuestion = qPool.getQuestion(currentRecordId);
			isCreatingNew = false;
		}
		else      // If currently displayed record has been changed then  
    		checkIfChanged();  {      		// need to update record.
		}
    		
		// display previous record (if one exists)
		if (currentRecordId > 0) { 
		    currentRecordId -= 1;
		}		    
		Question prevRecord = qPool.getQuestion(currentRecordId);
		if (prevRecord != null) {
			displayQuestionRecord(prevRecord);	
		}				

		// Update menu options.
        menuInsertImage.setEnabled(true);
        menuFileSave.setEnabled(true);
        menuFileSaveMarked.setEnabled(true);

        // Debug only.
        //qPool.printAllQuestions(false);	
	}

	/**
	 * User clicked 'Next' button. Implicitly save any changes to 
	 * current record, and then retrieve and display next record
	 * in the List (if one exists).
	 *  
	 * @param e
	 */	
	private void nextButtonActionPerformed(ActionEvent e) {
		QuestionPool qPool; 
		
		try {
		    qPool = jqEditor.getQuestionPool();
		}
		catch (NullPointerException npe) {
			return;
		}
		catch (Exception ex) {
            ex.printStackTrace();
			return;
		}		

		// Need to check for any errors before we can save Question object.
		if (doErrorChecking() == false) {
			return;
		}
					
		
		if (isCreatingNew == true) {   // save new record
			qPool.addQuestion(saveQuestionRecord());   // add to question pool
			currentRecordId = qPool.getSize()-1;   // point to last (new) record.
			currentlyDisplayedQuestion = qPool.getQuestion(currentRecordId);
			isCreatingNew = false;
		}
		else      // If currently displayed record has been changed then  
    		checkIfChanged();  {      		// need to update record.
		}

    		
		// display next record (if one exists)
		Question nextRecord = qPool.getQuestion(currentRecordId+1);
		if (nextRecord != null) {
			currentRecordId += 1;
			displayQuestionRecord(nextRecord);
		}		 		
	
		// Update menu options
        menuInsertImage.setEnabled(true);
        menuFileSave.setEnabled(true);
        menuFileSaveMarked.setEnabled(true);
        
        // Debug only.
        //qPool.printAllQuestions(false);
	}


	/**
	 * Before we can save the current record we need to perform 
	 * error checking.
	 * 
	 * Errors are reported within methods.
	 * 
	 * A boolean response is returned indicating if record was
	 * successfully saved or not.
	 * 
	 * @return
	 */
	public boolean doErrorChecking() {
		boolean retVal = true;
		
		if (isCreatingNew) {
			if (newQuestionType == QuestionType.DRAG_N_DROP) {
	    		retVal = dndPanel.errorChecking();				
			}
			else 
			if (newQuestionType == QuestionType.MULTI_CHOICE) {
	    		retVal = multiChoicePanel.errorChecking();
			}			
		}
		else {  // not creating a new 
	        if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.DRAG_N_DROP) {
	    		retVal = dndPanel.errorChecking();
	        }
	        else 
	        if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.MULTI_CHOICE) {
	    		retVal = multiChoicePanel.errorChecking();
	        }			
		}
		
		return retVal;
	}
	

	/**
	 * User has clicked 'new' button to create a new Question record.
	 * 
	 * @param e
	 */
	private void newButtonActionPerformed(ActionEvent e) {		
		
		// Save any changes to current record (if one)
		if (currentlyDisplayedQuestion != null) {
    		if (doErrorChecking() == false) {
			    return;
	    	}
		    checkIfChanged();
		}		
		
		
        Object[] options = {"Multiple Choice", "Drag and Drop"};
        String questionTypeStr = (String) JOptionPane.showInputDialog(
                            this,
                            "What type of Question would you like to create ?\n",
                            "Create a new Question Dialog",
                            JOptionPane.QUESTION_MESSAGE,
                            null, 
                            options,
                            options[0]);

        //If a string was returned...
        if ((questionTypeStr != null) && (questionTypeStr.length() > 0)) {
            if (questionTypeStr.compareTo((String) options[0]) == 0) {
    			hintText = "";
    			explainText = "";        		
            	multiChoicePanel.getMainTextArea().setDocument(new DefaultStyledDocument());
    			newQuestionType = QuestionType.MULTI_CHOICE;
    			multiChoicePanel.setOptions(4);
    		    isCreatingNew = true;    
    	        outerCardLayout.show(outerCardPanel, "contentPanel");
            	innerCardLayout.show(innerCardPanel, "multiChoicePanel");
            	multiChoicePanel.clearDisplay();
            }
            else 
            if (questionTypeStr.compareTo((String) options[1]) == 0) {            	
    			hintText = "";
    			explainText = "";        		
            	dndPanel.getMainTextArea().setDocument(new DefaultStyledDocument());
       			newQuestionType = QuestionType.DRAG_N_DROP;
    			dndPanel.setOptions(1);
       		    isCreatingNew = true;
    	        outerCardLayout.show(outerCardPanel, "contentPanel");
       		    innerCardLayout.show(innerCardPanel, "dndPanel");
            	dndPanel.clearDisplay();
            }                

    		markQuestionBox.setSelected(false);
	        menuInsertImage.setEnabled(true);
    		updateCountLabel();
        }
        // else...'Cancel' was pressed - do nothing		
	}

	/**
	 * User has clicked 'delete' button to delete the current Question record.
	 * 
	 * @param e
	 */	
	private void deleteButtonActionPerformed(ActionEvent e) {					
		QuestionPool qPool; 
		Object[] options = { "Yes", "No", "Cancel" };

		try {
		    qPool = jqEditor.getQuestionPool();
		}
		catch (NullPointerException npe) {
			return;
		}
		catch (Exception ex) {
            ex.printStackTrace();
			return;
		}		

		if (isCreatingNew) {
			int value = JOptionPane.showOptionDialog(this,
					"Delete the current Question record ?",
					"Delete Question?", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[2]);

			if (value == JOptionPane.YES_OPTION) {
				// discard current record

				if (qPool.getSize() <= 0) {
					// No records left to display!
					// Clear GUI and reset internal variables.
					currentRecordId = -1;
					currentlyDisplayedQuestion = null;
		            hintText = "";
		            explainText = "";
		            markQuestionBox.setSelected(false);

		            // Return to start state.
		            outerCardLayout.show(outerCardPanel, "blankPanel");
			        menuFileSave.setEnabled(false);
			        menuFileSaveMarked.setEnabled(false);
			        menuDeleteMarked.setEnabled(false);
			        menuInsertImage.setEnabled(false);					
				}
				else {  // display previous record 
					if (currentRecordId > 0) { 
					    currentRecordId -= 1;
					}		    
					Question prevRecord = qPool.getQuestion(currentRecordId);
					if (prevRecord != null) {
						displayQuestionRecord(prevRecord);	
					}				

			        menuInsertImage.setEnabled(true);
			        menuFileSave.setEnabled(true);
			        menuFileSaveMarked.setEnabled(true);      					
				}			
			}

			// Tidy up.
			isCreatingNew = false;
    		updateCountLabel();
			return;							
		}
		
		// Not creating a new record.
		if (qPool.getSize() <= 0) {
		    JOptionPane.showMessageDialog(this,"No Question record to delete!");
		    return;
		}
		
		int value = JOptionPane.showOptionDialog(this,
				"Delete the current Question record ?",
				"Delete Question?", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[2]);

		if (value == JOptionPane.YES_OPTION) {
			boolean retVal = qPool.deleteQuestion(currentlyDisplayedQuestion);
		    if (retVal == true) {
			    
			    if (qPool.getSize() > 0) { 
			    	previousButtonActionPerformed(null);
			    }
			    else {   // No records left to display!
					// Clear GUI and reset internal variables.
					currentRecordId = -1;
					currentlyDisplayedQuestion = null;
		            hintText = "";
		            explainText = "";
		            markQuestionBox.setSelected(false);

		            // Return to start state.
		            outerCardLayout.show(outerCardPanel, "blankPanel");
		    		updateCountLabel();
		            menuFileSave.setEnabled(false);
			        menuFileSaveMarked.setEnabled(false);
			        menuDeleteMarked.setEnabled(false);
			        menuInsertImage.setEnabled(false);
			    }
			    JOptionPane.showMessageDialog(this,"Question has been deleted.");
		    }
		    else {
			    JOptionPane.showMessageDialog(this,"Error deleting Question has failed.");
		    }			
		}
	}

	/** 
	 * Each question may have 'Hint' text which may be edited 
	 * by the user and made available to the examinee at exam-time.  
	 * 
	 * @param e
	 */
	private void hintButtonActionPerformed(ActionEvent e) {
		MultiLineTextInputDialog multiDialog = new MultiLineTextInputDialog();
		String response = multiDialog.showInputDialog("Enter some hint text", hintText, true);
		if (response == null) {   // User pressed 'Cancel'
			return;   // hint text (if any) remains unchanged. 
		}
		else {
            hintText = response;
		}
	}

	/** 
	 * Each question may have 'Explain' text which may be edited 
	 * by the user and made available to the examinee at exam-time.  
	 * 
	 * @param e
	 */
	private void explainButtonActionPerformed(ActionEvent e) {
		MultiLineTextInputDialog multiDialog = new MultiLineTextInputDialog();
		String response = multiDialog.showInputDialog("Enter some explanation text", explainText, true);
		if (response == null) {   // User pressed 'Cancel'
			return;   // explanation text (if any) remains unchanged. 
		}
		else {
            explainText = response;
		}		
	}

	/**
	 * Menu actions
	 * 
	 */
	private void menuAboutActionPerformed(ActionEvent e) {
		AboutDialog aboutDialog = AboutDialog.getInstance();
		aboutDialog.setVisible(true);
	}
	
	private void menuExitActionPerformed(ActionEvent e) {
		System.exit(0);       // Terminate JVM without error.
	}

	/**
	 * File Close
	 * @param e
	 */
	private void menuFileCloseActionPerformed(ActionEvent e) {
		Object[] options = {"Yes", "No"};
	
		int value = JOptionPane.showOptionDialog(this,
				"Save current file before closing?",
				"Save current file before closing?", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

		if (value == JOptionPane.YES_OPTION) {
			menuFileSaveActionPerformed(e);
		}
		else {
            QuestionPool qPool = jqEditor.getQuestionPool();
            qPool.removeAllQuestions();
			
			// Clear GUI and reset internal variables.
			currentRecordId = -1;
			currentlyDisplayedQuestion = null;
            hintText = "";
            explainText = "";
            markQuestionBox.setSelected(false);

            // Return to start state
            outerCardLayout.show(outerCardPanel, "blankPanel");
            
            // Update menu options.
	        menuFileSave.setEnabled(false);
	        menuFileSaveMarked.setEnabled(false);
	        menuDeleteMarked.setEnabled(false);
	        menuInsertImage.setEnabled(false);		
		}		
	}


	/**
	 * File New
	 * @param e
	 */
	private void menuFileNewActionPerformed(ActionEvent e) {
		if ((dndPanel.isShowing()) || (multiChoicePanel.isShowing())) {
			Object[] options = { "Yes", "No" };

			int value = JOptionPane.showOptionDialog(this,
					"Save current file before creating new?",
					"Save current file before creating new?",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, options, options[1]);

			if (value == JOptionPane.YES_OPTION) {
				menuFileSaveActionPerformed(e);
			}
		}
	     
		QuestionPool qPool = jqEditor.getQuestionPool();
		qPool.removeAllQuestions();
				
		// Clear GUI and reset internal variables.
		currentRecordId = -1;
		currentlyDisplayedQuestion = null;
        hintText = "";
        explainText = "";
        markQuestionBox.setSelected(false);

        // Return to start state and set menu options.
        outerCardLayout.show(outerCardPanel, "blankPanel");
        menuFileSave.setEnabled(false);
        menuFileSaveMarked.setEnabled(false);
        menuDeleteMarked.setEnabled(false);
        menuInsertImage.setEnabled(false);

        newButtonActionPerformed(e);
        updateFileNameLabel(DEFAULT_FILENAME);

        // Update menu options.
        menuFileClose.setEnabled(true);
	}


	/**
	 * File Open - 
	 *     If append is false then the current file may be saved before
	 *     it is closed and the new file is loaded.
	 *     If append is true then the new file is loaded and the questions
	 *     are appended to the existing list of questions. 
	 * In this way question pools can be easily merged and grow.
	 *     
	 * @param e
	 * @param append - Append the new set of questions to the current 
	 *        pool or not.
	 */
	private void menuFileOpenActionPerformed(ActionEvent e, boolean append) {
		
		if (blankPanel.isShowing() == false) {
			Object[] options = { "Yes", "No" };

			int value = JOptionPane.showOptionDialog(this,
					"Save current file before opening new?",
					"Save current file before opening new?",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, options, options[1]);

			if (value == JOptionPane.YES_OPTION) {
				menuFileSaveActionPerformed(e);

	            QuestionPool qPool = jqEditor.getQuestionPool();
	            qPool.removeAllQuestions();
				
				// Clear GUI and reset internal variables.
				currentRecordId = -1;
				currentlyDisplayedQuestion = null;
	            hintText = "";
	            explainText = "";
	            markQuestionBox.setSelected(false);

	            // Return to start state and set menu options.
	            outerCardLayout.show(outerCardPanel, "blankPanel");
		        menuFileSave.setEnabled(false);
		        menuFileSaveMarked.setEnabled(false);
		        menuDeleteMarked.setEnabled(false);
		        menuInsertImage.setEnabled(false);		
			}
		}

		QuestionPool qPool = null;
			
		// Handle open button action - show FileChooser dialog.
		int returnVal = fc.showOpenDialog(JQuestionsEditorGUI.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// Open the file.
			File file = fc.getSelectedFile();
			
			if (!file.canRead()) {
				return;
			}
			
			if (append) {
			    qPool = jqEditor.getQuestionPool();
			}
			else 
			if (!append) {  // Set new QuestionPool
				qPool = new QuestionPool();
				jqEditor.setQuestionPool(qPool);
				updateFileNameLabel(file.getName());
			}
			IOUtils.readFile(file.getPath(), qPool, null);

			if (!append) {
				configWindow.setValues(qPool.getDescriptionText(),
					qPool.getIsChangeAllowed(),
					qPool.getIsExam(),
					qPool.getTimed(),
					qPool.getTimeLimit(),
					qPool.getDisplayFinalScore(),
					qPool.getDisplayPrintOption(),
					qPool.getNumberOfQuestions(),
					qPool.getStudentName());
			}
			
			// Display the contentPanel containing the innerCardLayout panel.
	        outerCardLayout.show(outerCardPanel, "contentPanel");
	        menuFileSave.setEnabled(true);
	        menuFileSaveMarked.setEnabled(true);
	        menuDeleteMarked.setEnabled(true);

	        
			if (qPool.getSize() > 0) {
				// Display first record (if one exists!)
			    currentlyDisplayedQuestion = qPool.getQuestion(0); 								
				if (currentlyDisplayedQuestion != null) {
				    currentRecordId = 0;
					displayQuestionRecord(currentlyDisplayedQuestion);
			        menuInsertImage.setEnabled(true);
					setFieldsEditable(true);
				}				
				else {  // No records.
				    currentRecordId = -1;
				}
			}			
		} 

        // Update menu options.
        menuFileClose.setEnabled(true);
	}		

	/**
	 * File Delete
	 * @param e
	 */
	private void menuDeleteMarkedActionPerformed(ActionEvent e) {
	    QuestionPool qPool = jqEditor.getQuestionPool();

		if (qPool.getSize() <= 0) {
		    JOptionPane.showMessageDialog(this,"No Question record to delete!");
		    return;
		}

		LinkedList<Question> questions = new LinkedList<Question>();
		
		for (int i=0; i<qPool.getSize(); i++) {
			Question question = qPool.getQuestion(i);
			if (question.isMarked()) {
				questions.add(question);
			}
		}		
		
		Object[] options = {"Yes", "No"};
		String questionString = null;
		int numQs = questions.size();
		if (numQs == 0) {
		    JOptionPane.showMessageDialog(this,"No marked Questions to delete!");
     	    return;
		}
		else 
		if (numQs == 1) 	
			questionString = "Delete the " + numQs + " marked Question record ?";	
		else 
		if (numQs > 1)	
			questionString = "Delete the " + numQs + " marked Question records ?";
				
		int value = JOptionPane.showOptionDialog(this,
				questionString,
				"Delete Marked Questions?", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

		if (value == JOptionPane.YES_OPTION) {
			
			for (Question q : questions) {
				boolean retVal = qPool.deleteQuestion(q);
			}

			if (qPool.getSize() == 0) { // No records left to display!
				// Clear GUI and reset internal variables.
				currentRecordId = -1;
				currentlyDisplayedQuestion = null;
				clearDisplay();
				hintText = "";
				explainText = "";
				markQuestionBox.setSelected(false);
				innerCardLayout.show(innerCardPanel, "multiChoicePanel");
				updateCountLabel();
			}
			else
			if (qPool.getSize() > 0) {
				// Display first record (if one exists!)
			    currentlyDisplayedQuestion = qPool.getQuestion(0); 								
				if (currentlyDisplayedQuestion != null) {
				    currentRecordId = 0;
					displayQuestionRecord(currentlyDisplayedQuestion);
			        menuInsertImage.setEnabled(true);
					setFieldsEditable(true);
				}				
				else {  // No records.
				    currentRecordId = -1;
				}								
				updateCountLabel();					
			}
			
			JOptionPane.showMessageDialog(this, "Marked Questions have been deleted.");
		} 			    
	}
	

	/**
	 * File Save Marked questions.
	 * 
	 * @param e
	 */	
	private void menuFileSaveMarkedActionPerformed(ActionEvent e) {
	
	    QuestionPool qPool = jqEditor.getQuestionPool();

		if (qPool.getSize() <= 0) {
		    JOptionPane.showMessageDialog(this,"No Question record to save!");
		    return;
		}

		List<Question> questions = new LinkedList<Question>();
		
		for (int i=0; i<qPool.getSize(); i++) {
			Question question = qPool.getQuestion(i);
			if (question.isMarked()) {
				questions.add(question);
			}
		}		

		// Create temp pool of marked questions.
		QuestionPool tempPool = new QuestionPool();
		for (Question q : questions) {
			tempPool.addQuestion(q);
		}

		
		Object[] options = {"Yes", "No"};
		String questionString = null;
		int numQs = questions.size();
		if (numQs == 0) {
		    JOptionPane.showMessageDialog(this,"No marked Questions to save!");
     	    return;
		}
		else 
		if (numQs == 1) 	
			questionString = "Save the " + numQs + " marked Question record ?";	
		else 
		if (numQs > 1)	
			questionString = "Save the " + numQs + " marked Question records ?";
				
		int value = JOptionPane.showOptionDialog(this,
				questionString,
				"Save Marked Questions?", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

		if (value == JOptionPane.YES_OPTION) {			
			int returnVal = fc.showSaveDialog(JQuestionsEditorGUI.this);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            //Save the file with the pool of marked questions.
	    		IOUtils.writeFile(file.getPath(), tempPool, true);
	        } 

			JOptionPane.showMessageDialog(this, "Marked Questions have been saved.");
		} 			    
	}
	
	/**
	 * File Save (all) questions.
	 * 
	 * @param e
	 */	
	private void menuFileSaveActionPerformed(ActionEvent e) {
        int returnVal = fc.showSaveDialog(JQuestionsEditorGUI.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //Save the file.
			QuestionPool qPool = jqEditor.getQuestionPool();
    		IOUtils.writeFile(file.getPath(), qPool, false);
    		updateFileNameLabel(file.getName());
        } 
	}

	/**
	 * Insert an image file (.jpeg, .tiff, .gif, .png) into
	 * main panel of current question.
	 * 
	 * @param e
	 */
	public void menuInsertImageActionPerformed(ActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
	    chooser.addChoosableFileFilter(new ImageFileFilter());
	    int status = chooser.showOpenDialog(this);	    
	    if (status == JFileChooser.APPROVE_OPTION) {
	    	File file = chooser.getSelectedFile();
	        Icon icon = new ImageIcon (file.getAbsolutePath());
	        
	        if (dndPanel.isShowing()) {
	        	dndPanel.insertIcon (icon);
			}
			else
		    if (multiChoicePanel.isShowing()) {			
				multiChoicePanel.insertIcon(icon);					
			}
	    }
	}

	/**
	 * Change the configuration settings for the current question pool.
	 */
	public void menuConfigActionPerformed(ActionEvent e) {
		QuestionPool qPool = jqEditor.getQuestionPool();
		configWindow.setValues(qPool.getDescriptionText(), 
				qPool.getIsChangeAllowed(),
				qPool.getIsExam(),
				qPool.getTimed(),
				qPool.getTimeLimit(),
				qPool.getDisplayFinalScore(),
				qPool.getDisplayPrintOption(),
				qPool.getNumberOfQuestions(),
				qPool.getStudentName());

		configWindow.setVisible(true);
	}

	/**
	 * Clear all fields on GUI.
	 */	
	public void clearDisplay() {
        if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.MULTI_CHOICE) {
	        multiChoicePanel.clearDisplay();
		} 
		else 
        if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.DRAG_N_DROP) {
            dndPanel.clearDisplay();
        }
	}
	
	public void setFieldsEditable(boolean b) {
        if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.MULTI_CHOICE) {
        	multiChoicePanel.setFieldsEditable(b);
		}
		else 
        if (currentlyDisplayedQuestion.getQuestionType() == QuestionType.DRAG_N_DROP) {
        	dndPanel.setFieldsEditable(b);
		}
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
	

	public JQuestionsEditor getEditor() {
		return jqEditor;
	}
			
	private void createMenuBar() {
		menuBar1 = new JMenuBar();
		menu1 = new JMenu();
		menuFileNew = new JMenuItem();
		menuFileOpen = new JMenuItem();
		menuFileOpenAndAppend = new JMenuItem();
		menuFileSave = new JMenuItem();
		menuFileSaveMarked = new JMenuItem();
		menuFileClose = new JMenuItem();
		menuDeleteMarked = new JMenuItem();
		menuInsertImage = new JMenuItem();
		menuConfig = new JMenuItem();
		menuExit = new JMenuItem();

		// ======== menuBar1 ========

		// ======== menu1 ========
		menu1.setText("File");


		// ---- menuFileNew ----
		menuFileNew.setText("File New");
		menuFileNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFileNewActionPerformed(e);
			}
		});
		menu1.add(menuFileNew);
		
		// ---- menuFileOpen ----
		menuFileOpen.setText("File Open");
		menuFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFileOpenActionPerformed(e, false);
			}
		});
		menu1.add(menuFileOpen);

		// ---- menuFileOpenAndAppend ----
		menuFileOpenAndAppend.setText("File Open and Append");
		menuFileOpenAndAppend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFileOpenActionPerformed(e, true);
			}
		});
		menu1.add(menuFileOpenAndAppend);

				
		// ---- menuFileSave ----
		menuFileSave.setText("File Save");
		menuFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFileSaveActionPerformed(e);
			}
		});
		menu1.add(menuFileSave);

		// ---- menuFileSaveMarked ----
		menuFileSaveMarked.setText("File Save Marked Questions");
		menuFileSaveMarked.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFileSaveMarkedActionPerformed(e);
			}
		});
		menu1.add(menuFileSaveMarked);
		
		// ---- menuFileClose ----
		menuFileClose.setText("File Close");
		menuFileClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFileCloseActionPerformed(e);
			}
		});
		menu1.add(menuFileClose);
		

		// ---- menuDeleteMarked ----
		menuDeleteMarked.setText("Delete Marked Questions");
		menuDeleteMarked.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuDeleteMarkedActionPerformed(e);
			}
		});
		menu1.add(menuDeleteMarked);
		
		
		// ---- menuInsertImage ----
		menuInsertImage.setText("Insert Image");
		menuInsertImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuInsertImageActionPerformed(e);
			}
		});
		menu1.add(menuInsertImage);

		// ---- menuConfig ----
		menuConfig.setText("Define Configuration");
		menuConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuConfigActionPerformed(e);
			}
		});
		menu1.add(menuConfig);

		// ---- menuExit ----
		menuExit.setText("Exit");
		menuExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuExitActionPerformed(e);
			}
		});
		menu1.add(menuExit);
		menuBar1.add(menu1);

		menu2 = new JMenu();
		menuAbout = new JMenuItem();

		// ======== menu2 ========
		menu2.setText("Help");

		// ---- menuAbout ----
		menuAbout.setText("About JQuestions Editor");
		menuAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAboutActionPerformed(e);
			}
		});
		menu2.add(menuAbout);

		
		menuBar1.add(menu2);
	}
	
	public JPanel createBlankPanel() {
		JPanel blankPanel = new JPanel();
		blankPanel.setBackground(Color.LIGHT_GRAY);
		return blankPanel;
	}
	
	public JPanel createNorthPanel() {
	    northPanel = new JPanel(new GridBagLayout());
		countLabel = new JLabel();
	    fileNameLabel = new JLabel();
	    markQuestionBox = new JCheckBox();

	    
		//---- fileNameLabel ----
		fileNameLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        
		// ---- countLabel ----
		countLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		countLabel.setText("Question 0 of 0");

		// ---- markQuestionBox ----
		markQuestionBox = new JCheckBox("Mark Question");
				
		//                                                    x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		northPanel.add(fileNameLabel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
		northPanel.add(markQuestionBox, new GridBagConstraints( 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
		northPanel.add(countLabel, new GridBagConstraints( 2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
			
		
        return northPanel;	    
	}
	
	public JPanel createSouthPanel() {
	    southPanel = new JPanel(new GridBagLayout());
        previousButton = new JButton();
        nextButton = new JButton();
        newButton = new JButton();
        deleteButton = new JButton();
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
	
		
		//---- newButton ----
		newButton.setText("New");
		newButton.setToolTipText("<html>Create a new Question record.</html>");
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newButtonActionPerformed(e);
			}
		});
		
		
		//---- deleteButton ----
		deleteButton.setText("Delete");
		deleteButton.setToolTipText("<html>Delete the current Question record.</html>");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteButtonActionPerformed(e);
			}
		});

		
		//---- hintButton ----
		hintButton.setText("Hint");
		hintButton.setToolTipText("<html>Add hint-text to current Question record.</html>");
		hintButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hintButtonActionPerformed(e);
			}
		});
		

		//---- explainButton ----
		explainButton.setText("Explain");
		explainButton.setToolTipText("<html>Add answer and explanation text <br>to current Question record.</html>");
		explainButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				explainButtonActionPerformed(e);
			}
		});

		//                                                     x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		southPanel.add(previousButton, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );	
		southPanel.add(nextButton, new GridBagConstraints( 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );
		southPanel.add(newButton, new GridBagConstraints( 2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );
		southPanel.add(deleteButton, new GridBagConstraints( 3, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );		
		southPanel.add(hintButton, new GridBagConstraints( 4, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );
		southPanel.add(explainButton, new GridBagConstraints( 5, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );		
				

	    return southPanel;
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

        northPanel = createNorthPanel();
        innerCardLayout = new CardLayout();        
        innerCardPanel = new JPanel(innerCardLayout);
        southPanel = createSouthPanel();                
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(northPanel, BorderLayout.NORTH);
        contentPanel.add(innerCardPanel, BorderLayout.CENTER);
        contentPanel.add(southPanel, BorderLayout.SOUTH);
        
        outerCardPanel.add(blankPanel, "blankPanel");
        outerCardPanel.add(contentPanel, "contentPanel");

        multiChoicePanel = new MultiChoiceQuestionScreen(northPanel, southPanel);
        dndPanel = new DragAndDropQuestionScreen(northPanel, southPanel);
    
        innerCardPanel.add(multiChoicePanel, "multiChoicePanel");
        innerCardPanel.add(dndPanel, "dndPanel");   
       
	}	
}
