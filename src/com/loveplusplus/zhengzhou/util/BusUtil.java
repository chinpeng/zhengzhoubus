package com.loveplusplus.zhengzhou.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class BusUtil {

	public static String[] getGps(String lineName, String direction, String sno) {
		String str1 = "1000090102f865eb33c96467a2714febe7e575d3"
				+ "<?xml version='1.0'?>" + "<root>" + "<head>"
				+ "<imei>351565053240000</imei>" + "<system>4.2.1</system>"
				+ "<mobile></mobile>" + "<model>Galaxy Nexus</model>"
				+ "</head>" + "<body>" + "<linename>" + lineName
				+ "</linename>" + "<lineud>" + direction + "</lineud>"
				+ "<stationno>" + sno + "</stationno>" + "</body>" + "</root>";
		try {
			String xml = SocketUtil.socket(str1);
			return parseXml(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private static String[] parseXml(String xml) throws XmlPullParserException,
			IOException {

		String[] result = new String[6];
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
					result[0] = parser.nextText();
				} else if ("lineud".equals(tagName)) {
					result[1] = parser.nextText();
				} else if ("stateionname".equals(tagName)) {
					result[2] = parser.nextText();
				} else if ("gpsinfo".equals(tagName)) {
					String s = parser.nextText();
					String[] split = s.split("；");
					result[3] = split.length > 0 ? split[0] : "暂无信息";
					result[4] = split.length > 1 ? split[1] : "暂无信息";
					result[5] = split.length > 2 ? split[2] : "暂无信息";
				}
				break;
			case XmlPullParser.END_TAG:

				break;
			}
			eventType = parser.next();
		}

		return result;
	}

	public static String getGps1(String lineName, String direction, String sno,
			String hczd) throws IOException {
		String url = "http://wap.zhengzhoubus.com/gps.asp";
		Map<String, String> map = new HashMap<String, String>();
		map.put("xl", lineName);
		map.put("ud", direction);
		// map.put("fx", value);
		map.put("sno", sno);
		map.put("hczd", hczd);
		map.put("ref", "4");

		String str = HttpUtil.post(url, map);

		return parseHtml(str);
	}

	public static String parseHtml(String str) {
		Matcher matcher = Pattern.compile(
				"【GPS候车查询】<br>([\\w\\W]+?)<br><br>获取最新信息").matcher(str);
		if (matcher.find()) {
			str = matcher.group(1).replace("  ", "").replace("<br>注", "\n\n注")
					.replace("</a>", "").replace("<br>", "\n");
		}
		return str;

	}
}
