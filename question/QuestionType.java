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

package question;


/**
 * 
 * A small but important enum class of Question types. 
 * 
 * Currently there are only two types of question but it should be 
 * straightforward to add others in the future - starting here.
 * 
 * MULTI_CHOICE questions are displayed using the MultiChoiceQuestionsScreen.
 * DRAG_N_DROP questions are displayed using the DragAndDropQuestionsScreen. 
 *
 * The class needs to be public since it is referred to extensively 
 * throughout both JQuestions and JQuestionsEditor programs. 
 * 
 * @author Ken Williams <jquestions@truerandomness.com>
 * @see Question
 * @see MultiChoiceQuestionScreen
 * @see DragAndDropQuestionScreen
 * 
 */



public enum QuestionType {MULTI_CHOICE, DRAG_N_DROP};



