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

/**
 * 
 * @author Brendan Dahl
 *
 */
public class DictionaryLetters implements Dictionary {
	private final Random random = new Random();
	
	public String getNextWord(int minLength, int maxLength) {
		int length = minLength;
		int diff = maxLength-minLength;
		if (diff > 0) {
			length += random.nextInt(diff);
		}
		String str = "";
		for (int index = 0; index < length; index++) {
			str += (char)(random.nextInt(26)+((int)'A'));
		}
		return str;
	}

}
