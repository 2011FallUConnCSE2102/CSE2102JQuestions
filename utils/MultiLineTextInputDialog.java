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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 * A simple text box that allows the user to input text such 
 * as hints and explanations. 
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see Question
 */

public class MultiLineTextInputDialog extends JOptionPane {

	private static final long serialVersionUID = 1L;

	public static String showInputDialog(final String message, 
			            final String currentData, final boolean setEditable) {
		String data = currentData;

		class GetData extends JDialog implements ActionListener {
			private static final long serialVersionUID = 1L;
			JScrollPane scrollPane = new JScrollPane();
            final static int height = 10;
            final static int width = 30;            
			JTextArea inputTextArea = new JTextArea(currentData, height, width);
			JButton okButton = new JButton("OK");
			JButton cancelButton = new JButton("Cancel");
			String str = null;
			
			public GetData() {
				setModal(true);							
				
				if (message.contains("hint")) {
				    setTitle("Hint Text");
				}
				else
				if (message.contains("expla")) {
				    setTitle("Explanation");
				}
				
				getContentPane().setLayout(new BorderLayout());
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				setLocation(400, 300);
				JLabel messageLabel = new JLabel(message);
				
				if (setEditable == true) {
					getContentPane().add(messageLabel, BorderLayout.NORTH);					
				}

				getContentPane().add(scrollPane, BorderLayout.CENTER);

				inputTextArea.setWrapStyleWord(true);
				inputTextArea.setLineWrap(true);
				inputTextArea.setBorder(null);
				inputTextArea.setEditable(setEditable);				
				scrollPane.add(inputTextArea);
				scrollPane.setBorder(null);
				scrollPane.setViewportView(inputTextArea);
				

				JPanel jp = new JPanel();
				okButton.addActionListener(this);
				cancelButton.addActionListener(this);
				jp.add(okButton);
				jp.add(cancelButton);
				getContentPane().add(jp, BorderLayout.SOUTH);
								
				pack();
				setVisible(true);
			}

			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == okButton)
					str = inputTextArea.getText();
				dispose();
			}

			public String getData() {
				return str;
			}
		}
		data = new GetData().getData();
		return data;
	}
}
