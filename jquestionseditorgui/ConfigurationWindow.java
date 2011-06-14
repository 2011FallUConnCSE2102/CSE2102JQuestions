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
import javax.swing.*;
import javax.swing.border.*;

import question.QuestionPool;

import jquestionseditor.JQuestionsEditor;


/**
 * This class defines a Configuration window which is the means 
 * by which various configuration settings are set for a pool
 * of questions.
 * 
 * These include: isExamMode, is there a time limit, number of 
 * questions from the pool to answer, can the user change these
 * settings at run time, etc.
 * 
 * The class also hold references to the main GUI, the application
 * itself and also the current question pool. 
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see JQuestionsEditor
 * 
 */
@SuppressWarnings("serial")
public class ConfigurationWindow extends JDialog { 

	// The main GUI
    private JQuestionsEditorGUI jqEditorGUI;
    
    // The JQuestionsEditor application
    private JQuestionsEditor jqEditor;
    
    // The current Question Pool.
	private QuestionPool qPool;
	
	/**
	 * If there is no parent then just initialise the
	 * settings to default values.
	 * 
	 */ 
	public ConfigurationWindow() {
		initComponents();
	}
	
	/**
	 * If a parent exists then read the existing settings.
	 */ 	
	public ConfigurationWindow(Object parent) {
		qPool = null;
		
		initComponents();		
		
		// Get the Question Pool.
		if (parent instanceof JQuestionsEditorGUI) {
			   jqEditorGUI = (JQuestionsEditorGUI) parent;
			   jqEditor = jqEditorGUI.getEditor();
			   qPool = jqEditor.getQuestionPool();
		       
		       // Make description text editable for the Editor program.
		       descriptionTextPane.setEditable(true);
		}

		// Set the values for the Conf Window. 
        setValues(qPool.getDescriptionText(),
        	   qPool.getIsChangeAllowed(),	
               qPool.getIsExam(),
               qPool.getTimed(),
               qPool.getTimeLimit(),
               qPool.getDisplayFinalScore(),
               qPool.getDisplayPrintOption(),
               qPool.getNumberOfQuestions(),
               qPool.getStudentName());
	}

	
	/*
	 * General set values method.
	 */
	public void setValues(String descText, Boolean isChangeAllowed, Boolean isExam, Boolean isTimed, 
			            Integer timeLimit, Boolean displayFinalScore, 
			            Boolean displayPrintOption, Integer nQuestions, String studentName) {
		this.setDescriptionText(descText);
		this.setIsChangeAllowed(isChangeAllowed); 
		this.setIsExam(isExam);
		this.setTimed(isTimed);
		this.setTimeLimit(timeLimit);
		this.setDisplayFinalScore(displayFinalScore);
		this.setDisplayPrintOption(displayPrintOption);
		this.setNumberOfQuestions(nQuestions);
	}

	/*
	 * If 'OK' button is pressed then save the settings.
	 */
	private void okButtonActionPerformed(ActionEvent e) {
		// Get the Question Pool instance.
	    qPool = jqEditor.getQuestionPool();

		// Save the values from the Conf Window back to the Question Pool.
	    qPool.setDescriptionText(this.getDescriptionText());
	    qPool.setIsChangeAllowed(this.getIsChangeAllowed());
        qPool.setIsExam(this.getIsExam());
        qPool.setTimed(this.isTimed());
        qPool.setTimeLimit(this.getTimeLimit());
        qPool.setDisplayFinalScore(this.isDisplayFinalScore());
        qPool.setDisplayPrintOption(this.isDisplayPrintOption());
        qPool.setNumberOfQuestions(this.getNumberOfQuestions());
		
		setVisible(false);
	}

	
	private void cancelButtonActionPerformed(ActionEvent e) {
		// Close window but don't save anything.
		setVisible(false);
	}

	/*
	 * Mainly getters and setters from here.
	 */
	public String getDescriptionText() {
	    return descriptionTextPane.getText();
	}
	
	public void setDescriptionText(String descText) {
		descriptionTextPane.setText(descText);
		descriptionTextPane.setCaretPosition(0);
	}

	public Boolean getIsChangeAllowed() {
		if (yesChangeButton.isSelected()) {
			return new Boolean(true);
		}
		else {
		    return new Boolean(false);	
		}
	}
	
	public void setIsChangeAllowed(Boolean isExam) {
    	yesChangeButton.setSelected(isExam);
        noChangeButton.setSelected(!isExam);
	}
	
