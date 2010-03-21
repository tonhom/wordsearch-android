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

import java.util.Date;
import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dahl.brendan.wordsearch.model.HighScore;
import com.dahl.brendan.wordsearch.util.ConversionUtil;
import com.dahl.brendan.wordsearch.view.WordDictionaryProvider.Word;
import com.dahl.brendan.wordsearch.view.controller.TextViewGridController;
import com.dahl.brendan.wordsearch.view.controller.WordSearchActivityController;
import com.firegnom.rat.DefaultExceptionHandler;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * 
 * @author Brendan Dahl
 *
 * Activity for the word search game itself
 */
public class WordSearchActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
	class DialogGameNewListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				getControl().newWordSearch();
			}
		}
	}
	class DialogHighScoresInitialsListener implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				String name = ((EditText)((AlertDialog)dialog).findViewById(android.R.id.input)).getText().toString();
				HighScore hs = getControl().getCurrentHighScore();
				if (!TextUtils.isEmpty(name)) {
					hs.setInitials(name);
				} else {
					hs.setInitials("?");
				}
				LinkedList<HighScore> scores = getControl().getPrefs().getTopScores();
				scores.add(hs);
				getControl().getPrefs().setTopScores(scores);
				showDialog(WordSearchActivity.DIALOG_ID_HIGH_SCORES_SHOW);
			} else {
				showDialog(WordSearchActivity.DIALOG_ID_GAME_NEW);
			}
			removeDialog(DIALOG_ID_HIGH_SCORES_INITIALS);
		}

		public void onCancel(DialogInterface dialog) {
			removeDialog(DIALOG_ID_HIGH_SCORES_INITIALS);
		}
	}
	class DialogHighScoresShowListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_NEGATIVE) {
				getControl().resetScores();
			}
			if (!getControl().isGameRunning()) {
				showDialog(WordSearchActivity.DIALOG_ID_GAME_NEW);
			}
		}
	}
	class DialogNoWordsCustomListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				Intent intent = new Intent(Intent.ACTION_EDIT, com.dahl.brendan.wordsearch.view.WordDictionaryProvider.Word.CONTENT_URI);
				intent.setType(com.dahl.brendan.wordsearch.view.WordDictionaryProvider.Word.CONTENT_TYPE);
				startActivity(intent);
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				startActivity(new Intent(WordSearchActivity.this, WordSearchPreferences.class));
				break;
			default:
				break;
			}
		}
	}
	class DialogNoWordsListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				getControl().newWordSearch();
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				startActivity(new Intent(WordSearchActivity.this, WordSearchPreferences.class));
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				startActivity(new Intent(WordSearchActivity.this, WordSearchPreferences.class));
				break;
			default:
				break;
			}
		}
	}
	class DialogIntroListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE: {
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WordSearchActivity.this);
				sp.edit().putString(getString(R.string.prefs_touch_mode), getString(R.string.TAP)).commit();
				break;
			}
			case DialogInterface.BUTTON_NEUTRAL:
				break;
			case DialogInterface.BUTTON_NEGATIVE: {
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WordSearchActivity.this);
				sp.edit().putString(getString(R.string.prefs_touch_mode), getString(R.string.DRAG)).commit();
				break;
			}
			default:
				break;
			}
		}
	}
	final public static int DIALOG_ID_NO_WORDS = 0;
	final public static int DIALOG_ID_NO_WORDS_CUSTOM = 1;
	final public static int DIALOG_ID_HIGH_SCORES_INITIALS = 2;
	final public static int DIALOG_ID_HIGH_SCORES_SHOW = 3;
	final public static int DIALOG_ID_GAME_NEW = 4;
	final private DialogIntroListener DIALOG_LISTENER_INTRO = new DialogIntroListener();
	final private DialogNoWordsListener DIALOG_LISTENER_NO_WORDS = new DialogNoWordsListener();
	final private DialogNoWordsCustomListener DIALOG_LISTENER_NO_WORDS_CUSTOM = new DialogNoWordsCustomListener();

	final private static String LOG_TAG = "WordSearchActivity";
	/**
	 * control classes were made to segment the complex game logic away from the display logic
	 */
	private WordSearchActivityController control;
	private GoogleAnalyticsTracker tracker = null;
	private String appVer;

	public WordSearchActivityController getControl() {
		return control;
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
			startActivity(new Intent(this, WordSearchPreferences.class));
			break;
		default:
			break;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DefaultExceptionHandler.register(this,CrashActivity.class);
		try {
			tracker = GoogleAnalyticsTracker.getInstance();
		} catch (RuntimeException re) {
			Log.e(LOG_TAG, "tracker failed!");
		} catch (Exception e) {
			Log.e(LOG_TAG, "tracker failed!");
		}
		try {
			appVer = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			appVer = "unknown";
		}
		try {
			tracker.start("UA-146333-5", 60, this);
			tracker.trackPageView("/app/"+appVer+"/WordSearchActivity");
		} catch (RuntimeException re) {
			Log.e(LOG_TAG, "tracker failed!");
		} catch (Exception e) {
			Log.e(LOG_TAG, "tracker failed!");
		}
		setContentView(R.layout.wordsearch_main);
		control = new WordSearchActivityController(this);
		control.restoreState(savedInstanceState);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		{
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			String key = getString(R.string.KEY_INTRO_VER);
			if (!appVer.equals(sp.getString(key, null)) && sp.getString(getString(R.string.prefs_touch_mode), null) == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.INTRO);
				builder.setPositiveButton(R.string.tap, DIALOG_LISTENER_INTRO);
				builder.setNeutralButton(android.R.string.cancel, DIALOG_LISTENER_INTRO);
				builder.setNegativeButton(R.string.drag, DIALOG_LISTENER_INTRO);
				builder.show();
				sp.edit().putString(key, appVer).commit();
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch(id) {
		case DIALOG_ID_NO_WORDS: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.no_words);
			builder.setNegativeButton(R.string.category, DIALOG_LISTENER_NO_WORDS);
			builder.setPositiveButton(R.string.new_game, DIALOG_LISTENER_NO_WORDS);
			builder.setNeutralButton(R.string.size, DIALOG_LISTENER_NO_WORDS);
			dialog = builder.create();
			break;
		}
		case DIALOG_ID_NO_WORDS_CUSTOM: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.no_words_custom);
			builder.setNegativeButton(R.string.category, DIALOG_LISTENER_NO_WORDS_CUSTOM);
			builder.setPositiveButton(R.string.custom_editor, DIALOG_LISTENER_NO_WORDS_CUSTOM);
			dialog = builder.create();
			break;
		}
		case DIALOG_ID_HIGH_SCORES_INITIALS: {
			final DialogHighScoresInitialsListener DIALOG_LISTENER_HIGH_SCORES_INITIALS = new DialogHighScoresInitialsListener();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("blank");
			EditText text = new EditText(this);
			text.setSingleLine();
			text.setId(android.R.id.input);
			builder.setView(text);
			builder.setPositiveButton(android.R.string.ok, DIALOG_LISTENER_HIGH_SCORES_INITIALS);
			builder.setNeutralButton(android.R.string.cancel, DIALOG_LISTENER_HIGH_SCORES_INITIALS);
			builder.setOnCancelListener(DIALOG_LISTENER_HIGH_SCORES_INITIALS);
			dialog = builder.create();
			break;
		}
		case DIALOG_ID_HIGH_SCORES_SHOW: {
			final DialogHighScoresShowListener DIALOG_LISTENER_HIGH_SCORES_SHOW = new DialogHighScoresShowListener();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("blank");
			builder.setTitle(R.string.high_score);
			builder.setNegativeButton(R.string.reset, DIALOG_LISTENER_HIGH_SCORES_SHOW);
			builder.setNeutralButton(android.R.string.ok, DIALOG_LISTENER_HIGH_SCORES_SHOW);
			dialog = builder.create();
			break;
		}
		case DIALOG_ID_GAME_NEW: {
			final DialogGameNewListener DIALOG_LISTENER_GAME_NEW = new DialogGameNewListener();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("blank");
			builder.setPositiveButton(R.string.new_game, DIALOG_LISTENER_GAME_NEW);
			builder.setNeutralButton(android.R.string.cancel, DIALOG_LISTENER_GAME_NEW);
			dialog = builder.create();
			break;
		}
		default:
			dialog = super.onCreateDialog(id);
			break;
		}
		return dialog;
	}

	/** hook into menu button for activity */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.wordsearch_options, menu);
		menu.findItem(R.id.menu_new).setIcon(android.R.drawable.ic_menu_add);
		menu.findItem(R.id.menu_options).setIcon(android.R.drawable.ic_menu_preferences);
		menu.findItem(R.id.menu_custom).setIcon(android.R.drawable.ic_menu_edit);
		menu.findItem(R.id.menu_tutorial).setIcon(android.R.drawable.ic_menu_help);
		menu.findItem(R.id.menu_scores).setIcon(android.R.drawable.ic_menu_gallery);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch(id) {
		case DIALOG_ID_HIGH_SCORES_INITIALS: {
			TextView label = (TextView)((AlertDialog)dialog).findViewById(android.R.id.message);
			HighScore hs = control.getCurrentHighScore();
			label.setText(this.getString(R.string.enter_initials).replace("%replaceme", hs.getScore().toString()+" ("+ConversionUtil.formatTime.format(new Date(hs.getTime()))+")"));
			((EditText)((AlertDialog)dialog).findViewById(android.R.id.input)).setText("");
			break;
		}
		case DIALOG_ID_HIGH_SCORES_SHOW: {
			LinkedList<HighScore> highScores = this.getControl().getHighScores();
			StringBuilder str = new StringBuilder();
			if (highScores.size() == 0) {
				str.append(this.getString(R.string.no_high_scores));
			} else {
				for (int index = 0; index < highScores.size(); index++) {
					str.append(Integer.toString(index+1)+": "+highScores.get(index).getInitials()+" " + highScores.get(index).getScore() + " ( " + ConversionUtil.formatTime.format(new Date(highScores.get(index).getTime())) + " )\n");
				}
			}
			TextView label = (TextView)((AlertDialog)dialog).findViewById(android.R.id.message);
			label.setText(str);
			break;
		}
		case DIALOG_ID_GAME_NEW: {
			String extraText;
			if (!this.getControl().isGameRunning()) {
				HighScore hs = control.getCurrentHighScore();
				extraText = this.getString(R.string.congratulations).replace("%replaceme",hs.getScore().toString()+" ("+ConversionUtil.formatTime.format(new Date(hs.getTime()))+")")+"\n";
			} else {
				extraText = "";
			}
			TextView label = (TextView)((AlertDialog)dialog).findViewById(android.R.id.message);
			label.setText(extraText+this.getString(R.string.game_over));
			break;
		}
		default:
			break;
		}
	}

	/** when menu button option selected */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_scores:
			this.showDialog(DIALOG_ID_HIGH_SCORES_SHOW);
			return true;
		case R.id.menu_options:
			startActivity(new Intent(this, WordSearchPreferences.class));
			return true;
		case R.id.menu_new:
			control.newWordSearch();
			return true;
		case R.id.menu_custom:
		{
			Intent intent = new Intent(Intent.ACTION_EDIT, Word.CONTENT_URI);
			intent.setType(Word.CONTENT_TYPE);
			this.startActivity(intent);
			return true;
		}
		case R.id.menu_tutorial:
		{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(this, TutorialActivity.class);
			startActivity(intent);
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
//		Log.v(LOG_TAG, "onPause");
		control.timePause();
		try {
			tracker.dispatch();
		} catch (RuntimeException re) {
			Log.e(LOG_TAG, "tracker failed!");
		} catch (Exception e) {
			Log.e(LOG_TAG, "tracker failed!");
		}
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
		try {
			tracker.dispatch();
		} catch (RuntimeException re) {
			Log.e(LOG_TAG, "tracker failed!");
		} catch (Exception e) {
			Log.e(LOG_TAG, "tracker failed!");
		}
		this.removeDialog(DIALOG_ID_HIGH_SCORES_INITIALS);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (control == null) {// hack fix later
			return;
		}
		if (this.getString(R.string.prefs_touch_mode).equals(key)) {
			control.updateTouchMode();
		}
	}

	/**
	 * creates a grid of textViews from layout files based on the gridSize
	 *  and sets the new textViews to use the controller as their listener
	 * 
	 * @param gridSize square size of the new grid to make
	 * @param controller the onkeyListener used for the grid's textViews, also holds the gridView an array of the new textView's in the grid
	 */
	public void setupViewGrid() {
		int gridSize = control.getGridSize();
		TextViewGridController controller = control.getGridManager();
		ViewGroup gridTable = (ViewGroup) this.findViewById(R.id.gridTable);
		if (gridTable.getChildCount() != gridSize) {
			if (gridTable.getChildCount() == 0) {
				gridTable.setKeepScreenOn(true);
				gridTable.setOnTouchListener(controller);
			}
			control.getGridManager().setPointDemension(null);
			gridTable.removeAllViews();
			Point point = new Point();
			controller.setGridView(new TextView[gridSize][]);
			TextView[][] gridView = controller.getGridView();
			for (point.y = 0; point.y < gridSize; point.y++) {
				this.getLayoutInflater().inflate(R.layout.grid_row, gridTable, true);
				ViewGroup row = (ViewGroup)gridTable.getChildAt(point.y);
				TextView[] rowText = new TextView[gridSize];
				for (point.x = 0; point.x < gridSize; point.x++) {
					this.getLayoutInflater().inflate(R.layout.grid_text_view, row, true);
					TextView view = (TextView)row.getChildAt(point.x);
					view.setId(ConversionUtil.convertPointToID(point, control.getGridSize()));
					view.setOnKeyListener(controller);

					rowText[point.x] = view;
				}
				gridView[point.y] = rowText;
			}
			gridTable.requestLayout();
		}
	}

	public void trackGame() {
		try {
			String category = control.getPrefs().getCategory();
			String input = "Tap";
			if (control.getPrefs().getTouchMode()) {
				input = "Drag";
			}
			tracker.trackEvent(category, input, appVer, control.getGridSize());
		} catch (RuntimeException re) {
			Log.e(LOG_TAG, "tracker failed!");
		} catch (Exception e) {
			Log.e(LOG_TAG, "tracker failed!");
		}
	}

}
