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
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * This class holds all the details and methods for the 
 * Report Summary window. This is the window displayed
 * once the user has answered the last question, or the 
 * timer has timed-out.
 * 
 * It performs an evaluation of the correctness of all the 
 * users answers and the updates itself, its icons and percentage
 * score fields accordingly. 
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see JQuestionsGUI
 * 
 */
public class ReportSummaryWindow extends JDialog implements Printable { 

	// Reference to main GUI
	private JQuestionsGUI jqg;
	
	// The list of currrent user answers. 
    private ArrayList<UserAnswer> userAnswers;
    private ArrayList<Integer> questionIds;
	private int questionsToAnswer = 0;

	// ImageIcons
	private ImageIcon greenTick; 
	private ImageIcon redCross; 

	// Main GUI components.
	private JPanel detailsPanel;
	private JLabel nQuestionsLabel, correctLabel, scoreLabel;
	private JScrollPane reportScrollPane;
	private JPanel reportPanel;
	private JLabel[] qLabel;
	private JLabel[] aLabel;
	private JButton exitButton, returnToQuestionsButton, printButton;
	private JLabel studentNameLabel, startLabel, finishLabel, 
	               fileNameLabel, modeLabel, timeLabel;

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor sets values of variables, builds components,
	 * and displays appropriate buttons depending oon configuration
	 * settings.
	 */
	public ReportSummaryWindow(JQuestionsGUI parent) {
		jqg = parent;
			
		userAnswers = jqg.getUserAnswers();
        questionIds = jqg.getQuestionIds();

		questionsToAnswer = questionIds.size();
        
    	greenTick = ImageLibrary.getGreenTickIcon();
    	redCross = ImageLibrary.getRedCrossIcon();        
 
		initComponents();
		
		// Some components can be set now.
		nQuestionsLabel.setText("Number Of Questions: " + questionsToAnswer);
        studentNameLabel.setText("Name: " + jqg.getStudentName());
        startLabel.setText("Start: " + jqg.getStartTime());
        
		// Display 'Print' button, or not ?
		if (jqg.getDisplayPrintOption() == true) {
			printButton.setVisible(true);
		}
		else {
			printButton.setVisible(false);
		}
		
		// Display mode.
		if (jqg.getIsExam()) {
			modeLabel.setText("Mode: EXAM");
		}
		else {
			modeLabel.setText("Mode: QUIZ");
		}
		
		fileNameLabel.setText("File: " + jqg.getFileName());
		
		if (jqg.getTimed()) {
		    timeLabel.setText("Time Limit: " + jqg.getTimeLimit() + " minutes");
		}
		else {
			timeLabel.setText("Time Limit: No time limit");
		}	
		
		if (jqg.isApplet) {
			exitButton.setEnabled(false);
		}
	}


	/**
	 * Exit the JQuestions application.
	 * 
	 * @param e
	 */
	private void exitButtonActionPerformed(ActionEvent e) {
		System.exit(0);
	}

