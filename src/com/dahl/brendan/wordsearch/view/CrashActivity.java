package com.dahl.brendan.wordsearch.view;

import com.firegnom.rat.ExceptionActivity;

public class CrashActivity extends ExceptionActivity {

	@Override
	public String getSecurityToken() {
		return this.getString(R.string.KEY_SECRET);
	}

	@Override
	public String getUrl() {
		return "http://wordsearchapp.brendandahl.com/app/crash";
	}

}