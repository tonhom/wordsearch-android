package com.dahl.brendan.wordsearch.view.runnables;

import android.widget.ArrayAdapter;

public class UpdateWordList implements Runnable {
	final private ArrayAdapter<String> adapter;
	public UpdateWordList(ArrayAdapter<String> adapter) {
		this.adapter = adapter;
	}
	public void run() {
		this.adapter.notifyDataSetChanged();
	}

}
