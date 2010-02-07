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
import java.util.LinkedList;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.dahl.brendan.wordsearch.model.HighScore;
import com.dahl.brendan.wordsearch.util.ConversionUtil;
import com.dahl.brendan.wordsearch.view.R;
import com.dahl.brendan.wordsearch.view.WordSearchActivity;
import com.dahl.brendan.wordsearch.view.controller.WordSearchActivityController;

/**
 * 
 * @author Brendan Dahl
 *
 * shows an alert dialog to display the top three scores
 *
 */
public class HighScoresShow implements Runnable, DialogInterface.OnClickListener {
	final private WordSearchActivity wordSearch;
	final private WordSearchActivityController controller;
	final private Boolean gameOver;
	public HighScoresShow(WordSearchActivityController controller, WordSearchActivity wordSearch, boolean gameOver) {
		this.gameOver = gameOver;
		this.controller = controller;
		this.wordSearch = wordSearch;
	}

	public void run() {
		LinkedList<HighScore> highScores = controller.getHighScores();
		StringBuilder str = new StringBuilder();
		if (highScores.size() == 0) {
			str.append(wordSearch.getString(R.string.no_high_scores));
		} else {
			for (int index = 0; index < highScores.size(); index++) {
				str.append(Integer.toString(index+1)+": "+highScores.get(index).getInitials()+" " + highScores.get(index).getScore() + " ( " + ConversionUtil.formatTime.format(new Date(highScores.get(index).getTime())) + " )\n");
			}
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(wordSearch);
		builder.setTitle(R.string.high_score);
		builder.setMessage(str);
		builder.setNegativeButton(R.string.reset, this);
		builder.setNeutralButton(android.R.string.ok, this);
		builder.show();
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			controller.resetScores();
		}
		if (gameOver) {
			wordSearch.runOnUiThread(new NewGameDialog(controller, wordSearch, null));
		}
	}
}
