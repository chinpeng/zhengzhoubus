/*
 * Copyright 2012 Google Inc.
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
 */
package com.loveplusplus.zhengzhou.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.google.android.gcm.GCMRegistrar;
import com.loveplusplus.zhengzhou.Config;
import com.loveplusplus.zhengzhou.R;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();
	private static final String TAG = "ServerUtilities";

	/**
	 * Register this account/device pair within the server.
	 * 
	 * @return whether the registration succeeded or not.
	 */
	public static boolean register(final Context context, final String regId) {
		Log.i(TAG, "registering device (regId = " + regId + ")");
		String serverUrl = Config.SERVER_URL + "/register";
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		// Once GCM returns a registration id, we need to register it in the
		// demo server. As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				Config.displayMessage(context, context.getString(
						R.string.server_registering, i, MAX_ATTEMPTS));
				post(serverUrl, params);
				GCMRegistrar.setRegisteredOnServer(context, true);
				String message = context.getString(R.string.server_registered);
				Config.displayMessage(context, message);
				return true;
			} catch (IOException e) {
				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).
				Log.e(TAG, "Failed to register on attempt " + i, e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return false;
				}
				// increase backoff exponentially
				backoff *= 2;
			}
		}
		String message = context.getString(R.string.server_register_error,
				MAX_ATTEMPTS);
		Config.displayMessage(context, message);
		return false;
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	public static void unregister(final Context context, final String regId) {
		Log.i(TAG, "unregistering device (regId = " + regId + ")");
		String serverUrl = Config.SERVER_URL + "/unregister";
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);
		try {
			post(serverUrl, params);
			GCMRegistrar.setRegisteredOnServer(context, false);
			String message = context.getString(R.string.server_unregistered);
			Config.displayMessage(context, message);
		} catch (IOException e) {
			// At this point the device is unregistered from GCM, but still
			// registered in the server.
			// We could try to unregister again, but it is not necessary:
			// if the server tries to send a message to the device, it will get
			// a "NotRegistered" error message and should unregister the device.
			String message = context.getString(
					R.string.server_unregister_error, e.getMessage());
			Config.displayMessage(context, message);
		}
	}

	/**
	 * Issue a POST request to the server.
	 * 
	 * @param endpoint
	 *            POST address.
	 * @param params
	 *            request parameters.
	 * 
	 * @throws IOException
	 *             propagated from POST.
	 */
	public static String post(String urlString, Map<String, String> params)
			throws IOException {
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + urlString);
		}
		StringBuilder bodyBuilder = new StringBuilder();
		if (null != params) {
			Iterator<Entry<String, String>> iterator = params.entrySet()
					.iterator();
			// constructs the POST body using the parameters
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				bodyBuilder.append(param.getKey()).append('=')
						.append(param.getValue());
				if (iterator.hasNext()) {
					bodyBuilder.append('&');
				}
			}
		}
		String body = bodyBuilder.toString();
		Log.v(TAG, "Posting '" + body + "' to " + url);
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();
            return sb.toString();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	
	public static String[] getGps(String lineName,String direction,String sno) {
		String str1 = "1000090102f865eb33c96467a2714febe7e575d3"+
		"<?xml version='1.0'?>" +
		"<root>" +
		    "<head>" +
		      "<imei>351565053240000</imei>" +
		      "<system>4.2.1</system>" +
		      "<mobile></mobile>" +
		      "<model>Galaxy Nexus</model>" +
		    "</head>" +
		    "<body>" +
		      "<linename>"+lineName+"</linename>" +
		      "<lineud>"+direction+"</lineud>" +
		      "<stationno>"+sno+"</stationno>" +
		    "</body>" +
		 "</root>";
		try {
			Socket socket = new Socket();
			InetSocketAddress address = new InetSocketAddress("123.15.42.27",
					8094);
			socket.connect(address, 10000);
			 OutputStream os = socket.getOutputStream();
			 os.write(str1.getBytes("GBK"));
			 os.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream(),"gbk"));
			String xml = reader.readLine();
			reader.close();
			socket.close();
			return parseXml(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	private static String[] parseXml(String xml) throws XmlPullParserException, IOException {
	
		String[] result=new String[6];
		ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "UTF-8");

		// 获取事件类型
		int eventType = parser.getEventType();
		String tagName;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			// 文档开始
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if ("linename".equals(tagName)) {
					result[0]=parser.nextText();
				} else if ("lineud".equals(tagName)) {
					result[1]=parser.nextText();
				} else if ("stateionname".equals(tagName)) {
					result[2]=parser.nextText();
				} else if ("gpsinfo".equals(tagName)) {
					String s=parser.nextText();
					String[] split = s.split("；");
					result[3]=split.length>0?split[0]:"暂无信息";
					result[4]=split.length>1?split[1]:"暂无信息";
					result[5]=split.length>2?split[2]:"暂无信息";
				}
				break;
			case XmlPullParser.END_TAG:

				break;
			}
			eventType = parser.next();
		}
		
		return result;
	}

	public static String getGps1(String lineName,String direction,String sno,String hczd) throws IOException {
		String url = "http://wap.zhengzhoubus.com/gps.asp";
		Map<String, String> map = new HashMap<String, String>();
		map.put("xl", lineName);
		map.put("ud", direction);
		//map.put("fx", value);
		map.put("sno", sno);
		map.put("hczd", hczd);
		map.put("ref", "4");

		String str= ServerUtilities.post(url, map);
		
		return parseHtml(str);
	}
	
	public static String parseHtml(String str){
		Matcher matcher = Pattern.compile("【GPS候车查询】<br>([\\w\\W]+?)<br><br>获取最新信息")
				.matcher(str);
		if (matcher.find()) {
			str = matcher.group(1).replace("  ", "").replace("<br>注", "\n\n注")
					.replace("</a>", "").replace("<br>", "\n");
		} 
		return str;
		
		
	}
}