	public Boolean getIsExam() {
		if (examButton.isSelected()) {
			return new Boolean(true);
		}
		else {
		    return new Boolean(false);	
		}
	}
	
	public void setIsExam(Boolean isExam) {
    	examButton.setSelected(isExam);
        quizButton.setSelected(!isExam);
	}
	
    public Boolean isTimed() {
		if (timedButton.isSelected()) {
			return new Boolean(true);
		}
		else {
		    return new Boolean(false);	
		}
    }

    public void setTimed(Boolean b) {
    	timedButton.setSelected(b);
    	noTimedButton.setSelected(!b);
    	
    	if (b) {
    		minutesSpinner.setEnabled(true);
			label1.setEnabled(true);
    	}
    	else {
    		minutesSpinner.setEnabled(false);
			label1.setEnabled(false);
    	}
    }
    
    public Boolean isDisplayFinalScore() {    	
		if (finalScoreYesButton.isSelected()) {
			return new Boolean(true);
		}
		else {
		    return new Boolean(false);	
		}
    }

    public void setDisplayFinalScore(Boolean b) {
    	finalScoreYesButton.setSelected(b);
    	finalScoreNoButton.setSelected(!b);    	
    }
    
    public Boolean isDisplayPrintOption() {    	
		if (printOptionYesButton.isSelected()) {
			return new Boolean(true);
		}
		else {
		    return new Boolean(false);	
		}
    }

    public void setDisplayPrintOption(Boolean b) {
    	printOptionYesButton.setSelected(b);
    	printOptionNoButton.setSelected(!b);    	
    }
          
    public void setTimeLimit(Integer timeLimit) {
    	minutesSpinner.setValue(timeLimit);
    }
    
    public Integer getTimeLimit() {
    	Integer mins = (Integer) minutesSpinner.getValue();
    	return mins;
    }
        
    public void setNumberOfQuestions(Integer nQuestions) {
    	questionsSpinner.setValue(nQuestions);
    }
    
    public Integer getNumberOfQuestions() {
    	Integer nQuestions = (Integer) questionsSpinner.getValue();
    	return nQuestions;
    }
    
	private void timedButtonItemStateChanged(ItemEvent e) {
		if (timedButton.isSelected()) {
			minutesSpinner.setEnabled(true);
			label1.setEnabled(true);
		}
		else {
			minutesSpinner.setEnabled(false);
			label1.setEnabled(false);
		}
	}

	public void setVisible(boolean b) {
		updateContainsQsLabel();
		updateMaxQsLabel();
		super.setVisible(b);
	}
	
	private void updateContainsQsLabel() {
		int nQuestions = 0;
		// Get the Question Pool instance, if exists.
		if (jqEditor != null) {
	        qPool = jqEditor.getQuestionPool();
		}

		if (qPool != null) {
			nQuestions = qPool.getSize();
		}
		containsQsLabel.setText("This file contains " + nQuestions + " questions.");
	}
	
	private void updateMaxQsLabel() {
		int nQuestions = 0;
		// Get the Question Pool instance, if exists.
		if (jqEditor != null) {
	        qPool = jqEditor.getQuestionPool();
		}

		if (qPool != null) {
			nQuestions = qPool.getSize();
		}
		maxQsLabel.setText("(Max. " + nQuestions + ")");
	}
	
