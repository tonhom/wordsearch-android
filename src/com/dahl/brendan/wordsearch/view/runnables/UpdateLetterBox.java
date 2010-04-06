//    This file is part of Open WordSearch.
//
//    Open WordSearch is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Open WordSearch is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Open WordSearch.  If not, see <http://www.gnu.org/licenses/>.
//
//	  Copyright 2009, 2010 Brendan Dahl <dahl.brendan@brendandahl.com>
//	  	http://www.brendandahl.com

package com.dahl.brendan.wordsearch.view.runnables;

import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author Brendan Dahl
 *
 * this class stores the action of updating the letter box on the ui thread
 *
 */
public class UpdateLetterBox implements Runnable {

	final private CharSequence letterBoxText;
	final private Button prev;
	final private TextView letterBox;
	
	public UpdateLetterBox(CharSequence letterBoxText, Button prev, TextView letterBox) {
		this.letterBoxText = letterBoxText;
		this.prev = prev;
		this.letterBox = letterBox;
	}
	
	public void run() {
		if (letterBoxText == null) {
			prev.setVisibility(Button.VISIBLE);
			letterBox.setVisibility(TextView.INVISIBLE);
		} else {
			prev.setVisibility(Button.INVISIBLE);
			letterBox.setText(letterBoxText);
			letterBox.setVisibility(TextView.VISIBLE);
		}
	}

}
