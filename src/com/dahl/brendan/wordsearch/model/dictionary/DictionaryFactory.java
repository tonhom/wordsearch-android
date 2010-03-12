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
	public enum DictionaryType {
		PLACES,
		ANIMALS,
		PEOPLE,
		ADJECTIVES,
		GERMAN,
		MISC,
		INSANE,
		NUMBERS,
		CUSTOM,
		RANDOM;
	}
	private static final String LOG_TAG = "DICTIONARYFACTORY";
	public static final int MAX_TRIES = 5;
	private Random random = new Random();
	private final Context ctx;
	private DictionaryType currentDictionaryType = DictionaryType.RANDOM;
	private Dictionary currentDic = null;
	
	/**
	 * @param ctx application context used to access resources
	 */
	public DictionaryFactory(Context ctx) {
		this.ctx = ctx;
	}
	
	/**
	 * @return if we are currently using a custom dictionary provider
	 */
	public Boolean isCustomDictionary() {
		return DictionaryType.CUSTOM.equals(currentDictionaryType);
	}

	/**
	 * @param themeIndex constant representing a dictionary type
	 * @return new dictionary of the requested type
	 */
	private Dictionary getDictionary(DictionaryType themeIndex) {
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
		case GERMAN:
			dic = new DictionaryStringArray(ctx.getResources().getStringArray(R.array.german));
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
		case RANDOM:
			dic = getDictionary(DictionaryType.values()[random.nextInt(DictionaryType.values().length-4)]);
			break;
		}
		return dic;
	}
	
	public Dictionary getDictionary(String type) {
		DictionaryType newType = DictionaryType.RANDOM;
		try {
			newType = DictionaryType.valueOf(type);
		} catch (Exception e) {
//			Log.v(LOG_TAG, "DictionaryType unknown: ",e);
			newType = DictionaryType.RANDOM;
		}
		// if random or custom dictionary types creates a new dictionary based on the type
		switch (newType) {
		case RANDOM:
			this.currentDic = getDictionary(DictionaryType.RANDOM);
			break;
		case CUSTOM:
			this.currentDic = getDictionary(DictionaryType.CUSTOM);
			break;
		default:
			if (!newType.equals(currentDictionaryType)) {
				this.currentDic = getDictionary(newType);
			}
			break;
		}
		currentDictionaryType = newType;
		return this.currentDic;
	}

	/**
	 * @return a score multiplier based on the expected difficulty of the current dictionary provider
	 */
	public float getScoreThemeMultiplier() {
		float score = 1;
		switch (this.currentDictionaryType) {
		case PLACES:
		case ANIMALS:
		case PEOPLE:
		case ADJECTIVES:
		case GERMAN:
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
		case RANDOM:
			score = 95;
			break;
		}
		return score/100;
	}
}
