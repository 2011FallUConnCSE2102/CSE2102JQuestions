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

package jquestionseditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import jquestionseditorgui.JQuestionsEditorGUI;


/**
 * This is a simple wrapper class defined to allow the JQuestions Editor
 * to run as a stand-alone desktop application or as an applet embedded
 * within a web-page.  
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see JQuestionsEditor
 * @see JQuestionsMain
 * 
 */
public class JQEditorMain extends JApplet {

	private final static long serialVersionUID = 1L;
	private static Dimension screenSize;
	public final static int preferredWidth = 700;
    public final static int preferredHeight = 850;
    
	JQuestionsEditor jqEditor;
	
	public void init() {
		getContentPane().setLayout(new BorderLayout());
		jqEditor = new JQuestionsEditor();
		JQuestionsEditorGUI guiInterface = new JQuestionsEditorGUI(jqEditor);
		JMenuBar menuBar = guiInterface.getMenuBar();
		setJMenuBar(menuBar);
		getContentPane().add(guiInterface);
	}	
	
	/**
	 * @param args 
	 */	
	public static void main(String[] args) {

		JFrame f = new JFrame("JQuestions Editor");
	    f.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent e) {System.exit(0);}
	    });
				
	    // Create the application wrapper so it can run as 
	    // either a stand-alone application or a JApplet.
	    JApplet jqEditorApplet = new JQEditorMain();
	            
        f.getContentPane().add("Center", jqEditorApplet);
        jqEditorApplet.init();
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
