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

import java.util.Date;
import java.util.LinkedList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;

import com.dahl.brendan.wordsearch.model.HighScore;
import com.dahl.brendan.wordsearch.model.Preferences;
import com.dahl.brendan.wordsearch.util.ConversionUtil;
import com.dahl.brendan.wordsearch.view.R;
import com.dahl.brendan.wordsearch.view.WordSearchActivity;

/**
 * 
 * @author Brendan Dahl
 *
 * this class stores the action of requesting the user's initials to store a new high score
 *
 */
public class HighScoresInitials implements Runnable, DialogInterface.OnClickListener {
	final private HighScore hs;
	final private EditText text;
	final private WordSearchActivity wordSearch;
	final private Preferences prefs;
	public HighScoresInitials(HighScore hs, WordSearchActivity wordSearch, Preferences prefs) {
		this.hs = hs;
		this.wordSearch = wordSearch;
		this.prefs = prefs;
		this.text = new EditText(wordSearch);
	}

	public void run() {
		AlertDialog.Builder builder = new AlertDialog.Builder(wordSearch);
		builder.setMessage(wordSearch.getString(R.string.enter_initials).replace("%score", hs.getScore().toString()+" ("+ConversionUtil.formatTime.format(new Date(hs.getTime()))+")"));
		text.setSingleLine();
		builder.setView(text);
		builder.setPositiveButton(android.R.string.ok, this);
		builder.setNeutralButton(android.R.string.cancel, this);
		builder.show();
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			LinkedList<HighScore> scores = prefs.getTopScores();
			String name = text.getText().toString();
			if (!TextUtils.isEmpty(name)) {
				hs.setInitials(name);
			} else {
				hs.setInitials("?");
			}
			scores.add(hs);
			prefs.setTopScores(scores);
			wordSearch.showHighScore();
		}
	}
}
