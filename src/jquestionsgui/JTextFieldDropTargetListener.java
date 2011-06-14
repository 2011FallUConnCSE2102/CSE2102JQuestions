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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JTextField;

/**
 * This class defines the drop target listener code used in the 
 * DRAG_N_DROP type question screen. Drop targets are the 
 * text fields in the main display panel which hold the 
 * user answers to the question. They are the 'gaps' the
 * user fills in - this is the listener for those gaps. 
 *   
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see DragAndDropQuestionScreen
 * 
 */
public class JTextFieldDropTargetListener implements DropTargetListener {

	private JTextField jtf = null;
	
	JTextFieldDropTargetListener(JTextField parent) {
		jtf = parent; 
	}
	
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
	    try {
	        // Ok, get the dropped object and try to figure out what it is
	        Transferable tr = dtde.getTransferable();
	        DataFlavor[] flavors = tr.getTransferDataFlavors();

			// Hopefully it's a text input stream.
			for (int i = 0; i < flavors.length; i++) {
				if (flavors[i].isRepresentationClassInputStream()) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY);
					jtf.read(new InputStreamReader((InputStream) tr
							.getTransferData(flavors[i])), "");
					dtde.dropComplete(true);
					return;
				}
			}
	        // Hmm, the user must have dropped something unexpected...
	        //System.out.println("Drop failed: " + dtde);
	        dtde.rejectDrop();
	      } catch (Exception e) {
	        e.printStackTrace();
	        dtde.rejectDrop();
	      }
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}
}
