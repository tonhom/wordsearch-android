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

import com.dahl.brendan.wordsearch.view.WordSearchActivity;

/**
 * 
 * @author Brendan Dahl
 *
 * this class stores the action of requesting the user's initials to store a new high score
 *
 */
public class GameOver implements Runnable {

	final private WordSearchActivity wordSearch;

	public GameOver(WordSearchActivity wordSearch2) {
		this.wordSearch = wordSearch2;
	}

	public void run() {
		if (wordSearch.getControl().isHighScorer()) {
			wordSearch.showDialog(WordSearchActivity.DIALOG_ID_HIGH_SCORES_INITIALS);
		} else {
			wordSearch.showDialog(WordSearchActivity.DIALOG_ID_GAME_NEW);
		}
	}

}
