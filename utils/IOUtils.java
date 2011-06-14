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


package utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;


import question.PossibleAnswer;
import question.Question;
import question.QuestionPool;
import question.QuestionType;

// IOUtils
//-----------------------------------------------------------------------
/**
 * <p>IOUtils instances should NOT be constructed in standard programming.
 * The methods have been declared as <code>static</code> therefore, the class 
 * should be used as <code>IOUtils.readFile........
 * </code>.</p>
 * 
 * <p>File of general utilities for input/output.</p>
 * <ul>
 * <li>This code is used by both JQuestions and the JQuestions Editor
 *     applications. As such, I thought it best to put these functions
 *     into a separate utils class where they can be called by either.
 * <li>It also makes it easier to deal with changes to the JQuestions 
 *     file format, if needed.
 * <li>Throw exceptions where necessary.
 * </ul>
 *
 * <p>Each method documents its behaviour in more detail.</p>
 *
 * @author Ken Williams  <jquestions@truerandomness.com>
 *
 */

public class IOUtils {

	
	// Save the file format version in the file itself, so in the future
    // we can still read files of old versions.
    private static final String FILE_FORMAT_VERSION = "0.9";

	// The standard ASCII delimiter used between fields in file format.
    private static final Character UNIT_SEPARATOR = '\u001F';
    private static final String DELIM = UNIT_SEPARATOR.toString();
    
    // Empty field marker. 
    private static final Character nsm = Character.NON_SPACING_MARK;
    private static final String EMPTY_FIELD = nsm.toString();    	
    
    
    /**
     * <p>This constructor is public and only exists to permit tools that require 
     * a JavaBean instance to operate.</p>
     */
    public IOUtils() {
        super();
    }
    
