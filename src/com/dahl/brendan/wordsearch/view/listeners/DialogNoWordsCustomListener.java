package com.dahl.brendan.wordsearch.view.listeners;

import android.content.DialogInterface;
import android.content.Intent;

import com.dahl.brendan.wordsearch.view.WordSearchActivity;
import com.dahl.brendan.wordsearch.view.WordSearchPreferences;

public class DialogNoWordsCustomListener implements DialogInterface.OnClickListener {

	final private WordSearchActivity wordSearch;

	public DialogNoWordsCustomListener (WordSearchActivity wordSearch) {
		this.wordSearch = wordSearch;
	}
	
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			Intent intent = new Intent(Intent.ACTION_EDIT, com.dahl.brendan.wordsearch.view.WordDictionaryProvider.Word.CONTENT_URI);
			intent.setType(com.dahl.brendan.wordsearch.view.WordDictionaryProvider.Word.CONTENT_TYPE);
			wordSearch.startActivity(intent);
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
