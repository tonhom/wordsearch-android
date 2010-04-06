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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.dahl.brendan.wordsearch.view.R;
import com.firegnom.rat.net.HttpPoster;

public abstract class ExceptionActivity extends Activity {
	// intent params
	public final static String APPLICATION_VERSION = "APPLICATION_VERSION";
	public final static String APPLICATION_PACKAGE = "APPLICATION_PACKAGE";
	public final static String APPLICATION_STACKTRACE = "APPLICATION_STACKTRACE";
	public final static String PHONE_MODEL = "PHONE_MODEL";
	public final static String ANDROID_VERSION = "ANDROID_VERSION";
	public final static String SECURITY_TOKEN = "SECURITY_TOKEN";
	public final static String ADDITIONAL_DATA = "ADDITIONAL_DATA";

	protected static final int TRACE_DIALOG = 1;
	protected static final int PROGRESS_DIALOG = 2;

	private String ver;
	private String trace;
	private String aVer;
	private String pac;
	private String model;

	private HttpPoster poster = new HttpPoster(getUrl());

	private void buildTrace(Intent i) {
		ver = i.getStringExtra(APPLICATION_VERSION);
		aVer = i.getStringExtra(ANDROID_VERSION);
		pac = i.getStringExtra(APPLICATION_PACKAGE);
		model = i.getStringExtra(PHONE_MODEL);
		trace = i.getStringExtra(APPLICATION_STACKTRACE);
	}

	private String getPreview() {
		String preview = "Application ver: ----ver----\nAndroid ver: ----aVer----\nPackage: ----pac----\nPhone model: ----model----\nDetails: ----details----\nStackTrace:\n----trace----";
		preview = preview.replace("----ver----", ver + "");
		preview = preview.replace("----aVer----", aVer + "");
		preview = preview.replace("----pac----", pac + "");
		preview = preview.replace("----model----", model + "");
		preview = preview.replace("----details----", getMoreDetails() + "");
		preview = preview.replace("----trace----", trace + "");
		return preview;
	}
	public String getMoreDetails() {
		return ((EditText)this.findViewById(R.id.CRASH_DETAILS)).getText().toString();
	}

	/**
	 * 
	 * @return
	 */
	public abstract String getSecurityToken();

	/**
	 * 
	 * @return
	 */
	public abstract String getUrl();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		buildTrace(i);
		this.setContentView(R.layout.crash_main);
		((Button)this.findViewById(R.id.CRASH_BUTTON_PREVIEW)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(TRACE_DIALOG);
			}
		});
		((Button)this.findViewById(R.id.CRASH_BUTTON_SEND)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendTrace();
			}
		});
		((Button)this.findViewById(R.id.CRASH_BUTTON_QUIT)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Process.killProcess(Process.myPid());
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case TRACE_DIALOG:
			builder.setMessage(getPreview()).setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			return builder.create();
		case PROGRESS_DIALOG:
			return ProgressDialog.show(this, "", getString(R.string.CRASH_SENDING_WAIT), true);
		}
		return null;
	}

	private void sendTrace() {
		showDialog(PROGRESS_DIALOG);
		new Thread() {
			public void run() {
				poster.sendStackTrace(getSecurityToken(), ver, pac, model,
						aVer, trace, getMoreDetails());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				Process.killProcess(Process.myPid());
			};
		}.start();

	}
}