    /**
     * Write the current pool of questions and configuration details 
     * to a file of the given name.
     * 
     * @param fileName - File to write to.
     * @param qPool - The currrent question pool.
     * @param writeMarkedOnly - Boolean value, only write marked questions ?
     */
    // writeFile
    //-----------------------------------------------------------------------
	public static void writeFile(final String fileName, 
			              final QuestionPool qPool, boolean writeMarkedOnly) {
		
		String descriptionText = qPool.getDescriptionText();
		Boolean isChangeAllowed = qPool.getIsChangeAllowed();
		Boolean isExam = qPool.getIsExam();
		Boolean timed = qPool.getTimed();
        Integer timeLimit = qPool.getTimeLimit(); 		
		Boolean displayFinalScore = qPool.getDisplayFinalScore();
		Boolean displayPrintOption = qPool.getDisplayPrintOption();
		String studentName = qPool.getStudentName();
        Integer numberOfQuestions = qPool.getNumberOfQuestions();
                        
        try {
            FileOutputStream fos = new FileOutputStream(fileName);// Save to file
            GZIPOutputStream gzos = new GZIPOutputStream(fos);    // Compressed
            OutputStreamWriter osw = new OutputStreamWriter(gzos);    
            BufferedWriter os = new BufferedWriter(osw);  // Buffered
            
            // Write format version number as part of file header.            
        	os.write(FILE_FORMAT_VERSION + DELIM);

        	// Write recommended Configuration settings
        	String descText = descriptionText;
        	if ((descText == null) || (descText.length() == 0)) {
        		descText = EMPTY_FIELD;
        	}
        	os.write(descText + DELIM);

        	if (isChangeAllowed == true) {
				os.write("T" + DELIM);
			} else {
				os.write("F" + DELIM);
			}        	

			if (isExam == true) {
				os.write("T" + DELIM);
			} else {
				os.write("F" + DELIM);
			}        	

			if (timed == true) {
				os.write("T" + DELIM);
			} else {
				os.write("F" + DELIM);
			}        	

			os.write(timeLimit + DELIM);

			if (displayFinalScore == true) {
				os.write("T" + DELIM);
			} else {
				os.write("F" + DELIM);
			}        	

			if (displayPrintOption == true) {
				os.write("T" + DELIM);
			} else {
				os.write("F" + DELIM);
			}        	

        	String nameText = studentName;
        	if ((nameText == null) || (nameText.length() == 0)) {
        		nameText = EMPTY_FIELD;
        	}
        	os.write(nameText + DELIM);
			
        	
			os.write(numberOfQuestions + DELIM);
        	
        	
            // For each question, write all fields data.
            for (int i=0; i < qPool.getSize(); i++) {
            	Question q = qPool.getQuestion(i);
            
            	// Are we only saving marked questions?
            	if ((writeMarkedOnly) && (q.isMarked() == false)) {
            		continue;
            	}
            	
            	
            	String qText = q.getQuestionText();
            	if ((qText == null) || (qText.length() == 0)) {
            		qText = EMPTY_FIELD;
            	}
            	os.write(qText + DELIM);
            	
            	String hintText = q.getHintText();
            	if ((hintText == null) || (hintText.length() == 0)) {
            		hintText = EMPTY_FIELD;
            	}            	
            	os.write(hintText + DELIM);
            	
            	String eText = q.getExplainText();
            	if ((eText == null) || (eText.length() == 0)) {
            		eText = EMPTY_FIELD;
            	}            	            	
            	os.write(eText + DELIM);
            	
            	String qType = null;
            	if (q.getQuestionType() == QuestionType.MULTI_CHOICE) {
            		qType = "1";
            	}
            	else
               	if (q.getQuestionType() == QuestionType.DRAG_N_DROP) {
               		qType = "2";
               	}
            	os.write(qType + DELIM);
              
            	List<PossibleAnswer> pAnswers = q.getPossibleAnswers();
            	int numPAnswers = pAnswers.size();
            	os.write(numPAnswers + DELIM);
            	
            	String pString = new String();
                for (int j=0; j < numPAnswers; j++) {
                	String pVal = pAnswers.get(j).getTheAnswer();
                	if ((pVal == null) || (pVal.length() == 0)) {
                		pVal = EMPTY_FIELD;
                	}            	            	
                	pString += pVal + DELIM;

                	pVal = pAnswers.get(j).getAnswerExplanation().toString();
                	if ((pVal == null) || (pVal.length() == 0)) {
                		pVal = EMPTY_FIELD;
                	}            	            	
                	pString += pVal + DELIM;                	
                }
                os.write(pString + DELIM);

            	List<Integer> cAnswers = q.getCorrectAnswers();
            	int numCAnswers = cAnswers.size();
            	os.write(numCAnswers + DELIM);            	

            	String cString = new String();
                for (int k=0; k < numCAnswers; k++) {
                	Integer cVal = cAnswers.get(k);
                	cString += cVal + DELIM;                	
                }
                os.write(cString + DELIM);

            	// DefaultStyledDocument qDoc
            	DefaultStyledDocument qDoc = q.getQuestionDoc();

        		int len = qDoc.getLength();

		        // DEBUG - Do a text dump of the DefaultStyledDocument.
		        //doc.dump(System.out);

		        // 1. Get the text.
		        String docTxt = qDoc.getText(0, len);

		        // Get references to any Images and JTextFields we may find.
		        HashMap<ImageIcon, Integer> imageIconMap = new HashMap<ImageIcon, Integer>();
		        HashMap<JTextField, Integer> textFieldMap = new HashMap<JTextField, Integer>();
		        
		        // For each character element in the StyledDoc...
				for (int c = 0; c < qDoc.getLength(); c++) {
					
					// 2. Identify any JTextFields
					Component comp = StyleConstants.getComponent(qDoc
							.getCharacterElement(c).getAttributes());
					if (comp != null && (comp instanceof JTextField)) {						
						JTextField jtf = (JTextField) comp;
						// Assign fields names ?
						// String str = jtf.getText(); System.out.println("getText() = " + str);
						// str = jtf.getName(); System.out.println("getName() = " + str);
						textFieldMap.put(jtf, c);
					}
					
					// 3. Identify any Images			
					Icon icon = StyleConstants.getIcon(qDoc
							.getCharacterElement(c).getAttributes());
			
					if (icon != null && (icon instanceof Icon)) {
						ImageIcon imageIcon = (ImageIcon) icon;						      
						imageIconMap.put(imageIcon, c);
					}
				}

            	// We need to save enough details so we can reconstruct 
            	// the DefaultStyledDocument afterwards.
				
				// Save the text string.
                os.write(docTxt + DELIM);
                
                // Save the number of Images we found (maybe zero...)
                os.write(imageIconMap.size() + DELIM);

                // Loop over HashMap of ImageIcons, saving details as we go.
                for (Map.Entry<ImageIcon, Integer> entry : imageIconMap.entrySet()) {

                    // Save image properties.
                    ImageIcon imageIcon = entry.getKey();
                	Image image = imageIcon.getImage();
                	int w = imageIcon.getIconWidth();
	    			int h = imageIcon.getIconHeight();
	    			int pos = entry.getValue();
                	int[] imagePixels = ImageUtils.getArrayFromImage(image, w, h);
	                os.write(w + DELIM);
	                os.write(h + DELIM);
	                os.write(pos + DELIM);
	                
	                // Convert an array of ints into a string.
	                StringBuilder pixelStr = new StringBuilder();
	                for (int x=0; x < imagePixels.length; x++) {
	                	pixelStr.append(imagePixels[x] + ",");
	                }	                	                
	                os.write(pixelStr + DELIM);
                }
                
                // Save the number of JTextFields we found (maybe zero...)
                os.write(textFieldMap.size() + DELIM);

                // Loop over HashMap of JTextFields, saving details as we go.
                for (Map.Entry<JTextField, Integer> entry : textFieldMap.entrySet()) {
                    JTextField jtf = entry.getKey();
                    
                    // Save JTextField properties
                    int pos = entry.getValue();
                    String fieldText = jtf.getText();
                    
	                os.write(pos + DELIM);
                	os.write(fieldText + DELIM);
                }
            }
                        
            os.flush();
            os.close();

        } catch (IOException ioe) {
      	    System.out.println("ERROR Saving File(" + fileName + "): " + ioe);      	    
      	    ioe.printStackTrace();
        } catch (Exception e) {
        	System.out.println("Exception Saving File " + fileName + "\n" + e);
        	e.printStackTrace();
        }				
	}	


