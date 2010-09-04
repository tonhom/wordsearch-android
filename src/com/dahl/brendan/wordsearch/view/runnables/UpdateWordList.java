package com.dahl.brendan.wordsearch.view.runnables;

import android.widget.ArrayAdapter;

import com.dahl.brendan.wordsearch.model.Grid;

public class UpdateWordList implements Runnable {
	final private ArrayAdapter<String> adapter;
	final private String word;
	final private Grid grid;
	public UpdateWordList(ArrayAdapter<String> adapter, String word, Grid grid) {
		this.adapter = adapter;
		this.word = word;
		this.grid = grid;
	}
	public void run() {
		if (word != null) {
			this.adapter.remove(word);
			this.adapter.add(word);
		}
		if (grid != null) {
			this.adapter.clear();
			for (String str : grid.getWordList()) {
				this.adapter.add(str);
			}
			for (String str : grid.getWordFound()) {
				this.adapter.add(str);
			}
		}
		this.adapter.notifyDataSetChanged();
	}

}
