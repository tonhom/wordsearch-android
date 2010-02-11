package com.dahl.brendan.wordsearch.view.listeners;

import android.content.DialogInterface;
import android.content.Intent;

import com.dahl.brendan.wordsearch.view.WordSearchActivity;
import com.dahl.brendan.wordsearch.view.WordSearchPreferences;

public class DialogNoWordsListener implements DialogInterface.OnClickListener {

	final private WordSearchActivity wordSearch;

	public DialogNoWordsListener (WordSearchActivity wordSearch) {
		this.wordSearch = wordSearch;
	}

	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			wordSearch.getControl().newWordSearch();
			break;
		case DialogInterface.BUTTON_NEUTRAL:
			wordSearch.startActivity(new Intent(wordSearch, WordSearchPreferences.class));
//			showSizeSelector(); TODO
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			wordSearch.startActivity(new Intent(wordSearch, WordSearchPreferences.class));
//			showCategorySelector(); TODO
			break;
		default:
			break;
		}
	}
}