	/**
	 *   
     * Read from the given file to the current pool of questions and also 
     * configuration details 
     *
	 * @param fileName - the file to read.
	 * @param qPool - the current question pool.
	 * @param classRef - The Class object is needed if we need to read
	 *                   from a Resource File (for instance, when JQuestions
	 *                   is running as an Applet in Demo-Mode).  
	 */
	public static void readFile(final String fileName, final QuestionPool qPool, Class classRef) {

		// Build up the input.
		StringBuilder input = new StringBuilder();

		try {

			GZIPInputStream gzis = null;  // Compressed file format.

			// Read a resource file. 
			if (classRef != null) {
				InputStream ins = classRef.getResourceAsStream(fileName); 
				gzis = new GZIPInputStream(ins);
			}
			else { // Read ordinary file object as a stream.
				FileInputStream fis = new FileInputStream(fileName);				
				gzis = new GZIPInputStream(fis);
			}

			InputStreamReader isw = new InputStreamReader(gzis); // 
			BufferedReader is = new BufferedReader(isw);// Buffered

			// Input may contain multiple lines.			
	        char[] buff = new char[1024];
	        int numRead = 0;
	        while((numRead = is.read(buff)) != -1){
	            input.append(buff, 0, numRead);
	        }
	        
            // Close input stream.
	        is.close();

	        // Start processing tokens.
	        String str = input.toString();
            processInput(str, qPool);
						

		} catch (IOException ioe) {
			System.out.println("ERROR Reading File(" + fileName + "): " + ioe);
			ioe.printStackTrace();
		} catch (Exception e) {
			System.out.println("Exception Reading File " + fileName + "\n" + e);
			e.printStackTrace();
		}

		// DEBUG code 
		//System.out.println("Read File " + fileName + "\n");
		//System.out.println("qp.size() = " + qPool.getSize());
		//qPool.printAllQuestions();
		//
	}