	/**
	 * This button allows the user to close the Report window and return 
	 * to the questions, perhaps to edit their answers. The Report 
	 * window will need to perform an evaluation the next time it displays.
	 *   
	 * @param e
	 */	
	private void returnToQuestionsButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}

    /**
	  * This is the "callback" method that the PrinterJob will invoke.
	  * This method is defined by the Printable interface.
	  */
	public int print(Graphics g, PageFormat format, int pagenum) {
	    // The PrinterJob will keep trying to print pages until we return
	    // this value to tell it that it has reached the end.
	    if (pagenum > 0) 
	      return Printable.NO_SUCH_PAGE;

	    // We're passed a Graphics object, but it can always be cast to Graphics2D
	    Graphics2D g2 = (Graphics2D) g;

	    // Use the top and left margins specified in the PageFormat Note
	    // that the PageFormat methods are poorly named.  They specify
	    // margins, not the actual imageable area of the printer.
	    g2.translate(format.getImageableX(), format.getImageableY());

	    // Tell the component to draw itself to the printer by passing in 
	    // the Graphics2D object.  This will not work well if the component
	    // has double-buffering enabled.
	    paint(g2);

	    // Return this constant to tell the PrinterJob that we printed the page.
	    return Printable.PAGE_EXISTS;
	}	
	
	/**
	 * Allow the user to print the Report window as a record of their score.
	 * NOTE: This prints the whole window, not just the 'visible area' but
	 * also parts off-screen (i.e. scrolled off screen) and not visible with 
	 * the current viewport.
	 * 
	 *   @param e
	 */
	private void printButtonActionPerformed(ActionEvent e) {

		// Get the PrinterJob object
		PrinterJob job = PrinterJob.getPrinterJob();
		// Get the default page format, then allow the user to modify it
		PageFormat format = job.pageDialog(job.defaultPage());
		// Tell the PrinterJob what to print
		job.setPrintable(this, format);
		// Ask the user to confirm, and then begin the printing process
		if (job.printDialog())
		  try {
			  job.print();
		  } catch (PrinterException e1) {
			  e1.printStackTrace();
		  }
	}

	/**
	 * Update display just prior to displaying the Report Window. 
	 * Hence the recalculation of results is performed here.
	 */
	public void updateDisplay() {
						
		// The time 'now'... finish time ??
		finishLabel.setText("Finish: " + new Date());
		
		// Recalculate results.
        int countCorrect = 0;
        for (int i=0; i<questionsToAnswer; i++) {        	
        	int index = questionIds.get(i);
        	UserAnswer answer = userAnswers.get(index);        	
        	
        	if (answer.isCorrect()) {      		
        		aLabel[i].setIcon(greenTick);
        		countCorrect++;
        	}
        	else {   /* if answer is wrong */
        		aLabel[i].setIcon(redCross);
        	}		        	
        }	

        // Calculate percentage correct.
        double percentageScore = 100.0 * ((double) countCorrect 
                       		                   / (double) questionsToAnswer);
        
		// Display 'Score', or not ?
		if (jqg.getDisplayFinalScore() == true) {
	        correctLabel.setText("Correct Answers: " + countCorrect);
	        scoreLabel.setText("Score: " + (int) percentageScore + "%");
            correctLabel.setVisible(true);
            scoreLabel.setVisible(true);
		}
		else {
            correctLabel.setVisible(false);
            scoreLabel.setVisible(false);
		}              
	}
	
	
	/**
	 * Construct main UI components.
	 */
	private void initComponents() {
	
	    detailsPanel = new JPanel(new GridBagLayout());
		reportScrollPane = new JScrollPane();
		reportPanel = new JPanel(new GridBagLayout());
        qLabel = new JLabel[questionsToAnswer]; 
        aLabel = new JLabel[questionsToAnswer];

		exitButton = new JButton();
		printButton = new JButton();
		returnToQuestionsButton = new JButton();

		//======== this ========
		setTitle("JQuestions - Report Summary");
		setModal(true);
		setName("ReportSummaryDialog");
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		
		//======== scorePanel ========

		detailsPanel.setBackground(Color.WHITE);
		detailsPanel.setLayout(new GridBagLayout());

		studentNameLabel = new JLabel("Name: Student Name");
		startLabel = new JLabel("Start: ");
		finishLabel = new JLabel("Finish: ");

		//                                                         x  y  w  h  wtx  wty  anchor                   fill                              T   L   B   R padx pady
		detailsPanel.add(studentNameLabel, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );		
		detailsPanel.add(startLabel, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );		
		detailsPanel.add(finishLabel, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );		

		fileNameLabel = new JLabel("File: ");
		modeLabel = new JLabel("Mode: ");		
		timeLabel = new JLabel("Time Limit: ");

		//                                                      x  y  w  h  wtx  wty  anchor                   fill                              T   L   B   R padx pady
		detailsPanel.add(fileNameLabel, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );		
		detailsPanel.add(modeLabel, new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );		
		detailsPanel.add(timeLabel, new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );		
		
		
		nQuestionsLabel = new JLabel("Number Of Questions: ");	
        correctLabel = new JLabel("Correct Answers: ");
		scoreLabel = new JLabel("Score: ");

		//                                                        x  y  w  h  wtx  wty  anchor                   fill                              T   L   B   R padx pady
		detailsPanel.add(nQuestionsLabel, new GridBagConstraints( 2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );		
		detailsPanel.add(correctLabel, new GridBagConstraints( 2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );		
		detailsPanel.add(scoreLabel, new GridBagConstraints( 2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );		


		//======== reportScrollPane ========

		//======== reportPanel ========
		reportPanel.setLayout(new GridBagLayout());
		reportPanel.setBackground(Color.LIGHT_GRAY);				

		int x = 0, y = 0;

		
		/**
		 * Layout the ImageIcons carefully because there might potentially 
		 * be a large number of them (hundreds ??). 
		 */		
		for (int qCount=0; qCount<questionsToAnswer; qCount++) {
			qLabel[qCount] = new JLabel("Q. " + (qCount+1));
			aLabel[qCount] = new JLabel(redCross);
			aLabel[qCount].setHorizontalAlignment(SwingConstants.CENTER);
			//                                                      x  y  w  h  wtx  wty  anchor                   fill                              T   L   B   R padx pady
			reportPanel.add(qLabel[qCount], new GridBagConstraints( x*2, y, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );
			reportPanel.add(aLabel[qCount], new GridBagConstraints( x*2+1, y, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );

			// Layout in rows of 10 wide.
			x = x + 1;
			if ((x % 10) == 0) {
				x = 0;
				y++;
			}	    			
		}				
		reportScrollPane.setViewportView(reportPanel);

		contentPane.add(detailsPanel, BorderLayout.NORTH);
		JPanel buttonsPanel = new JPanel(new GridBagLayout());
		contentPane.add(buttonsPanel, BorderLayout.SOUTH);
		contentPane.add(reportScrollPane, BorderLayout.CENTER);

		//---- printButton ----
		printButton.setText("Print");
		printButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printButtonActionPerformed(e);
			}
		});

		//                                                    x  y  w  h  wtx  wty  anchor                   fill                              T   L   B   R padx pady
		buttonsPanel.add(printButton, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 20, 0 ) );		
		
				
		//---- returnToQuestionsButton ----
		returnToQuestionsButton.setText("Return to Questions");
		returnToQuestionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				returnToQuestionsButtonActionPerformed(e);
			}
		});
		//                                                    x  y  w  h  wtx  wty  anchor                   fill                              T   L   B   R padx pady
		buttonsPanel.add(returnToQuestionsButton, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );		

		//---- exitButton ----
		exitButton.setText("Exit");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitButtonActionPerformed(e);
			}
		});

		//                                                   x  y  w  h  wtx  wty  anchor                   fill                              T   L   B   R padx pady
		buttonsPanel.add(exitButton, new GridBagConstraints( 2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 20, 0 ) );		
			
		//                                 w    h
		Dimension winSize = new Dimension(650, 250);
		contentPane.setMinimumSize(winSize);
		contentPane.setPreferredSize(winSize); 		
		pack();
		setLocationRelativeTo(getOwner());
	}
}
