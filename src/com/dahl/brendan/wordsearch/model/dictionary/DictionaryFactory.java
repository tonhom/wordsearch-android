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

package com.dahl.brendan.wordsearch.model.dictionary;

import java.util.Random;

import android.content.Context;
import android.util.Log;

import com.dahl.brendan.wordsearch.view.R;

/**
 * this class creates any of the defined Dictionaries
 * 
 * @author Brendan Dahl
 *
 */
public class DictionaryFactory {
	private static final int PLACES = 0;
	private static final int ANIMALS = 1;
	private static final int PEOPLE = 2;
	private static final int ADJECTIVES = 3;
	private static final int MISC = 4;
	private static final int INSANE = 5;
	private static final int NUMBERS = 6;
	private static final int CUSTOM = 7;
	private static final int RANDOM = 8;
	public static final int INIT = -1;
	private static final String LOG_TAG = "DICTIONARYFACTORY";
	public static final int MAX_TRIES = 5;
	private Random random = new Random();
	private final String[] themes;
	private final Context ctx;
	private int themeIndex = RANDOM;
	private Dictionary currentDic = null;
	
	/**
	 * @param ctx application context used to access resources
	 */
	public DictionaryFactory(Context ctx) {
		this.ctx = ctx;
		String[] themes = {
				ctx.getString(R.string.places),
				ctx.getString(R.string.animals),
				ctx.getString(R.string.people),
				ctx.getString(R.string.adjectives),
				ctx.getString(R.string.misc),
				ctx.getString(R.string.insane),
				ctx.getString(R.string.numbers),
				ctx.getString(R.string.custom),
				ctx.getString(R.string.random)
		};
		this.themes = themes;
		this.resetDictionary();
	}
	
	/**
	 * @return currently chosen dictionary
	 */
	public Dictionary getCurrentDic() {
		return currentDic;
	}

	/**
	 * @return name of the currently chosen dictionary
	 */
	public String getCurrentTheme() {
		int themeI = RANDOM;
		if (themeIndex >= 0 && themeIndex <= RANDOM) {
			themeI = themeIndex;
		}
		return this.themes[themeI];
	}
	
	/**
	 * @return if we are currently using a custom dictionary provider
	 */
	public Boolean isCustomDictionary() {
		return this.themeIndex == CUSTOM;
	}

	/**
	 * @param themeIndex constant representing a dictionary type
	 * @return new dictionary of the requested type
	 */
	private Dictionary getDictionary(int themeIndex) {
		Dictionary dic = null;
		switch (themeIndex) {
		case PLACES:
			dic = new DictionaryStringArray(ctx.getResources().getStringArray(R.array.places));
			break;
		case ANIMALS:
			dic = new DictionaryStringArray(ctx.getResources().getStringArray(R.array.animals));
			break;
		case PEOPLE:
			dic = new DictionaryStringArray(ctx.getResources().getStringArray(R.array.people));
			break;
		case ADJECTIVES:
			dic = new DictionaryStringArray(ctx.getResources().getStringArray(R.array.adjectives));
			break;
		case MISC:
			dic = new DictionaryFlat(ctx, "dictionary");
			break;
		case INSANE:
			dic = new DictionaryLetters();
			break;
		case NUMBERS:
			dic = new DictionaryNumbers();
			break;
		case CUSTOM:
			dic = new DictionaryCustomProvider(ctx);
			break;
		default:
			Log.e(LOG_TAG, "invalid index received");
		case INIT:
		case RANDOM:
			dic = getDictionary(random.nextInt(MISC+1));
			break;
		}
		return dic;
	}
	
	/**
	 * @return an array of names of the current types of dictionary available
	 */
	public String[] getThemes() {
		return themes;
	}

	/**
	 * if random or cutsom dictionary types creates a new dictionary based on the type
	 */
	public void resetDictionary() {
		if (themeIndex == RANDOM) {
			this.currentDic = getDictionary(INIT);
		}
		if (themeIndex == CUSTOM) {
			this.currentDic = getDictionary(CUSTOM);
		}
	}

	/**
	 * @param themeIndex the new dictionary type's theme index
	 */
	public void setTheme(int themeIndex) {
		this.themeIndex = themeIndex;
		this.currentDic = getDictionary(this.themeIndex);
	}

	/**
	 * @return a score multiplier based on the expected difficulty of the current dictionary provider
	 */
	public float getScoreThemeMultiplier() {
		float score = 1;
		switch (this.themeIndex) {
		case PLACES:
			score = 90;
			break;
		case ANIMALS:
			score = 90;
			break;
		case PEOPLE:
			score = 90;
			break;
		case ADJECTIVES:
			score = 90;
			break;
		case MISC:
			score = 95;
			break;
		case INSANE:
		case NUMBERS:
			score = 100;
			break;
		case CUSTOM:
			score = 80;
			break;
		case INIT:
		case RANDOM:
			score = 95;
			break;
		}
		return score/100;
	}
}
