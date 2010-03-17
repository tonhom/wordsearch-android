package com.dahl.brendan.wordsearch.view;

import java.util.Arrays;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class WordSearchPreferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener { 
	@Override 
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		addPreferencesFromResource(R.xml.preferences);
		this.updateCategorySummary();
		this.updateSizeSummary();
		this.updateTouchmodeSummary();
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}

	private void updateCategorySummary() {
		String categorySum = this.getString(R.string.prefs_category_summary);
		Preference p = this.findPreference(this.getString(R.string.prefs_category));
		String category = p.getSharedPreferences().getString(p.getKey(), getString(R.string.RANDOM));
		List<String> catValues = Arrays.asList(this.getResources().getStringArray(R.array.categories_list_values));
		String[] catLabels = this.getResources().getStringArray(R.array.categories_list_labels);
		int index = catValues.indexOf(category);
		categorySum = categorySum.replaceAll("%replaceme", catLabels[index]);
		p.setSummary(categorySum);
	}

	private void updateSizeSummary() {
		String sizeSum = this.getString(R.string.prefs_size_summary);
		Preference p = this.findPreference(this.getString(R.string.prefs_size));
		String size = p.getSharedPreferences().getString(p.getKey(), getString(R.string.SIZE_DEFAULT));
		List<String> sizeValues = Arrays.asList(this.getResources().getStringArray(R.array.sizes_list_values));
		String[] sizeLabels = this.getResources().getStringArray(R.array.sizes_list_labels);
		int index = sizeValues.indexOf(size);
		sizeSum = sizeSum.replaceAll("%replaceme", sizeLabels[index]);
		p.setSummary(sizeSum);
	}

	private void updateTouchmodeSummary() {
		String touchmodeSum = this.getString(R.string.prefs_touch_mode_summary);
		Preference p = this.findPreference(this.getString(R.string.prefs_touch_mode));
		String touchmode = p.getSharedPreferences().getString(p.getKey(), getString(R.string.TAP));
		List<String> modeValues = Arrays.asList(this.getResources().getStringArray(R.array.touch_mode_list_values));
		String[] modeLabels = this.getResources().getStringArray(R.array.touch_mode_list_labels);
		int index = modeValues.indexOf(touchmode);
		touchmodeSum = touchmodeSum.replaceAll("%replaceme", modeLabels[index]);
		p.setSummary(touchmodeSum);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.preferences_options, menu);
		menu.findItem(R.id.menu_quit).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_quit:
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (this.getString(R.string.prefs_category).equals(key)) {
			this.updateCategorySummary();
		} else if (this.getString(R.string.prefs_size).equals(key)) {
			this.updateSizeSummary();
		} else if (this.getString(R.string.prefs_touch_mode).equals(key)) {
			this.updateTouchmodeSummary();
		}
	}
}
