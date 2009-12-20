//    This file is part of WordSearch FREE.
//
//    WordSearch FREE is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    WordSearch FREE is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with WordSearch FREE.  If not, see <http://www.gnu.org/licenses/>.
//
//	  Copyright 2009 Brendan Dahl

package com.dahl.brendan.wordsearch.view.runnables;

import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author Brendan Dahl
 *
 * this class stores the action of updating the wordBox on the ui thread
 *
 */
public 	class UpdateWordBox implements Runnable {
	final private Button prev;
	final private Button next;
	final private TextView wordBox;
	final private boolean prevEnabled;
	final private boolean nextEnabled;
	final private CharSequence wordBoxtext;
	public UpdateWordBox(Button prev, Button next, TextView wordBox,
			boolean prevEnabled, boolean nextEnabled, CharSequence wordBoxtext) {
		this.prev = prev;
		this.next = next;
		this.wordBox = wordBox;
		this.prevEnabled = prevEnabled;
		this.nextEnabled = nextEnabled;
		this.wordBoxtext = wordBoxtext;
	}
	public void run() {
		next.setEnabled(nextEnabled);
		prev.setEnabled(prevEnabled);
		wordBox.setText(wordBoxtext);
	}
}