	/**
	 * Tokenises the input stream, constructs objects, reads values, 
	 * stores information throws errors where necessary.
	 * 
	 * @param line
	 * @param qPool
	 */
	
	// Processes one String, constructs Question objects. 
	public static void processInput(final String line, final QuestionPool qPool) {

		Question question = null;
		String fileFormatVersion = null;
        
		// Tell StringTokenizer to silently discard DELIM tokens.
		// boolean keepDELIMTokens = false;
		StringTokenizer st = new StringTokenizer(line, DELIM, false);
		
		// Read file format version number. 
		fileFormatVersion = st.nextToken();

		// Perform any checks on file format version number.
		if (fileFormatVersion.compareToIgnoreCase(FILE_FORMAT_VERSION)> 0) {
			JOptionPane.showMessageDialog(null,
		    "Newer file format. You need to upgrade JQuestions to a newer version\n"
			+ "to read this file. Visit http://sourceforge.net/projects/jquestions",
		    "File Format Version Error",
		    JOptionPane.ERROR_MESSAGE);
			return;
		}
	
		
    	// Read recommended Configuration settings
		String descriptionText;
    	String descText = st.nextToken();
    	if (descText.compareToIgnoreCase(EMPTY_FIELD) == 0) {
    		descriptionText = "";
    	}
    	descriptionText = descText;
    	qPool.setDescriptionText(descriptionText);

   	
    	Boolean isChangeAllowed;
    	String isChangeText = st.nextToken();
    	if (isChangeText.compareToIgnoreCase("T") == 0) {
            isChangeAllowed = new Boolean(true);    		
    	}
    	else {
    	    isChangeAllowed = new Boolean(false);
    	}
    	qPool.setIsChangeAllowed(isChangeAllowed);
    	
    	
    	Boolean isExam;
    	String isExamText = st.nextToken();
    	if (isExamText.compareToIgnoreCase("T") == 0) {
            isExam = new Boolean(true);    		
    	}
    	else {
    	    isExam = new Boolean(false);
    	}
    	qPool.setIsExam(isExam);


    	Boolean timed;
    	String timedText = st.nextToken();
    	if (timedText.compareToIgnoreCase("T") == 0) {
            timed = new Boolean(true);    		
    	}
    	else {
    	    timed = new Boolean(false);
    	}
    	qPool.setTimed(timed);
    	
    	
    	String timeLimitText =  st.nextToken();
        Integer timeLimit = Integer.parseInt(timeLimitText);
    	qPool.setTimeLimit(timeLimit);

    	Boolean displayFinalScore;
    	String finalScoreText =  st.nextToken();
    	if (finalScoreText.compareToIgnoreCase("T") == 0) {
            displayFinalScore = new Boolean(true);    		
    	}
    	else {
    	    displayFinalScore = new Boolean(false);
    	}
    	qPool.setDisplayFinalScore(displayFinalScore);
    	
    	Boolean displayPrintOption;
    	String printOptionText =  st.nextToken();
    	if (printOptionText.compareToIgnoreCase("T") == 0) {
            displayPrintOption = new Boolean(true);    		
    	}
    	else {
    	    displayPrintOption = new Boolean(false);
    	}
    	qPool.setDisplayPrintOption(displayPrintOption);

    	
    	String studentNameText =  st.nextToken();    	
    	if (studentNameText.compareToIgnoreCase(EMPTY_FIELD) == 0) {
    		studentNameText = "";
    	}
    	qPool.setStudentName(studentNameText);
    	
    	String nQuestionsText =  st.nextToken();
        Integer numberOfQuestions = Integer.parseInt(nQuestionsText);
        qPool.setNumberOfQuestions(numberOfQuestions);
   
              
		// Read fields for each new Question object.
		// Just ignore the "tokens" that consist of DELIM.
		while (st.hasMoreTokens()) {
        	String qText =  st.nextToken();
        	if (qText.compareToIgnoreCase(EMPTY_FIELD) == 0) {
        		qText = "";
        	}
        	
        	String hintText = st.nextToken();
        	if (hintText.compareToIgnoreCase(EMPTY_FIELD) == 0) {
        		hintText = "";
        	}
        	
        	String eText = st.nextToken();
        	if (eText.compareToIgnoreCase(EMPTY_FIELD) == 0) {
        		eText = "";
        	}
        	
        	String qType = st.nextToken();

        	QuestionType questionType = null;
        	if (qType.compareToIgnoreCase("1") == 0) {
        		questionType = QuestionType.MULTI_CHOICE;
        	}
        	else 
           	if (qType.compareToIgnoreCase("2") == 0) {
           		questionType = QuestionType.DRAG_N_DROP;
           	}
        	  	
        	List<PossibleAnswer> pAnswers = new ArrayList<PossibleAnswer>();
        	String strPAnswers = st.nextToken();
        	int numPAnswers = Integer.parseInt(strPAnswers);


          	String pString = null;
          	String eString = null;
            for (int j=0; j < numPAnswers; j++) {
            	pString = st.nextToken();
            	if (pString.compareToIgnoreCase(EMPTY_FIELD) == 0) {
            		pString = "";
            	}

            	eString = st.nextToken();
            	if (eString.compareToIgnoreCase(EMPTY_FIELD) == 0) {
            		eString = "";
            	}
            	            	
            	pAnswers.add(new PossibleAnswer(pString, false, j, new StringBuilder(eString)));
            }

        	List<Integer> cAnswers = new ArrayList<Integer>();
        	String strCAnswers = st.nextToken();
        	int numCAnswers = Integer.parseInt(strCAnswers);

          	String cString = null;
            for (int j=0; j < numCAnswers; j++) {
            	cString = st.nextToken();
            	int cInt = Integer.parseInt(cString);
            	cAnswers.add(cInt);
            }
            
            // Read DefaultStyledDocument properties.
            String null1 = null;
            DefaultStyledDocument dsd = new DefaultStyledDocument();

            // Read the doc text.
            String docText = st.nextToken();
        	if (docText.compareToIgnoreCase(EMPTY_FIELD) == 0) {
        		docText = "";
        	}

        	try {
        		dsd.insertString(0, docText, null);
        	} catch (BadLocationException ble) {
        		System.out.println("ERROR: " + ble);
        		ble.printStackTrace();
        	}
        	
        	
            // Read the number of Images we found (maybe zero...)
        	String imageCountStr = st.nextToken();
        	int imageCount = Integer.parseInt(imageCountStr);
            
        	
            // Read details of ImageIcons (if any).
        	for (int i=0; i<imageCount; i++) {
                String tempStr = st.nextToken();
                int w = Integer.parseInt(tempStr);
                
                tempStr = st.nextToken();
                int h = Integer.parseInt(tempStr);

                tempStr = st.nextToken();
                int pos = Integer.parseInt(tempStr);
                
                // Int array of image pixels, read as a String.
                tempStr = st.nextToken();
                                
                // Convert input String into array of ints.
                final int imageSize = h * w;
            	int[] pixelArray = new int[imageSize];  
            	
            	String[] splitStr = tempStr.split(",");
            	for(int z = 0; z < imageSize; z++) 
            		pixelArray[z] = Integer.parseInt(splitStr[z]);
                
            	Image image = ImageUtils.getImageFromArray(pixelArray, w, h);
            	ImageIcon imageIcon = new ImageIcon(image);
            	
            	// Reinsert image into 'dsd' String at correct position.
            	try {
                    // The image must first be wrapped in a style
                    Style style = dsd.addStyle("StyleName", null);
                    StyleConstants.setIcon(style, imageIcon);
                
                    // Insert the image at the correct position in the text.
                    // NOTE! You MUST insert a string representation of PRECISELY ONE
                    // char in length (namely "X", below) in order to insert ONE image.
                    // Remove the blank space first.
                    dsd.remove(pos, 1);
                    // Now insert the Image.
                    dsd.insertString(pos, "X", style);
            	} catch (BadLocationException ble) {
            		System.out.println("ERROR: " + ble);
            		ble.printStackTrace();                    
            	}
            }                
            

            // Read the number of JTextFields we found (maybe zero...)
        	String jtfCountStr = st.nextToken();
        	int jtfCount = Integer.parseInt(jtfCountStr);


            // Read properties of JTextFields (if any).
        	for (int i=0; i<jtfCount; i++) {
                String tempStr = st.nextToken();
                int pos = Integer.parseInt(tempStr);

                tempStr = st.nextToken();
                String textField = tempStr;
            	
            	// Create new JTextField component.
            	JTextField newJTF = createNewJTextField(textField);

            	// Reinsert the JTextField into 'dsd' String at correct pos.
            	try {
                    // The component must first be wrapped in a style
                    Style style = dsd.addStyle("StyleName", null);
                    StyleConstants.setComponent(style, newJTF);
                
                    // Insert the JTextField at the correct position in the text.
                    // NOTE! You MUST insert a string representation of PRECISELY ONE
                    // char in length (namely "X", below) in order to insert ONE component.
                    // Remove the blank space first.
                    dsd.remove(pos, 1);
                    // Now insert the JTextField.
                    dsd.insertString(pos, "X", style);
            	} catch (BadLocationException ble) {
            		System.out.println("ERROR: " + ble);
            		ble.printStackTrace();
            	}
            	
        	}        	
        	
        	// Instantiate new Question object.
            question = new Question(null1, qText, pAnswers, cAnswers, questionType, dsd, hintText, eText, false);
            
            //question.analyzeDoc();
                        
            // Add new question to pool.
            qPool.addQuestion(question);
		}
	}
	
