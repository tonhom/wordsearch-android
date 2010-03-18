package com.dahl.brendan.wordsearch.view;

import android.content.pm.PackageManager.NameNotFoundException;

import com.firegnom.rat.ExceptionActivity;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class CrashActivity extends ExceptionActivity {

	@Override
	public String getSecurityToken() {
		return this.getString(R.string.KEY_SECRET);
	}

	@Override
	public String getUrl() {
		try {
			GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
			String appVer;
			try {
				appVer = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				appVer = "unknown";
			}
			tracker.start("UA-146333-5", this);
			tracker.trackPageView("/app/"+appVer+"/CrashActivity");
			tracker.dispatch();
			tracker.stop();
		} catch (RuntimeException re) {
		} catch (Exception e) {
		}
		return "http://wordsearchapp.brendandahl.com/app/crash";
	}

}