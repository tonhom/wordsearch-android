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
package com.firegnom.rat.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.firegnom.rat.ExceptionActivity;

public class HttpPoster {
  private URI url;

  public HttpPoster(URI url) {
    this.url = url;
  }
  public HttpPoster(String url) {
    try {
      this.url = new URI(url);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  public void sendStackTrace(String token,String applicationVer,String appPkg,String phoneModel,String androidVersion,String stackTrace,String additionalData) {
    DefaultHttpClient httpClient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(url);
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    nvps.add(new BasicNameValuePair(ExceptionActivity.SECURITY_TOKEN, token));
    nvps.add(new BasicNameValuePair(ExceptionActivity.APPLICATION_VERSION, applicationVer));
    nvps.add(new BasicNameValuePair(ExceptionActivity.APPLICATION_PACKAGE, appPkg));
    nvps.add(new BasicNameValuePair(ExceptionActivity.PHONE_MODEL, phoneModel));
    nvps.add(new BasicNameValuePair(ExceptionActivity.ANDROID_VERSION, androidVersion));
    nvps.add(new BasicNameValuePair(ExceptionActivity.APPLICATION_STACKTRACE, stackTrace));
    nvps.add(new BasicNameValuePair(ExceptionActivity.ADDITIONAL_DATA, additionalData));
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
  }

}
