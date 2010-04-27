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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.dahl.brendan.wordsearch.Constants;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class CrashActivity extends Activity {

	protected static final int TRACE_DIALOG = 1;
	protected static final int PROGRESS_DIALOG = 2;

	private String ver;
	private String trace;
	private String aVer;
	private String pac;
	private String model;

	private void buildTrace(Intent i) {
		ver = i.getStringExtra(Constants.APPLICATION_VERSION);
		aVer = i.getStringExtra(Constants.ANDROID_VERSION);
		pac = i.getStringExtra(Constants.APPLICATION_PACKAGE);
		model = i.getStringExtra(Constants.PHONE_MODEL);
		trace = i.getStringExtra(Constants.APPLICATION_STACKTRACE);
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
			    DefaultHttpClient httpClient = new DefaultHttpClient();
			    HttpPost httpPost = new HttpPost(getUrl());
			    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			    nvps.add(new BasicNameValuePair(Constants.SECURITY_TOKEN, Constants.VALUE_SECRET));
			    nvps.add(new BasicNameValuePair(Constants.APPLICATION_VERSION, ver));
			    nvps.add(new BasicNameValuePair(Constants.APPLICATION_PACKAGE, pac));
			    nvps.add(new BasicNameValuePair(Constants.PHONE_MODEL, model));
			    nvps.add(new BasicNameValuePair(Constants.ANDROID_VERSION, aVer));
			    nvps.add(new BasicNameValuePair(Constants.APPLICATION_STACKTRACE, trace));
			    nvps.add(new BasicNameValuePair(Constants.ADDITIONAL_DATA, getMoreDetails()));
			    try {
			      httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			      httpClient.execute(httpPost);
			      
			      
			    } catch (UnsupportedEncodingException e) {
			      e.printStackTrace();
			    } catch (ClientProtocolException e) {
			      e.printStackTrace();
			    } catch (IOException e) {
			      e.printStackTrace();
			    }
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
		return Constants.API_URL_CRASH;
	}

}