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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
import android.widget.TextView;

import com.dahl.brendan.wordsearch.Constants;
import com.dahl.brendan.wordsearch.model.Grid;
import com.dahl.brendan.wordsearch.model.HighScore;
import com.dahl.brendan.wordsearch.model.Preferences;
import com.dahl.brendan.wordsearch.model.Theme;
import com.dahl.brendan.wordsearch.model.dictionary.DictionaryFactory;
import com.dahl.brendan.wordsearch.util.AndroidHttpClient;
import com.dahl.brendan.wordsearch.view.R;
import com.dahl.brendan.wordsearch.view.WordSearchActivity;

/**
 * 
 * @author Brendan Dahl
 *
 * controls game logic and sub-control modules for word search activity
 */
public class WordSearchActivityController {
	private final static String LOG_TAG = WordSearchActivityController.class.getName();

	class GameOver implements Runnable {
		public void run() {
			new GameOverTask().execute(new Integer[0]);
		}
	}
	class GameOverTask extends AsyncTask<Integer, Integer, Boolean> {
		final private ProgressDialog pd = new ProgressDialog(wordSearch);

		@Override
		protected Boolean doInBackground(Integer... res) {
//			Debug.startMethodTracing("ranking");
			try {
				LinkedList<HighScore> scores = wordSearch.getControl().getHighScores();
				scores.add(hs);
				Collections.sort(scores);
				int positionLocal = scores.indexOf(hs);
				hs.setRank(positionLocal);
				JSONObject json = null;
				HttpPost httpPost = new HttpPost(Constants.API_URL_SCORE_RANK);
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair(Constants.SECURITY_TOKEN, Constants.VALUE_SECRET));
				nvps.add(new BasicNameValuePair(Constants.KEY_PAYLOAD, hs.toJSON().toString()));
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				HttpResponse response = null;
				try {
					response = WordSearchActivity.httpClient.execute(httpPost);
				} catch (IllegalStateException ise) {
					WordSearchActivity.httpClient = AndroidHttpClient.newInstance("wordsearch");
					response = WordSearchActivity.httpClient.execute(httpPost);
				}
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				response.getEntity().writeTo(baos);
				json = new JSONObject(baos.toString());
				hs.setGlobalHighScore(json.getBoolean(Constants.KEY_GLOBAL_HIGH_SCORE));
				hs.setGlobalRank(json.getInt(Constants.KEY_GLOBAL_RANK));
				return true;
			} catch (Exception e) {
				if (hs != null) {
					hs.setGlobalRank(-1);
				}
				return false;
			}
//			Debug.stopMethodTracing();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			try {
				if (pd.isShowing()) {
					pd.dismiss();
					if (getCurrentHighScore() != null && !this.isCancelled() && WordSearchActivityController.this.isVisible()) {
						wordSearch.showDialog(WordSearchActivity.DIALOG_ID_GAME_OVER);
					}
				}
			} catch (BadTokenException bte) {
				// activity no longer displayed
				Log.e(LOG_TAG, bte.getMessage());
			} catch (IllegalArgumentException iae) {
				// activity no longer displayed
				Log.e(LOG_TAG, iae.getMessage());
			}
		}

		@Override
		protected void onPreExecute() {
			ConnectivityManager conman = (ConnectivityManager)wordSearch.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (getCurrentHighScore() == null || conman.getActiveNetworkInfo() == null || !conman.getActiveNetworkInfo().isConnected()) {
				this.cancel(true);
				wordSearch.showDialog(WordSearchActivity.DIALOG_ID_GAME_OVER);
			} else {
				pd.setMessage(wordSearch.getString(R.string.HIGH_SCORE_CALCULATING));
				pd.setIndeterminate(true);
				pd.show();
			}
		}
	}
	private Theme theme = Theme.NIGHTSKY;
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
	private static final String BUNDLE_THEME = "ws_theme";
	private static final String BUNDLE_THEME_STR = "ws_theme_string";
	private static final String BUNDLE_HIGH_SCORE = "ws_high_score";
	public WordSearchActivityController(WordSearchActivity wordSearch) {
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
			gridManager = new TextViewGridController(this);
		}
		this.setLetter("l");
		this.setLetter(null);
		prefs.getTouchMode();
		this.updateTouchMode();
	}

	protected void foundWord(String word) {
		int remainingWordCount = wordBoxManager.wordFound(word);
		if (remainingWordCount == 0) {
			Long diffMill = System.currentTimeMillis() - timeStart + timeSum;
			setHighScore(diffMill);
		}
	}

	public HighScore getCurrentHighScore() {
		return hs;
	}

	public String getCurrentTheme() {
		return dictionaryFactory.getCurrentTheme();
	}

	public TextViewGridController getGridManager() {
		return gridManager;
	}

	public int getGridSize() {
		return grid.getSize();
	}

	public LinkedList<HighScore> getHighScores() {
		return prefs.getTopScores();
	}
	
	public Preferences getPrefs() {
		return prefs;
	}

	public Theme getTheme() {
		return theme;
	}
	public String guessWord(Point pointStart, Point pointEnd) {
		return grid.guessWord(pointStart, pointEnd);
	}
	public boolean isGameRunning() {
		return wordBoxManager.wordsLeft() != 0;
	}
	public void newWordSearch() {
		String category = PreferenceManager.getDefaultSharedPreferences(wordSearch).getString(wordSearch.getString(R.string.prefs_category), wordSearch.getString(R.string.RANDOM));
		String themeStr = PreferenceManager.getDefaultSharedPreferences(wordSearch).getString(wordSearch.getString(R.string.PREFS_THEME), Theme.ORIGINAL.toString());
		this.theme = Theme.valueOf(themeStr);
		if (this.theme == null) {
			this.theme = Theme.ORIGINAL;
		}
		grid = Grid.generateGrid(dictionaryFactory.getDictionary(category), 12, 4, prefs.getSize());
		wordSearch.setupViewGrid();
		gridManager.reset(grid);
		if (grid.getWordListLength() == 0) {
			if (dictionaryFactory.isCustomDictionary()) {
				wordSearch.showDialog(WordSearchActivity.DIALOG_ID_NO_WORDS_CUSTOM);
			} else {
				wordSearch.showDialog(WordSearchActivity.DIALOG_ID_NO_WORDS);
			}
		}
		timeSum = 0L;
		hs = null;
		this.setGrid(grid);
		getTheme().reset(grid.getWordListLength());
		wordSearch.trackGame();
	}

	public void resetGrid() {
		grid.reset();
		gridManager.reset(grid);
		this.setGrid(grid);
		getTheme().reset(grid.getWordListLength());
		timeSum = 0L;
		hs = null;
		wordSearch.trackReplay();
		wordSearch.trackGame();
	}

	public void resetScores() {
		prefs.resetTopScores();
	}

	public void restoreState(Bundle inState) {
		if (inState != null) {
			this.theme = Theme.valueOf(inState.getString(BUNDLE_THEME_STR));
			this.theme.fromBundle(inState.getBundle(BUNDLE_THEME));
			Bundle hsBundle = inState.getBundle(BUNDLE_HIGH_SCORE);
			if (hsBundle != null) {
				hs = new HighScore(hsBundle);
			} else {
				hs = null;
			}
			this.grid = inState.getParcelable(BUNDLE_GRID);
			this.setGrid(grid);
			wordSearch.setupViewGrid();
			gridManager.reset(grid);
			this.gridManager.fromBundle(inState.getBundle(BUNDLE_VIEW));
			this.timeSum = inState.getLong(BUNDLE_TIME, 0);
		} else {
			this.newWordSearch();
		}
	}
	
	public void saveState(Bundle outState) {
		if (outState != null) {
			this.timePause();
			outState.putLong(BUNDLE_TIME, this.timeSum);
			outState.putParcelable(BUNDLE_GRID, this.grid);
			outState.putBundle(BUNDLE_VIEW, this.gridManager.toBundle());
			outState.putBundle(BUNDLE_THEME, this.theme.toBundle());
			outState.putString(BUNDLE_THEME_STR, this.theme.toString());
			if (this.hs != null) {
				outState.putBundle(BUNDLE_HIGH_SCORE, this.hs.toBundle());
			}
		}
	}

	private void setGrid(Grid grid) {
		wordBoxManager.resetWords(grid.getWordList());
		timeStart = System.currentTimeMillis();
	}

	private void setHighScore(long time) {
		hs = new HighScore(time, getGridSize(), dictionaryFactory.getCurrentTheme(), this.wordBoxManager.getWordsFount());
		wordSearch.runOnUiThread(new GameOver());
	}

	public void setLetter(CharSequence charSequence) {
		wordBoxManager.setLetter(charSequence);
	}

	public void timePause() {
		if (timeStart != 0) {
			timeSum += System.currentTimeMillis() - timeStart;
			timeStart = 0;
		}
	}

	public void timeResume() {
		timeStart = System.currentTimeMillis();
	}

	public boolean isVisible() {
		return timeStart != 0;
	}
	public void updateTouchMode() {
		this.gridManager.setTouchMode(prefs.getTouchMode());
	}

	public boolean isReplaying() {
		return grid.isReplaying();
	}
}
