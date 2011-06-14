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


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import question.QuestionPool;

import jquestions.JQuestions;
import jquestionsgui.JQuestionsGUI;

/**
 * This class defines a Configuration window which is the means 
 * by which various configuration settings are displayed and 
 * may (optionally) be set for a pool of questions.
 * 
 * These include: isExamMode, is there a time limit, number of 
 * questions from the pool to answer, can the user change these
 * settings at run time, etc.
 * 
 * The configuration settings for the exam may or may not be 
 * displayed to the user at exam-time, as defined by the exam creator
 * in the JQuestionsEditor. If the user may change the configuration
 * settings then the configuration window appears when the user 
 * starts the exam - if not then the user goes straight to the
 * exam. 
 * 
 * The class also hold references to the main GUI, the application
 * itself and also the current question pool. 
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see JQuestions
 * 
 */

@SuppressWarnings("serial")
public class ConfigurationWindow extends JDialog { 

	// The main GUI
	private JQuestionsGUI jqGUI;
	
    // The JQuestions application	
	private JQuestions jQuestions;
	
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
	 * This does not necessarily mean display them to user. 
	 */ 	
	public ConfigurationWindow(Object parent) {
		qPool = null;
		
		initComponents();		

		// Get the Question Pool.
		if (parent instanceof JQuestionsGUI) {
			   jqGUI = (JQuestionsGUI) parent;
			   jQuestions = jqGUI.getJQuestions();
			   qPool = jQuestions.getQuestionPool();
		}

		// Set the values for the Conf Window. 
        setValues(qPool.getDescriptionText(), 
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
	public void setValues(String descText, Boolean isExam, Boolean isTimed, 
			Integer timeLimit, Boolean displayFinalScore, 
			Boolean displayPrintOption, Integer nQuestions, String studentName) {
		this.setDescriptionText(descText);
		this.setIsExam(isExam);
		this.setTimed(isTimed);
		this.setTimeLimit(timeLimit);
		this.setDisplayFinalScore(displayFinalScore);
		this.setDisplayPrintOption(displayPrintOption);
		this.setNumberOfQuestions(nQuestions);
		this.setStudentName(studentName);
		
        containsQsLabel.setText("This file contains " + nQuestions + " questions.");
        maxQsLabel.setText("(Max. " + nQuestions + ")");
	}

	/*
	 * If 'OK' button is pressed then save the settings.
	 */	
	private void okButtonActionPerformed(ActionEvent e) {
		// Get the Question Pool instance.
	    qPool = jQuestions.getQuestionPool();
		
	    jqGUI.setPressedOK(true);

		// Save the values from the Conf Window back to the Question Pool.
	    qPool.setDescriptionText(this.getDescriptionText());
        qPool.setIsExam(this.getIsExam());
        qPool.setTimed(this.isTimed());
        qPool.setDisplayFinalScore(this.isDisplayFinalScore());
        qPool.setDisplayPrintOption(this.isDisplayPrintOption());
        qPool.setStudentName(this.getStudentName());        
        
        
		int nQuestions = this.getNumberOfQuestions();
		int size = qPool.getSize();
		if ((nQuestions >= size) || (nQuestions < 0)) {
			nQuestions = size;
		}        
        qPool.setNumberOfQuestions(nQuestions);
			
        
        int timeLimit = this.getTimeLimit();
        if (timeLimit < 1) {
        	timeLimit = 1;
        }
        qPool.setTimeLimit(timeLimit);
        
		
		setVisible(false);
	}

	
	private void cancelButtonActionPerformed(ActionEvent e) {
		// Close window and save no changes (if any). 
		setVisible(false);
	}

	
	/**
	 * Mainly getters and setters from here.
	 */
	
	public String getDescriptionText() {
	    return descriptionTextPane.getText();
	}
	
	public void setDescriptionText(String descText) {
		descriptionTextPane.setText(descText);
		descriptionTextPane.setCaretPosition(0);
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
    	}
    	else {
    		minutesSpinner.setEnabled(false);
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
      
    public String getStudentName() {
    	return studentNameField.getText(); 
    }    

    public void setStudentName(String s) {
    	studentNameField.setText(s); 
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
		if (jQuestions != null) {
	        qPool = jQuestions.getQuestionPool();
		}

		if (qPool != null) {
			nQuestions = qPool.getSize();
		}
		containsQsLabel.setText("This file contains " + nQuestions + " questions.");

	}
	
	private void updateMaxQsLabel() {
		int nQuestions = 0;

		// Get the Question Pool instance, if exists.
		if (jQuestions != null) {
	        qPool = jQuestions.getQuestionPool();
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
		panel1 = new JPanel();
		descriptionScrollPane = new JScrollPane();
		descriptionTextPane = new JTextPane();
		panel2 = new JPanel();
		examButton = new JRadioButton();
		quizButton = new JRadioButton();
		panel3 = new JPanel();
		noTimedButton = new JRadioButton();
		timedButton = new JRadioButton();
		minutesSpinner = new JSpinner();
		label1 = new JLabel();
		panel4 = new JPanel();
		displayFinalScoreLabel = new JLabel();
		label2 = new JLabel();
		finalScoreYesButton = new JRadioButton();
		finalScoreNoButton = new JRadioButton();
		printOptionYesButton = new JRadioButton();
		printOptionNoButton = new JRadioButton();
		studentNameLabel = new JLabel();
		studentNameField = new JTextField();
		okButton = new JButton();
		cancelButton = new JButton();

		panel5 = new JPanel();
		containsQsLabel = new JLabel();
		howManyQuestionsLabel = new JLabel();
		questionsSpinner = new JSpinner();
		maxQsLabel = new JLabel();

		// ======== this ========
		setTitle("Configuration Settings");
		//setAlwaysOnTop(true);        
		setModal(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		// ======== panel1 ========
		{
			panel1.setBorder(new TitledBorder("Description"));								
			panel1
					.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
						public void propertyChange(
								java.beans.PropertyChangeEvent e) {
							if ("border".equals(e.getPropertyName()))
								throw new RuntimeException();
						}
					});

			panel1.setLayout(new GridBagLayout());

			// ======== descriptionScrollPane ========
			{
				// ---- decriptionTextPane ----
				descriptionTextPane.setEditable(false);
				descriptionTextPane.setText("description text");
				descriptionScrollPane.setViewportView(descriptionTextPane);
			}
			
			//                                                        x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
			panel1.add(descriptionScrollPane, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
		}

		//                                              x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		contentPane.add(panel1, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
				
		// ======== panel2 ========
		{
			panel2.setBorder(new TitledBorder("Mode"));
			panel2.setLayout(new GridBagLayout());

			// ---- examButton ----
			examButton
					.setText("Exam mode            (No Help available, No Feedback, No Hints, No Explanations available)");
			
			panel2.add(examButton, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

			// ---- quizButton ----
			quizButton
					.setText("Quiz mode           (Help is available, Feedback given, Hints and Explanation text is available)");
			
			//                                             x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady						
			panel2.add(quizButton, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

		}

		//                                              x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		contentPane.add(panel2, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

		// ======== panel3 ========
		{
			panel3.setBorder(new TitledBorder("Timed"));
			panel3.setLayout(new GridBagLayout());

			// ---- noTimedButton ----
			noTimedButton.setText("No time limit");
			
			//                                              x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
			panel3.add(noTimedButton, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

			// ---- timedButton ----
			timedButton.setText("Timed ");
			timedButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					timedButtonItemStateChanged(e);
				}
			});

			//                                              x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
			panel3.add(timedButton, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

			// ---- label1 ----
			label1.setText("Set time limit (minutes)");
			panel3.add(label1, new GridBagConstraints( 1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 100, 0, 0 ), 0, 0 ) );	
			panel3.add(minutesSpinner, new GridBagConstraints( 2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 150 ), 0, 0 ) );	

		}

		//                                              x  y  w  h  wtx  wty  anchor                   fill                                 T   L   B   R padx pady			
		contentPane.add(panel3, new GridBagConstraints( 0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

		// ======== panel4 ========
		{
			panel4.setBorder(new TitledBorder("On Completion"));
			panel4.setLayout(new GridBagLayout());

			// ---- displayFinalScoreLabel ----
			displayFinalScoreLabel.setText("Display Final Score ?");			
			//                                                         x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
			panel4.add(displayFinalScoreLabel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

			// ---- label2 ----
			label2.setText("Allow 'Print' option ?");
			panel4.add(label2, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

			// ---- finalScoreYesButton ----
			finalScoreYesButton.setText("Yes");
			panel4.add(finalScoreYesButton, new GridBagConstraints( 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );

			// ---- finalScoreNoButton ----
			finalScoreNoButton.setText("No");
			panel4.add(finalScoreNoButton, new GridBagConstraints( 2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );

			// ---- printOptionYesButton ----
			printOptionYesButton.setText("Yes");
			panel4.add(printOptionYesButton, new GridBagConstraints( 1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );

			// ---- printOptionNoButton ----
			printOptionNoButton.setText("No");
			panel4.add(printOptionNoButton, new GridBagConstraints( 2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );

		}

		//                                              x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		contentPane.add(panel4, new GridBagConstraints( 0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

		// ======== panel5 ========
		{
			panel5.setBorder(new TitledBorder("Number Of Questions"));
			panel5.setLayout(new GridBagLayout());

			// ---- containsQsLabel ----
			updateContainsQsLabel();

			//                                                  x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
			panel5.add(containsQsLabel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

			// ---- howManyQuestionsLabel ----
			howManyQuestionsLabel.setText("How many questions to answer ? ");
			panel5.add(howManyQuestionsLabel, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	
			panel5.add(questionsSpinner, new GridBagConstraints( 1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );
						
			// ---- maxLabel ----
			updateMaxQsLabel();
			panel5.add(maxQsLabel, new GridBagConstraints( 2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );
		}

		//                                              x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		contentPane.add(panel5, new GridBagConstraints( 0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

		// ======== buttonPanel ========
		buttonPanel = new JPanel();
		{
			buttonPanel.setBorder(new EmptyBorder(5,5,5,5));
			buttonPanel.setLayout(new GridBagLayout());
		}
	
		// ---- studentNameLabel ----
		studentNameLabel.setText("Student Name (optional):");
		//                                                        x  y  w  h  wtx  wty  anchor                   fill                                 T   L   B   R padx pady			
		buttonPanel.add(studentNameLabel, new GridBagConstraints( 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );	


		// ---- StudentNameField ----
		studentNameField.setText("Student Name");
		buttonPanel.add(studentNameField, new GridBagConstraints( 3, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );	

		
		// ---- okButton ----
		okButton.setText("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButtonActionPerformed(e);
			}
		});

		//                                                x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		buttonPanel.add(okButton, new GridBagConstraints( 1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );	
	
		
		// ---- cancelButton ----
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButtonActionPerformed(e);
			}
		});

		//                                                    x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		buttonPanel.add(cancelButton, new GridBagConstraints( 3, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 10, 10, 10,  5 ), 0, 0 ) );	

		//                                                   x  y  w  h  wtx  wty  anchor                   fill                                  T   L   B   R padx pady			
		contentPane.add(buttonPanel, new GridBagConstraints( 0, 5, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 5, 0,  5 ), 0, 0 ) );	

		final int startWidth = 585;
		final int startHeight = 600;
		final Dimension startSize = new Dimension(startWidth, startHeight); 
		contentPane.setMinimumSize(startSize);
		contentPane.setPreferredSize(startSize);
				
		pack();
		setLocationRelativeTo(getOwner());

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

	private JPanel panel1;
	private JScrollPane descriptionScrollPane;
	private JTextPane descriptionTextPane;
	private JPanel panel2;
	private JRadioButton examButton;
	private JRadioButton quizButton;
	private JPanel panel3;
	private JRadioButton noTimedButton;
	private JRadioButton timedButton;
	private JSpinner minutesSpinner;
	private JLabel label1;
	private JPanel panel4;
	private JLabel displayFinalScoreLabel;
	private JLabel label2;
	private JRadioButton finalScoreYesButton;
	private JRadioButton finalScoreNoButton;
	private JRadioButton printOptionYesButton;
	private JRadioButton printOptionNoButton;
	private JLabel studentNameLabel;
	private JTextField studentNameField;
	private JButton okButton;
	private JButton cancelButton;
	private JPanel panel5;
	private JPanel buttonPanel;
	private JLabel containsQsLabel;
	private JLabel howManyQuestionsLabel;
	private JSpinner questionsSpinner;
	private JLabel maxQsLabel;
}
