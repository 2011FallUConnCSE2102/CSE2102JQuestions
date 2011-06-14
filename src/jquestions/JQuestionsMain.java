package jquestions;

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


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import jquestionsgui.JQuestionsGUI;

/**
 * This is a simple wrapper class defined to allow JQuestions
 * to run as a stand-alone desktop application or as an applet 
 * embedded within a web-page (esp. in 'Demo mode').
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see JQuestions
 * @see JQEditorMain
 * 
 */

public class JQuestionsMain extends JApplet {

	// Define size.
	private static final long serialVersionUID = 1L;
	private static Dimension screenSize;
	final static int preferredWidth = 700;
	final static int preferredHeight = 850;
	private boolean isApplet = true;    // default should be 'true'.
    private boolean isDemo = false;	
	
	private JQuestions jqMain;
	private JQuestionsGUI guiInterface;
	
	public void setIsApplet(boolean b) {
		isApplet = b;
	}

	public boolean getIsApplet() {
		return isApplet;
	}

	public boolean getIsDemo() {
		return isDemo;
	}

	
	public void init() {		

		if (isApplet) {
			// Try to see if any command-line params.
			String isDemoStr = getParameter("isDemo"); 

			if (isDemoStr != null) {
            	if  (isDemoStr.compareToIgnoreCase("true") == 0) {
            		isDemo = true;
            	}
			}
		}		

		getContentPane().setLayout(new BorderLayout());
						
		jqMain = new JQuestions();

		guiInterface = new JQuestionsGUI(jqMain, getIsApplet(), getIsDemo());

		JMenuBar menuBar = guiInterface.getMenuBar();
		setJMenuBar(menuBar);
		getContentPane().add(guiInterface);
	}

	/**
	 * @param args - check for any command-line parameters
	 * e.g.
	 *               isDemo=true
	 */
	public static void main(String[] args) {

		JFrame f = new JFrame("JQuestions");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		
		// Create the application wrapper so it can run as
		// either a stand-alone application or a JApplet.
		JQuestionsMain jqMain = new JQuestionsMain();

		// If this 'main' method is executed then it cannot 
		// be running as an applet. Change boolean value before
		// calling the init() method.
		jqMain.setIsApplet(false);

		
		f.getContentPane().add("Center", jqMain);
		jqMain.init();
		f.pack();
		
		
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();		
		
		int w = preferredWidth;
		if (w > screenSize.width) {
			w = screenSize.width - 50;
		}

		int h = preferredHeight;
		if (h > screenSize.height) {
			h = screenSize.height - 50;
		}
		
		f.setSize(new Dimension(w, h));
		f.setVisible(true);
	}
}
