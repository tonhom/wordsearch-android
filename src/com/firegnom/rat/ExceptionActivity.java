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
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

	private String preview;

	private HttpPoster poster = new HttpPoster(getUrl());

	final private String[] msgs = new String[] {
			"Sending Please wait...",
			"We are very sorry but Application has crashed unexpectedly.\n\nPlease help us make our application better by sending us stack trace",
			"Preview", "Send", "Exit" };

	private void buildTrace(Intent i) {
		ver = i.getStringExtra(APPLICATION_VERSION);
		aVer = i.getStringExtra(ANDROID_VERSION);
		pac = i.getStringExtra(APPLICATION_PACKAGE);
		model = i.getStringExtra(PHONE_MODEL);
		trace = i.getStringExtra(APPLICATION_STACKTRACE);

		preview = "Application ver: ----ver----\nAndroid ver: ----aVer----\nPackage: ----pac----\nPhone model: ----model----\nDetails: ----details----\nStackTrace:\n----trace----";
		preview = preview.replace("----ver----", ver + "");
		preview = preview.replace("----aVer----", aVer + "");
		preview = preview.replace("----pac----", pac + "");
		preview = preview.replace("----model----", model + "");
		preview = preview.replace("----details----", getMoreDetails() + "");
		preview = preview.replace("----trace----", trace + "");

	}

	/**
	 * 
	 * @return
	 */
	public String[] getMessages() {
		return msgs;
	}

	/**
	 * 
	 * @return
	 */
	public abstract String getMoreDetails();

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

	/**
	 * 
	 * @return
	 */
	public abstract boolean isSend();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Intent i = getIntent();
		buildTrace(i);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		TextView tv = new TextView(this);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int height = dm.heightPixels;
		int width = dm.widthPixels;

		tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				height - 100));
		String msg = getMessages()[1];
		tv.setTextSize(20);
		tv.setText(msg);
		LinearLayout ll1 = new LinearLayout(this);
		ll1.setOrientation(LinearLayout.HORIZONTAL);
		ll1.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		boolean send = isSend();
		int buttonWidth = !send ? width : width / 3;
		if (send) {
			Button b = new Button(this);
			b.setLayoutParams(new LayoutParams(buttonWidth,
					LayoutParams.FILL_PARENT));
			b.setText(getMessages()[2]);
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					showDialog(TRACE_DIALOG);
				}
			});

			ll1.addView(b);
			Button b1 = new Button(this);
			b1.setLayoutParams(new LayoutParams(buttonWidth,
					LayoutParams.FILL_PARENT));
			b1.setText(getMessages()[3]);
			b1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					sendTrace();
				}
			});
			ll1.addView(b1);
		}
		Button b2 = new Button(this);
		b2.setLayoutParams(new LayoutParams(buttonWidth,
				LayoutParams.FILL_PARENT));
		b2.setText(getMessages()[4]);
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Process.killProcess(Process.myPid());
			}
		});
		ll1.addView(b2);

		ll.addView(tv);
		ll.addView(ll1);
		setContentView(ll);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case TRACE_DIALOG:
			builder.setMessage(preview).setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			return builder.create();
		case PROGRESS_DIALOG:
			return ProgressDialog.show(this, "", this.getMessages()[0], true);
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
