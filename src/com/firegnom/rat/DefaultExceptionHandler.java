/*******************************************************************************
 * Copyright 2010 Maciej Kaniewski mk@firegnom.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.firegnom.rat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Process;
import android.util.Log;

import com.dahl.brendan.wordsearch.Constants;
import com.dahl.brendan.wordsearch.view.CrashActivity;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {

	private Context context;
	public static final String TAG = DefaultExceptionHandler.class.getName();
	private Class<? extends CrashActivity> a;

	// constructor
	public DefaultExceptionHandler(Context context,
			Class<? extends CrashActivity> a) {
		this.context = context;
		this.a = a;
	}

	// Default exception handler
	public void uncaughtException(Thread t, Throwable e) {
		Intent intent = new Intent(context, a);
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi;
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			intent.putExtra(Constants.APPLICATION_VERSION,
					pi.versionName);
			intent.putExtra(Constants.APPLICATION_PACKAGE,
					pi.packageName);
			intent.putExtra(Constants.PHONE_MODEL,
					android.os.Build.MODEL);
			intent.putExtra(Constants.ANDROID_VERSION,
					android.os.Build.VERSION.RELEASE);
			intent.putExtra(Constants.APPLICATION_STACKTRACE, result
					.toString());

		} catch (NameNotFoundException ex) {
			ex.printStackTrace();
		}
		context.startActivity(intent);
		Process.killProcess(Process.myPid());
	}

	public static boolean register(Context context,
			Class<? extends CrashActivity> a) {
		Log.i(TAG, "Registering default exceptions handler");

		UncaughtExceptionHandler currentHandler = Thread
				.getDefaultUncaughtExceptionHandler();
		if (currentHandler != null) {
			Log.d(TAG, "current handler class="
					+ currentHandler.getClass().getName());
		}
		// don't register again if already registered
		if (!(currentHandler instanceof DefaultExceptionHandler)) {
			// Register default exceptions handler
			Thread
					.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(
							context, a));
		}
		return false;
	}

}
