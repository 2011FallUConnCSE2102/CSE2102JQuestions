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
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;


/**
 * A general 'About' dialog box which shows interesting licensing information.
 * Fixed size. Singleton.
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 */


public class AboutDialog extends JDialog {

	final private int width = 440;
	final private int height = 540;
	

	/**
	 * Constructor.
	 * 
	 * Private constructor prevents instantiation from other classes
	 */

	private AboutDialog() {
		super();

		setTitle("About JQuestions Editor");

		Container content = this.getContentPane();
				
		JPanel tempPanel = new JPanel();		
		tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.Y_AXIS));
        Border paneEdge = BorderFactory.createEmptyBorder(10,10,10,10);
        tempPanel.setBorder(paneEdge);
        content.add(tempPanel);
		
		//Create another panel with a nice border.		
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border compound = BorderFactory.createCompoundBorder(
					  raisedbevel, loweredbevel);
		JPanel stringsPanel = new JPanel(new GridBagLayout());
		stringsPanel.setBorder(compound);

        // Create labels.		
		JLabel jqString = new JLabel("JQuestions Editor");
		JLabel versionString = new JLabel("Version:  0.9.1 (BETA)");
		JLabel copyString = new JLabel("(c) 2010 Ken Williams.  All rights reserved");
		JLabel urlString = new JLabel("Home Page:  http://sourceforge.net/projects/jquestions");

		JButton okButton = new JButton("OK");

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setVisible(false);
			}
		});
	

		// Layout the labels.
		//                                                      x  y  w  h  wtx  wty  anchor                   fill                                 T   L   B   R padx pady			
		stringsPanel.add(jqString,      new GridBagConstraints( 0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 20, 25, 0,  5 ), 0, 0 ) );	
		stringsPanel.add(versionString, new GridBagConstraints( 0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 25, 0,  5 ), 0, 0 ) );	
		stringsPanel.add(copyString,    new GridBagConstraints( 0, 2, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 25, 0,  5 ), 0, 0 ) );	
		stringsPanel.add(urlString,     new GridBagConstraints( 0, 3, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 25, 0,  5 ), 0, 0 ) );	

		JPanel licensePanel = new JPanel();
        Border licenseEdge = BorderFactory.createLineBorder(Color.BLACK, 1);
        licensePanel.setBorder(licenseEdge);
        
        
        // This should probably be held in an external resource file in case the 
        // licensing text ever changes.
        final String licenseString = 
        	"<html>This program is free software; you can redistribute it and/or<br>" +
        	"modify it under the terms of the GNU General Public License <br>" + 
        	"as published by the Free Software Foundation; either version<br>" + 
        	"3 of the License or (at your discretion) any later version.<br>" +
        	"<br>" + 
        	"This program is distributed in the hope that it will be useful,<br>" +
        	"but WITHOUT ANY WARRANTY; without even the implied<br>" +
        	"warranty of MERCHANTABILITY or FITNESS FOR A<br>" + 
        	"PARTICULAR PURPOSE. See the GNU General Public License <br>" +
        	"for more details.<br>" +
        	"<br>" +
        	"You should have received a copy of the GNU General<br>" +
        	"Public License along with this program; if not, then see<br>" +
        	"http://www.gnu.org/licenses/gpl-3.0.txt, or else write<br>" +
        	"to the Free Software Foundation, Inc., 59 Temple Place - <br>" + 
        	"Suite 330, Boston, MA 02111-1307, USA." +
        	"</html>"; 
        
        
		JLabel licenseLabel = new JLabel(licenseString);
		licensePanel.add(licenseLabel);

		stringsPanel.add(licensePanel,  new GridBagConstraints( 0, 5, 3, 2, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 25, 0, 0 ), 0, 0 ) );	        		
		stringsPanel.add(okButton,      new GridBagConstraints( 0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 20, 150, 20,  0 ), 50, 0 ) );	        
		tempPanel.add(stringsPanel);

        
		// Fixed size.
		setSize(width, height);
		setResizable(false);
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	
	public void setVisible(boolean b) {
		setSize(width, height);      // I should not need to do this but 
		                             // it redraws differently if I don't (!?!)
		super.setVisible(b);
	}
	

	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before. Use class loader to instantiate instance, thereby
	 * ensuring instantiation is thread-safe.
	 */
	private static class SingletonHolder {
		private static final AboutDialog INSTANCE = new AboutDialog();
	}

	public static AboutDialog getInstance() {
		return SingletonHolder.INSTANCE;
	}

}
