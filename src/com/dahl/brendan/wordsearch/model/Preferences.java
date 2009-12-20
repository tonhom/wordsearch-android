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

package com.dahl.brendan.wordsearch.model;

import java.util.Collections;
import java.util.LinkedList;

import android.content.Context;
import android.content.SharedPreferences;

import com.dahl.brendan.wordsearch.model.dictionary.DictionaryFactory;

public class Preferences {
	private static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREFS_THEME = "theme";
	private static final String PREFS_SCORE_TIME = "score_time";
	private static final String PREFS_SCORE_NAME = "score_name";
	private static final String PREFS_SCORE_THEME = "score_theme";
	private static final String PREFS_SCORE_SIZE = "score_size";
	private static final String PREFS_TOUCHMODE = "touch_mode";
	private static final String PREFS_SEPARATOR = ":";
//	private static final String LOG_TAG = "Preferences";
	private static final String PREFS_SIZE = "size";
	private final SharedPreferences settings;

	private static int GRID_SIZE_DEFAULT = 10;

	public Preferences(Context ctx) {
		settings = ctx.getSharedPreferences(PREFS_NAME, 0);
	}

	public int getSize() {
		return settings.getInt(PREFS_SIZE, GRID_SIZE_DEFAULT);
	}

	public int getTheme() {
		return settings.getInt(PREFS_THEME, DictionaryFactory.INIT);
	}

	private String getTopScoreNameKey(int level) {
		return PREFS_SCORE_NAME + PREFS_SEPARATOR + Integer.toString(level);
	}

	private String getTopScoreTimeKey(int level) {
		return PREFS_SCORE_TIME + PREFS_SEPARATOR + Integer.toString(level);
	}
	
	private String getTopScoreThemeKey(int level) {
		return PREFS_SCORE_THEME + PREFS_SEPARATOR + Integer.toString(level);
	}
	
	private String getTopScoreSizeKey(int level) {
		return PREFS_SCORE_SIZE + PREFS_SEPARATOR + Integer.toString(level);
	}
	
	final private static int MAX_TOP_SCORES = 3;
	public LinkedList<HighScore> getTopScores() {
		LinkedList<HighScore> scores = new LinkedList<HighScore>();
		for (int level = 0; level < MAX_TOP_SCORES; level++) {
			long time = settings.getLong(getTopScoreTimeKey(level), -1);
			String name = settings.getString(getTopScoreNameKey(level), "");
			int size = settings.getInt(getTopScoreSizeKey(level), -1);
			float theme = settings.getFloat(getTopScoreThemeKey(level), -1);
			if (time != -1) {
				HighScore highScore = new HighScore(time, size, theme);
				highScore.setInitials(name);
				scores.add(highScore);
			}
		}
		Collections.sort(scores);
		return scores;
	}

	public void setSize(int gridSize) {
		if (gridSize < 6 || gridSize > 10) {
			gridSize = GRID_SIZE_DEFAULT;
		}
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(PREFS_SIZE, gridSize);
		editor.commit();
	}

	public void setTheme(int themeIndex) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(PREFS_THEME, themeIndex);
		editor.commit();
	}

	public void setTopScores(LinkedList<HighScore> highScores) {
		SharedPreferences.Editor editor = settings.edit();
		Collections.sort(highScores);
		for (int level = 0; level < MAX_TOP_SCORES; level++) {
			if (level < highScores.size()) {
				HighScore highScore = highScores.get(level);
				editor.putLong(getTopScoreTimeKey(level), highScore.getTime());
				editor.putString(getTopScoreNameKey(level), highScore.getInitials());
				editor.putInt(getTopScoreSizeKey(level), highScore.getSize());
				editor.putFloat(getTopScoreThemeKey(level), highScore.getThemeModifier());
			} else {
				editor.remove(getTopScoreTimeKey(level));
				editor.remove(getTopScoreNameKey(level));
				editor.remove(getTopScoreSizeKey(level));
				editor.remove(getTopScoreThemeKey(level));
			}
		}
		editor.commit();
	}

	public void resetTopScores() {
		SharedPreferences.Editor editor = settings.edit();
		for (int level = 0; level < MAX_TOP_SCORES; level++) {
			editor.remove(getTopScoreTimeKey(level));
			editor.remove(getTopScoreNameKey(level));
			editor.remove(getTopScoreSizeKey(level));
			editor.remove(getTopScoreThemeKey(level));
		}
		editor.commit();
	}

	public void setTouchMode(boolean touchMode) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PREFS_TOUCHMODE, touchMode);
		editor.commit();
	}
	
	public boolean getTouchMode() {
		return settings.getBoolean(PREFS_TOUCHMODE, true);
	}
}