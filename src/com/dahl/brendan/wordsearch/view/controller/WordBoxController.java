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

package com.dahl.brendan.wordsearch.view.controller;

import java.util.LinkedList;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dahl.brendan.wordsearch.model.Theme;
import com.dahl.brendan.wordsearch.view.R;
import com.dahl.brendan.wordsearch.view.runnables.UpdateLetterBox;
import com.dahl.brendan.wordsearch.view.runnables.UpdateWordBox;

/**
 * 
 * @author Brendan Dahl
 *
 * handles the logic of displaying the words that the user is to hunt in the grid
 */
public class WordBoxController implements OnClickListener, WordBoxControllerI {
	final private Button next;
	final private Button prev;
	final private TextView wordBox;
	final private TextView letterBox;
	private LinkedList<String> words;
	private int wordsIndex = 0;
	private int wordFound;

	protected WordBoxController(Button prev, Button next, TextView wordBox, TextView letterBox) {
		this.letterBox = letterBox;
		this.prev = prev;
		this.prev.setOnClickListener(this);
		this.next = next;
		this.next.setOnClickListener(this);
		this.wordBox = wordBox;
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.prev:
			if (wordsIndex > 0) {
				wordsIndex--;
			}
			break;
		case R.id.next:
			if (wordsIndex < words.size() - 1) {
				wordsIndex++;
			}
			break;
		default:
			return;
		}
		updateWordBox();
	}

	public void resetWords(LinkedList<String> wordList) {
		this.words = wordList;
		this.wordFound = words.size();
		this.wordsIndex = 0;
		this.updateWordBox();
	}

	/**
	 * displays the current word in the wordList to the user
	 */
	private void updateWordBox() {
		if (wordsIndex < 0 || wordsIndex > words.size()) {
			wordsIndex = 0;
		}
		boolean nextEnabled = (wordsIndex < words.size()-1);
		boolean prevEnabled = (wordsIndex > 0);
		CharSequence text = "";
		if (words.size() != 0) {
			text = words.get(wordsIndex);
		}
		prev.post(new UpdateWordBox(prev, next, wordBox, prevEnabled, nextEnabled, text));
	}

	public void setLetter(CharSequence charSequence) {
		prev.post(new UpdateLetterBox(charSequence, prev, letterBox));
	}
	
	public int wordFound(String str) {
		words.remove(str);
		wordsIndex = 0;
		this.updateWordBox();
		return words.size();
	}

	public int wordsLeft() {
		return words.size();
	}

	public int getWordsFound() {
		return this.wordFound;
	}
	
	public void updateTheme(Theme theme) {
		this.letterBox.setTextColor(theme.picked);
		this.wordBox.setTextColor(theme.normal);
	}
}
