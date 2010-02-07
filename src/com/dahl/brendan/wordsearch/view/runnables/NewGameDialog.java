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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.dahl.brendan.wordsearch.view.R;
import com.dahl.brendan.wordsearch.view.WordSearchActivity;
import com.dahl.brendan.wordsearch.view.controller.WordSearchActivityController;

/**
 * 
 * @author Brendan Dahl
 *
 * this class stores the action of notifying the user that their game is over and if they want to start a new game
 *
 */
public class NewGameDialog implements Runnable, DialogInterface.OnClickListener {
	final private WordSearchActivityController controller;
	final private WordSearchActivity wordSearch;
	final private String extraText;
	public NewGameDialog(WordSearchActivityController controller, WordSearchActivity wordSearch, String extraText) {
		this.controller = controller;
		this.wordSearch = wordSearch;
		if (TextUtils.isEmpty(extraText)) {
			this.extraText = "";
		} else {
			this.extraText = extraText+"\n";
		}
	}

	public void run() {
		AlertDialog.Builder builder = new AlertDialog.Builder(wordSearch);
		builder.setMessage(extraText+wordSearch.getString(R.string.game_over));
		builder.setPositiveButton(R.string.new_game, this);
		builder.setNeutralButton(android.R.string.cancel, this);
		builder.show();
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			controller.newWordSearch();
		}
	}
}
