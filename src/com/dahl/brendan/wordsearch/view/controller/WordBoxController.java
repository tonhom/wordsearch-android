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

import com.dahl.brendan.wordsearch.view.R;
import com.dahl.brendan.wordsearch.view.runnables.UpdateLetterBox;
import com.dahl.brendan.wordsearch.view.runnables.UpdateWordBox;

/**
 * 
 * @author Brendan Dahl
 *
 * handles the logic of displaying the words that the user is to hunt in the grid
 */
public class WordBoxController implements OnClickListener {
	final private Button next;
	final private Button prev;
	final private TextView wordBox;
	final private TextView letterBox;
	private LinkedList<String> words;
	private int wordsIndex = 0;

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

	/**
	 * resets the list of words available to the user
	 * 
	 * @param wordList new list of available words
	 */
	protected void resetWords(LinkedList<String> wordList) {
		this.words = wordList;
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

	/**
	 * 
	 * @param charSequence sets the letter to show the user which letter is being touched
	 * 						null to hide the preview letter
	 */
	protected void setLetter(CharSequence charSequence) {
		prev.post(new UpdateLetterBox(charSequence, prev, letterBox));
	}
	
	/**
	 * removes a word from the list of words to find
	 * 
	 * @param str word to remove the list of words
	 * @return number of words left to find
	 */
	protected int wordFound(String str) {
		words.remove(str);
		wordsIndex = 0;
		this.updateWordBox();
		return words.size();
	}

	protected int wordsLeft() {
		return words.size();
	}
}