	/*
	 * Construct the user 'View', 
	 * i.e. user interface for the configuration window. 
	 */	
	private void initComponents() {
	
		descriptionPanel = new JPanel();
		descriptionScrollPane = new JScrollPane();
		descriptionTextPane = new JTextPane();
		allowChangePanel = new JPanel();
		allowChangeLabel = new JLabel();
		yesChangeButton = new JRadioButton();
		noChangeButton = new JRadioButton();
		modePanel = new JPanel();
		examButton = new JRadioButton();
		quizButton = new JRadioButton();
		timedPanel = new JPanel();
		noTimedButton = new JRadioButton();
		timedButton = new JRadioButton();
		minutesSpinner = new JSpinner();
		label1 = new JLabel();
		onCompletionPanel = new JPanel();
		displayFinalScoreLabel = new JLabel();
		label2 = new JLabel();
		finalScoreYesButton = new JRadioButton();
		finalScoreNoButton = new JRadioButton();
		printOptionYesButton = new JRadioButton();
		printOptionNoButton = new JRadioButton();
		saveButton = new JButton();
		cancelButton = new JButton();
		numberOfQuestionsPanel = new JPanel();
		containsQsLabel = new JLabel();
		howManyQuestionsLabel = new JLabel();
		questionsSpinner = new JSpinner();
		maxQsLabel = new JLabel();
		buttonPanel = new JPanel();
		
		
		// ======== this ========
		setTitle("Define Configuration Settings for Exam");
		setAlwaysOnTop(true);
		setModal(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		// ======== descriptionPanel ========
		{
			descriptionPanel.setBorder(new TitledBorder("Description"));
			descriptionPanel.setLayout(new GridBagLayout());

			// ======== descriptionScrollPane ========
			{

				// ---- decriptionTextPane ----
				descriptionTextPane.setEditable(false);
				descriptionTextPane.setText("description text");
				descriptionScrollPane.setViewportView(descriptionTextPane);
			}

			//                                                                  x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
			descriptionPanel.add(descriptionScrollPane, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
		}
		
		//                                                        x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		contentPane.add(descriptionPanel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
		

		// ======== allowChangePanel ========
		{
			allowChangePanel.setBorder(new TitledBorder("Allow User to Change These Configuration Settings ?"));
			allowChangePanel.setLayout(new GridBagLayout());

			allowChangeLabel.setText("Allow User to Change these Settings ?");
			allowChangePanel.add(allowChangeLabel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );
			
			// ---- yesChangeButton ----
			yesChangeButton.setText("Yes");
			allowChangePanel.add(yesChangeButton, new GridBagConstraints( 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );

			// ---- noChangeButton ----
			noChangeButton.setText("No");
			allowChangePanel.add(noChangeButton, new GridBagConstraints( 2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );		
		}		

		//                                                        x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		contentPane.add(allowChangePanel, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

		
		// ======== modePanel ========
		{
			modePanel.setBorder(new TitledBorder("Mode"));
			modePanel.setLayout(new GridBagLayout());

			// ---- examButton ----
			examButton
					.setText("Exam mode            (No Help available, No Feedback, No Hints, No Explanations available)");
			//                                             x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
			modePanel.add(examButton, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
			
			// ---- quizButton ----
			quizButton
					.setText("Quiz mode           (Help is available, Feedback given, Hints and Explanation text is available)");
			//                                                x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady						
			modePanel.add(quizButton, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
		}

		//                                                 x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		contentPane.add(modePanel, new GridBagConstraints( 0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

		// ======== timedPanel ========
		{
			timedPanel.setBorder(new TitledBorder("Timed"));
			timedPanel.setLayout(new GridBagLayout());

			// ---- noTimedButton ----
			noTimedButton.setText("No time limit");
			//                                              x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
			timedPanel.add(noTimedButton, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

			// ---- timedButton ----
			timedButton.setText("Timed ");
			timedButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					timedButtonItemStateChanged(e);
				}
			});

			//                                              x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
			timedPanel.add(timedButton, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

			// ---- label1 ----
			label1.setText("Set time limit (minutes)");
			timedPanel.add(label1, new GridBagConstraints( 1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 100, 0, 0 ), 0, 0 ) );	
			timedPanel.add(minutesSpinner, new GridBagConstraints( 2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 150 ), 0, 0 ) );	
		}

		//                                              x  y  w  h  wtx  wty  anchor                   fill                                 T   L   B   R padx pady			
		contentPane.add(timedPanel, new GridBagConstraints( 0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

		
		// ======== onCompletionPanel ========
		{
			onCompletionPanel.setBorder(new TitledBorder("On Completion"));
			onCompletionPanel.setLayout(new GridBagLayout());

			// ---- displayFinalScoreLabel ----
			displayFinalScoreLabel.setText("Display Final Score ?");

			//                                                         x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
			onCompletionPanel.add(displayFinalScoreLabel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

			// ---- label2 ----
			label2.setText("Allow 'Print' option ?");
			onCompletionPanel.add(label2, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
			
			// ---- finalScoreYesButton ----
			finalScoreYesButton.setText("Yes");
			onCompletionPanel.add(finalScoreYesButton, new GridBagConstraints( 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );

			// ---- finalScoreNoButton ----
			finalScoreNoButton.setText("No");
			onCompletionPanel.add(finalScoreNoButton, new GridBagConstraints( 2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );

			// ---- printOptionYesButton ----
			printOptionYesButton.setText("Yes");
			onCompletionPanel.add(printOptionYesButton, new GridBagConstraints( 1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );

			// ---- printOptionNoButton ----
			printOptionNoButton.setText("No");
			onCompletionPanel.add(printOptionNoButton, new GridBagConstraints( 2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );
		}

		//                                              x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		contentPane.add(onCompletionPanel, new GridBagConstraints( 0, 5, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

		// ======== numberOfQuestionsPanel ========
		{
			numberOfQuestionsPanel.setBorder(new TitledBorder("Number Of Questions"));
			numberOfQuestionsPanel.setLayout(new GridBagLayout());

			// ---- containsQsLabel ----
            updateContainsQsLabel();
            
			//                                         x  y  w  h  wtx  wty  anchor                   fill                                T   L   B   R padx pady			
			numberOfQuestionsPanel.add(containsQsLabel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

			// ---- howManyQuestionsLabel ----
			howManyQuestionsLabel.setText("How many questions to answer ? ");
			numberOfQuestionsPanel.add(howManyQuestionsLabel, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
			numberOfQuestionsPanel.add(questionsSpinner, new GridBagConstraints( 1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );

			// ---- maxLabel ----
			updateMaxQsLabel();
			numberOfQuestionsPanel.add(maxQsLabel, new GridBagConstraints( 2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );
		}
		
		//                                              x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		contentPane.add(numberOfQuestionsPanel, new GridBagConstraints( 0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
		
		// ======== buttonPanel ========
		{
			buttonPanel.setBorder(new EmptyBorder(5,5,5,5));
			buttonPanel.setLayout(new GridBagLayout());
		}

		// ---- saveButton ----
		saveButton.setText("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButtonActionPerformed(e);
			}
		});
		//                                                  x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		buttonPanel.add(saveButton, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );	
		
		// ---- cancelButton ----
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButtonActionPerformed(e);
			}
		});
		//                                                    x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		buttonPanel.add(cancelButton, new GridBagConstraints( 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );	
				
		//                                                   x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		contentPane.add(buttonPanel, new GridBagConstraints( 0, 6, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

		final int startWidth = 585;
		final int startHeight = 600;
		final Dimension startSize = new Dimension(startWidth, startHeight); 
		contentPane.setMinimumSize(startSize);
		contentPane.setPreferredSize(startSize);
		
		pack();
		setLocationRelativeTo(getOwner());

		// ---- buttonGroup1 ----
		ButtonGroup buttonGroup0 = new ButtonGroup();
		buttonGroup0.add(yesChangeButton);
		buttonGroup0.add(noChangeButton);		
		
		// ---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(examButton);
		buttonGroup1.add(quizButton);

		// ---- buttonGroup2 ----
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(noTimedButton);
		buttonGroup2.add(timedButton);

		// ---- buttonGroup3 ----
		ButtonGroup buttonGroup3 = new ButtonGroup();
		buttonGroup3.add(finalScoreYesButton);
		buttonGroup3.add(finalScoreNoButton);

		// ---- buttonGroup4 ----
		ButtonGroup buttonGroup4 = new ButtonGroup();
		buttonGroup4.add(printOptionYesButton);
		buttonGroup4.add(printOptionNoButton);
	}

	private JPanel descriptionPanel;
	private JScrollPane descriptionScrollPane;
	private JTextPane descriptionTextPane;
	private JPanel allowChangePanel;
	private JLabel allowChangeLabel;
	private JRadioButton yesChangeButton;
	private JRadioButton noChangeButton;	
	private JPanel modePanel;
	private JRadioButton examButton;
	private JRadioButton quizButton;
	private JPanel timedPanel;
	private JRadioButton noTimedButton;
	private JRadioButton timedButton;
	private JSpinner minutesSpinner;
	private JLabel label1;
	private JPanel onCompletionPanel;
	private JLabel displayFinalScoreLabel;
	private JLabel label2;
	private JRadioButton finalScoreYesButton;
	private JRadioButton finalScoreNoButton;
	private JRadioButton printOptionYesButton;
	private JRadioButton printOptionNoButton;
	private JButton saveButton;
	private JButton cancelButton;
	private JPanel numberOfQuestionsPanel;
	private JLabel containsQsLabel;
	private JLabel howManyQuestionsLabel;
	private JSpinner questionsSpinner;
	private JLabel maxQsLabel;
	private JPanel buttonPanel;
}
