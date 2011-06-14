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
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.*;


/**
 * This class defines the listener for dropping of JTextField 
 * components onto a main question panel (textPane) as part
 * of the editing process for a DRAG_N_DROP type question.       
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see EditorQuestionScreen
 * @see DragAndDropQuestionScreen
 * 
 */
public class TextDropTargetListener implements DropTargetListener {

	// The main question panel.
	private JTextPane textPane;

	public TextDropTargetListener(JTextPane tp) {
		textPane = tp;
	}

	public void dragEnter(DropTargetDragEvent event) {
		int a = event.getDropAction();
		if (!isDragAcceptable(event)) {
			event.rejectDrag();
			return;
		}
	}

	public void dragExit(DropTargetEvent event) {}

	public void dragOver(DropTargetDragEvent event) {
		// you can provide visual feedback here.
	}

	public void dropActionChanged(DropTargetDragEvent event) {
		if (!isDragAcceptable(event)) {
			event.rejectDrag();
			return;
		}
	}

	public void drop(DropTargetDropEvent e) {
		try {
			Transferable t = e.getTransferable();

			if (e.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				e.acceptDrop(DnDConstants.ACTION_COPY); // set the drop action
														// type.

				String s;
				s = (String) t.getTransferData(DataFlavor.stringFlavor);

				JTextField jtf = createNewJTextField(s);

				// TextField is dropped at position of cursor.
				textPane.insertComponent(jtf);

				e.dropComplete(true);
			} else
				e.rejectDrop();
		} catch (IOException ioe) {
			System.out.println("ioe " + ioe);
			ioe.printStackTrace();

		} catch (UnsupportedFlavorException ufe) {
			System.out.println("ufe " + ufe);
			ufe.printStackTrace();
		}

	}

	public boolean isDragAcceptable(DropTargetDragEvent event) {
		// usually, you check the available data flavors here
		// in this program, we only use text.
		return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
	}

	public boolean isDropAcceptable(DropTargetDropEvent event) {
		// usually, you check the available data flavors here
		// in this program, we only use text.
		return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
	}

	// createNewJTextField
	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Given a text string this method constructs a 'standard', JQuestions
	 * JTextField object, used as a drop-target in a DRAG_N_DROP type Question.
	 * 
	 * This method may become more complex as features are added.
	 * 
	 * A Builder Design Pattern method, this method has been declared as
	 * <code>static</code> so it can be called without instantiating a
	 * TextDropTargetListener object.
	 * 
	 * @param str
	 *            The initial string of text (if any) the JTextField is to
	 *            contain.
	 * @return a JTextField containing the text, formatted and constructed in
	 *         the standard way for JQuestions.
	 */

	public static JTextField createNewJTextField(String str) {
		JTextField jtf = new JTextField(str); // Set initial text (if any).
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
}
