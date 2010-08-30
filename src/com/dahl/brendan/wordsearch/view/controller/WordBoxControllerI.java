package com.dahl.brendan.wordsearch.view.controller;

import com.dahl.brendan.wordsearch.model.Grid;
import com.dahl.brendan.wordsearch.model.Theme;

public interface WordBoxControllerI {
	/**
	 * 
	 * @param charSequence sets the letter to show the user which letter is being touched
	 * 						null to hide the preview letter
	 */
	public void setLetter(CharSequence charSequence);

	/**
	 * removes a word from the list of words to find
	 * 
	 * @param str word to remove the list of words
	 * @return number of words left to find
	 */
	public int wordFound(String str);

	/**
	 * resets the list of words available to the user
	 * 
	 * @param wordList new list of available words
	 */
	public void resetWords(Grid grid);

	public int wordsLeft();	
	public void updateTheme(Theme theme);
}
