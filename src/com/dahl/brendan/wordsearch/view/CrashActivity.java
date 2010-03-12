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

	@Override
	public String[] getMessages() {
		String[] msgs = new String[5];
		msgs[0] = getString(R.string.CRASH_SENDING_WAIT);
		msgs[1] = getString(R.string.CRASH_MSG);
		msgs[2] = getString(R.string.PREVIEW);
		msgs[3] = getString(R.string.SEND);
		msgs[4] = getString(R.string.quit);
		return msgs;
	}

	@Override
	public String getMoreDetails() {
		return null;
	}

	@Override
	public boolean isSend() {
		return true;
	}
}