//    This file is part of Open WordSearch.
//
//    Open WordSearch is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Open WordSearch is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Open WordSearch.  If not, see <http://www.gnu.org/licenses/>.
//
//	  Copyright 2009, 2010 Brendan Dahl <dahl.brendan@brendandahl.com>
//	  	http://www.brendandahl.com

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
//			tracker.stop();
		} catch (RuntimeException re) {
		} catch (Exception e) {
		}
		return "http://wordsearchapp.brendandahl.com/app/crash";
	}

}