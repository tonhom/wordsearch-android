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

package com.dahl.brendan.wordsearch.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dahl.brendan.wordsearch.util.ConversionUtil;
import com.dahl.brendan.wordsearch.view.WordDictionaryProvider.Word;
import com.dahl.brendan.wordsearch.view.controller.TextViewGridController;
import com.dahl.brendan.wordsearch.view.controller.WordSearchActivityController;
import com.dahl.brendan.wordsearch.view.runnables.HighScoresShow;

/**
 * 
 * @author Brendan Dahl
 *
 * Activity for the word search game itself
 */
public class WordSearchActivity extends Activity implements OnClickListener {
//	final private static String LOG_TAG = "WordSearchActivity";
	/**
	 * control classes were made to segment the complex game logic away from the display logic
	 */
	private WordSearchActivityController control;

	/**
	 * shows alert to user about their being no words in their game
	 */
	public void alertNoWords() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (control.isCurrentThemeCustom()) {
			builder.setMessage(R.string.no_words_custom);
			builder.setNegativeButton(R.string.category, this);
			builder.setPositiveButton(R.string.custom_editor, this);
		} else {
			builder.setMessage(R.string.no_words);
			builder.setNegativeButton(R.string.category, this);
			builder.setPositiveButton(R.string.new_game,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							control.newWordSearch();
						}
					});
			builder.setNeutralButton(R.string.size,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							showSizeSelector();
						}
					});
		}
		builder.show();
	}

	/**
	 * onclick for alert dialog on no words alerts
	 */
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			Intent intent = new Intent(Intent.ACTION_EDIT, com.dahl.brendan.wordsearch.view.WordDictionaryProvider.Word.CONTENT_URI);
			intent.setType(com.dahl.brendan.wordsearch.view.WordDictionaryProvider.Word.CONTENT_TYPE);
			startActivity(intent);
			break;
		case DialogInterface.BUTTON_NEUTRAL:
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			showCategorySelector();
			break;
		default:
			control.setTheme(which);
			control.newWordSearch();
			break;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Log.v(LOG_TAG, "onCreate");
		setContentView(R.layout.wordsearch_main);
		control = new WordSearchActivityController(this, savedInstanceState);
		final Bundle savedInstanceStateInner = savedInstanceState;
		findViewById(R.id.wordsearch_base).post(new Runnable() {
			public void run() {
				setupViewGrid(WordSearchActivityController.getGridSize(), control.getGridManager());
				if (savedInstanceStateInner == null) {
					control.newWordSearch();
				} else {
					control.restoreState(savedInstanceStateInner);
				}
			}
		});
	}

	/** hook into menu button for activity */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.wordsearch_options, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/** when menu button option selected */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_scores:
			HighScoresShow hsShow = new HighScoresShow(control, this, false);
			hsShow.run();
			return true;
		case R.id.menu_theme:
			this.showCategorySelector();
			return true;
		case R.id.menu_new:
			control.newWordSearch();
			return true;
		case R.id.menu_custom:
			Intent intent = new Intent(Intent.ACTION_EDIT, Word.CONTENT_URI);
			intent.setType(Word.CONTENT_TYPE);
			this.startActivity(intent);
			return true;
		case R.id.menu_tutorial:
			this.showTutorial();
			return true;
		case R.id.menu_size:
			this.showSizeSelector();
			return true;
		case R.id.menu_input_type:
			this.showInputTypeSelector();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
//		Log.v(LOG_TAG, "onPause");
		control.timePause();
	}

	@Override
	protected void onResume() {
		super.onResume();
//		Log.v(LOG_TAG, "onResume");
		control.timeResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
//		Log.v(LOG_TAG, "onSaveInstanceState");
		control.saveState(outState);
	}

	/**
	 * creates a grid of textViews from layout files based on the gridSize
	 *  and sets the new textViews to use the controller as their listener
	 * 
	 * @param gridSize square size of the new grid to make
	 * @param controller the onkeyListener used for the grid's textViews, also holds the gridView an array of the new textView's in the grid
	 */
	public void setupViewGrid(int gridSize, TextViewGridController controller) {
		ViewGroup gridTable = (ViewGroup) this.findViewById(R.id.gridTable);
		gridTable.removeAllViews();
		int portion = gridTable.getHeight()/gridSize;
		gridTable.setKeepScreenOn(true);
		Point point = new Point();
		TextView[][] gridView = controller.getGridView();
		for (point.y = 0; point.y < gridSize; point.y++) {
			ViewGroup row = (ViewGroup)this.getLayoutInflater().inflate(R.layout.grid_row, null);
			row.setMinimumHeight(portion);
			TextView[] rowText = new TextView[gridSize];
			for (point.x = 0; point.x < gridSize; point.x++) {
				TextView view = (TextView)this.getLayoutInflater().inflate(R.layout.grid_text_view, null);
				view.setHeight(portion);
//				Log.v(LOG_TAG, "point: "+point+"; id: "+ConversionUtil.convertPointToID(point)+"; point2: "+ConversionUtil.convertIDToPoint(ConversionUtil.convertPointToID(point)));
				view.setId(ConversionUtil.convertPointToID(point));
				view.setInputType(InputType.TYPE_NULL);

				view.setOnKeyListener(controller);

				rowText[point.x] = view;
				row.addView(view);
			}
			gridView[point.y] = rowText;
			gridTable.addView(row);
			row.requestLayout();
			row.invalidate();
		}
		gridTable.setOnTouchListener(controller);
	}
	
	/**
	 * show an alert dialog to pick a theme for the next game
	 */
	private void showCategorySelector() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.category) + " : " + control.getCurrentTheme());
		builder.setItems(control.getThemes(), this);
		builder.show();
	}

	/**
	 * show an alert dialog to pick a size for the next game
	 */
	private void showSizeSelector() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String[] sizes = this.getResources().getStringArray(R.array.sizes);
		builder.setTitle(this.getString(R.string.size) + " : " + sizes[WordSearchActivityController.getGridSize()-7]);
		builder.setItems(sizes,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				control.setGridSize(whichButton+7);
				control.newWordSearch();
			}
		});
		builder.show();
	}

	/**
	 * show an alert dialog to pick a whether to use click or touch input
	 */
	private void showInputTypeSelector() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (control.getTouchMode()) {
			builder.setTitle(this.getString(R.string.input_type) + " : " + this.getString(R.string.drag));
		} else {
			builder.setTitle(this.getString(R.string.input_type) + " : " + this.getString(R.string.tap));
		}
		builder.setItems(R.array.input_types,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				control.setTouchMode(whichButton == 0);
			}
		});
		builder.show();
	}

	/**
	 * starts the tutorial activity
	 */
	private void showTutorial() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setClass(this, TutorialActivity.class);
		startActivity(intent);
	}
}
