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
import android.preference.PreferenceManager;

import com.dahl.brendan.wordsearch.view.R;

public class Preferences {
	private final String PREFS_SIZE;
	private final String PREFS_TOUCHMODE;
	private final String PREFS_TOUCHMODE_DEFAULT;
	private final String PREFS_TOUCHMODE_DRAG;
	private final String PREFS_CATEGORY;

	private static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREFS_SCORE_TIME = "score_time";
	private static final String PREFS_SCORE_NAME = "score_name";
	private static final String PREFS_SCORE_THEME = "score_theme";
	private static final String PREFS_SCORE_SIZE = "score_size";
	private static final String PREFS_SEPARATOR = ":";
//	private static final String LOG_TAG = "Preferences";
	private final SharedPreferences settings_scores;
	private final SharedPreferences settings;

	private static int GRID_SIZE_DEFAULT = 10;

	final private static int MAX_TOP_SCORES = 3;

	public Preferences(Context ctx) {
		settings_scores = ctx.getSharedPreferences(PREFS_NAME, 0);
		settings = PreferenceManager.getDefaultSharedPreferences(ctx);
		PREFS_CATEGORY = ctx.getString(R.string.prefs_category);
		PREFS_SIZE = ctx.getString(R.string.prefs_size);
		PREFS_TOUCHMODE = ctx.getString(R.string.prefs_touch_mode);
		PREFS_TOUCHMODE_DEFAULT = ctx.getString(R.string.TAP);
		PREFS_TOUCHMODE_DRAG = ctx.getString(R.string.DRAG);
	}
	
	public String getCategory() {
		return settings.getString(PREFS_CATEGORY, "RANDOM");
	}

	public int getSize() {
		int size_int = GRID_SIZE_DEFAULT;
		try {
			String size = settings.getString(PREFS_SIZE, null);
			size_int = Integer.valueOf(size);
		} catch (Exception e) {
			size_int = GRID_SIZE_DEFAULT;
		}
		return size_int;
	}

	private String getTopScoreNameKey(int level) {
		return PREFS_SCORE_NAME + PREFS_SEPARATOR + Integer.toString(level);
	}
	
	public LinkedList<HighScore> getTopScores() {
		LinkedList<HighScore> scores = new LinkedList<HighScore>();
		for (int level = 0; level < MAX_TOP_SCORES; level++) {
			long time = settings_scores.getLong(getTopScoreTimeKey(level), -1);
			String name = settings_scores.getString(getTopScoreNameKey(level), "");
			int size = settings_scores.getInt(getTopScoreSizeKey(level), -1);
			float theme = settings_scores.getFloat(getTopScoreThemeKey(level), -1);
			if (time != -1) {
				HighScore highScore = new HighScore(time, size, theme);
				highScore.setInitials(name);
				scores.add(highScore);
			}
		}
		Collections.sort(scores);
		return scores;
	}
	
	private String getTopScoreSizeKey(int level) {
		return PREFS_SCORE_SIZE + PREFS_SEPARATOR + Integer.toString(level);
	}
	
	private String getTopScoreThemeKey(int level) {
		return PREFS_SCORE_THEME + PREFS_SEPARATOR + Integer.toString(level);
	}
	private String getTopScoreTimeKey(int level) {
		return PREFS_SCORE_TIME + PREFS_SEPARATOR + Integer.toString(level);
	}

	public boolean getTouchMode() {
		return PREFS_TOUCHMODE_DRAG.equals(settings.getString(PREFS_TOUCHMODE, PREFS_TOUCHMODE_DEFAULT));
	}

	public void resetTopScores() {
		SharedPreferences.Editor editor = settings_scores.edit();
		for (int level = 0; level < MAX_TOP_SCORES; level++) {
			editor.remove(getTopScoreTimeKey(level));
			editor.remove(getTopScoreNameKey(level));
			editor.remove(getTopScoreSizeKey(level));
			editor.remove(getTopScoreThemeKey(level));
		}
		editor.commit();
	}

	public void setTopScores(LinkedList<HighScore> highScores) {
		SharedPreferences.Editor editor = settings_scores.edit();
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
}