	// createNewJTextField 
	//-----------------------------------------------------------------------
	/**
	 * <p>Given a text string this method constructs a 'standard', JQuestions
	 * JTextField object, used as a drop-target in a DRAG_N_DROP type Question.
	 *  
	 * This method may become more complex as features are added.
	 *
	 * A Builder Design Pattern method is used. This method has been declared  
	 * as <code>static</code> so it can be called without instantiating
	 * an IOUtils object.
	 *
	 * @param str  The initial string of text (if any) the JTextField is to 
	 *             contain. 
	 * @return a JTextField containing the text, formatted and constructed in
	 *         the standard way for JQuestions. 
	*/     
	  
	public static JTextField createNewJTextField(String str) {
	      JTextField jtf = new JTextField(str);  // Set initial text (if any).
	      jtf.setHorizontalAlignment(SwingConstants.CENTER);
	      jtf.setBackground(Color.cyan);
	      jtf.setOpaque(true);
	      jtf.setEditable(false);
	      jtf.setBorder(LineBorder.createBlackLineBorder());
	      jtf.setColumns(10);
	      int w = 80;
	      int h = 15;
	      Dimension dim = new Dimension(w, h);
	      jtf.setMaximumSize(dim);
	  
	      return jtf;	  
	}
	  
	// Useful debug method.  
	public static void printResults(String input, Object[] outputs) {
		System.out.println("Input: " + input);
		for (int i = 0; i < outputs.length; i++)
			System.out.println("Output " + i + " was: " + outputs[i]);
	}	
}
