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
import java.net.InetSocketAddress;
import java.net.Socket;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * Helper class used to communicate with the demo server.
 */
public final class SocketUtil {


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

	
}
