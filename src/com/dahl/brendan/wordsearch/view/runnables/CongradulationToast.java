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
//	  Copyright 2009-2010 Brendan Dahl

package com.dahl.brendan.wordsearch.view.runnables;

import java.util.Date;

import com.dahl.brendan.wordsearch.model.HighScore;
import com.dahl.brendan.wordsearch.util.ConversionUtil;
import com.dahl.brendan.wordsearch.view.R;
import com.dahl.brendan.wordsearch.view.WordSearchActivity;
import com.dahl.brendan.wordsearch.view.controller.WordSearchActivityController;

/**
 * 
 * @author Brendan Dahl
 *
 * this class stores the action of showing a congratulation toast to the user for finishing the game
 *
 */
public class CongradulationToast implements Runnable {
	final private HighScore hs;
	final private WordSearchActivity wordSearch;
	final private WordSearchActivityController controller;
	public CongradulationToast(WordSearchActivityController controller, HighScore hs, WordSearchActivity wordSearch) {
		this.hs = hs;
		this.wordSearch = wordSearch;
		this.controller = controller;
	}
	public void run() {
		NewGameDialog newGameDialog = new NewGameDialog(controller, wordSearch, wordSearch.getString(R.string.congradulations).replace(
				"%time",hs.getScore().toString()+" ("+ConversionUtil.formatTime.format(new Date(hs.getTime()))+")"));
		newGameDialog.run();
	}
}
