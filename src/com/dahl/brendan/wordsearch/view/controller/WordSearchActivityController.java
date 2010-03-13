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

package com.dahl.brendan.wordsearch.view.controller;

import java.util.Calendar;
import java.util.LinkedList;

import android.content.res.ColorStateList;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;

import com.dahl.brendan.wordsearch.model.Grid;
import com.dahl.brendan.wordsearch.model.HighScore;
import com.dahl.brendan.wordsearch.model.Preferences;
import com.dahl.brendan.wordsearch.model.dictionary.DictionaryFactory;
import com.dahl.brendan.wordsearch.view.R;
import com.dahl.brendan.wordsearch.view.WordSearchActivity;
import com.dahl.brendan.wordsearch.view.runnables.GameOver;

/**
 * 
 * @author Brendan Dahl
 *
 * controls game logic and sub-control modules for word search activity
 */
public class WordSearchActivityController {
	/**
	 * this is the size of the current grid, also used by convertionUtil
	 */
	private static int gridSize = -1;
	/**
	 * sub-control module
	 */
	private WordBoxController wordBoxManager;
	/**
	 * sub-control module
	 */
	private TextViewGridController gridManager;
	/**
	 * factory to the possible dictionaries
	 */
	private DictionaryFactory dictionaryFactory;
	/**
	 * system time in millis when the current session began
	 */
	private long timeStart = 0L;
	/**
	 * sum in milliseconds of previous sessions for this game
	 */
	private long timeSum = 0L;
	/**
	 * contains the current game's grid of letters
	 */
	private Grid grid;
	/**
	 * stores and retreives settings that need to be persisted
	 */
	final private Preferences prefs;
	/**
	 * the activity this controls
	 */
	private WordSearchActivity wordSearch;
	private HighScore hs;

	/**
	 * used to serialize the control to and from a bundle
	 */
	private static final String BUNDLE_TIME = "ws_time";
	private static final String BUNDLE_VIEW = "ws_view";
	private static final String BUNDLE_GRID = "ws_grid";
	private static final String BUNDLE_GRID_SIZE = "ws_grid_size";
	private static final String BUNDLE_HIGH_SCORE = "ws_high_score";

	public static int getGridSize() {
		return gridSize;
	}

	public WordSearchActivityController(WordSearchActivity wordSearch,
			Bundle savedInstanceState) {
		super();
		this.wordSearch = wordSearch;
		prefs = new Preferences(this.wordSearch);
		dictionaryFactory = new DictionaryFactory(this.wordSearch);
		{
			TextView wordBox = (TextView) wordSearch.findViewById(R.id.wordBox);
			TextView letterBox = (TextView) wordSearch
					.findViewById(R.id.letterBox);
			Button prev = (Button) wordSearch.findViewById(R.id.prev);
			Button next = (Button) wordSearch.findViewById(R.id.next);
			wordBoxManager = new WordBoxController(prev, next, wordBox,
					letterBox);
		}
		{
			ColorStateList found = wordSearch.getResources().getColorStateList(
					R.drawable.color_found);
			ColorStateList picked = wordSearch.getResources()
					.getColorStateList(R.drawable.color_picked);
			ColorStateList normal = wordSearch.getResources()
					.getColorStateList(R.drawable.color_standard);
			gridManager = new TextViewGridController(this,
					new TextView[prefs.getSize()][],
					found, picked, normal);
		}
		this.setLetter("l");
		this.setLetter(null);
		if (savedInstanceState != null) {
			this.setGridSize(savedInstanceState.getInt(BUNDLE_GRID_SIZE));
		} else {
			this.setGridSize(prefs.getSize());
		}
		prefs.getTouchMode();
		this.updateTouchMode();
	}

	protected void foundWord(String word) {
		int remainingWordCount = wordBoxManager.wordFound(word);
		if (remainingWordCount == 0) {
			Calendar diffTime = Calendar.getInstance();
			Long diffMill = System.currentTimeMillis() - timeStart + timeSum;
			diffTime.setTimeInMillis(diffMill);
			setHighScore(diffMill);
		}
	}

	public HighScore getCurrentHighScore() {
		if (hs == null) {
			throw new RuntimeException("currentHighScore is null");
		}
		return hs;
	}

	public TextViewGridController getGridManager() {
		return gridManager;
	}

	public LinkedList<HighScore> getHighScores() {
		return prefs.getTopScores();
	}
	
	public Preferences getPrefs() {
		return prefs;
	}
	
	public String guessWord(Point pointStart, Point pointEnd) {
		return grid.guessWord(pointStart, pointEnd);
	}

	public boolean isGameRunning() {
		return wordBoxManager.wordsLeft() != 0;
	}

	public boolean isHighScorer() {
		LinkedList<HighScore> scores = prefs.getTopScores();
		return (scores.size() < 3 || (this.getCurrentHighScore() != null && this.getCurrentHighScore().getScore() > scores.get(2).getScore()));
	}

	public void newWordSearch() {
		this.setGridSize(prefs.getSize());
		String category = PreferenceManager.getDefaultSharedPreferences(wordSearch).getString(wordSearch.getString(R.string.prefs_category), wordSearch.getString(R.string.RANDOM));
		grid = Grid.generateGrid(dictionaryFactory.getDictionary(category), 12, 4, getGridSize());
		if (grid.getWordListLength() == 0) {
			if (dictionaryFactory.isCustomDictionary()) {
				wordSearch.showDialog(WordSearchActivity.DIALOG_ID_NO_WORDS_CUSTOM);
			} else {
				wordSearch.showDialog(WordSearchActivity.DIALOG_ID_NO_WORDS);
			}
		}
		timeSum = 0L;
		this.setGrid(grid);
		wordSearch.trackGame();
	}

	public void resetScores() {
		prefs.resetTopScores();
	}

	public void restoreState(Bundle inState) {
		if (inState != null) {
			this.timeSum = inState.getLong(BUNDLE_TIME, 0);
			this.grid = inState.getParcelable(BUNDLE_GRID);
			this.setGrid(grid);
			this.gridManager.fromBundle(inState.getBundle(BUNDLE_VIEW));
			this.hs = inState.getParcelable(BUNDLE_HIGH_SCORE);
		}
	}
	
	public void saveState(Bundle outState) {
		if (outState != null) {
			outState.putInt(BUNDLE_GRID_SIZE, gridSize);
			outState.putLong(BUNDLE_TIME, this.timeSum);
			outState.putParcelable(BUNDLE_GRID, this.grid);
			outState.putBundle(BUNDLE_VIEW, this.gridManager.toBundle());
			outState.putParcelable(BUNDLE_HIGH_SCORE, this.hs);
		}
	}
	private void setGrid(Grid grid) {
		gridManager.reset(grid);
		wordBoxManager.resetWords(grid.getWordList());
		timeStart = System.currentTimeMillis();
	}
	public void setGridSize(int gridSizeNew) {
		gridSize = prefs.getSize();
		gridManager.setGridView(new TextView[gridSize][]);
		wordSearch.setupViewGrid(gridSize, gridManager);
	}
	private void setHighScore(long time) {
		hs = new HighScore(time, gridSize, dictionaryFactory.getScoreThemeMultiplier());
		wordSearch.runOnUiThread(new GameOver(wordSearch));
	}
	protected void setLetter(CharSequence charSequence) {
		wordBoxManager.setLetter(charSequence);
	}

	public void timePause() {
		timeSum += System.currentTimeMillis() - timeStart;
	}

	public void timeResume() {
		timeStart = System.currentTimeMillis();
	}

	public void updateTouchMode() {
		this.gridManager.setTouchMode(prefs.getTouchMode());
	}

}
