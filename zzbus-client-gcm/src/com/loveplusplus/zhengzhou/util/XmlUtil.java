package com.loveplusplus.zhengzhou.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.loveplusplus.zhengzhou.bean.Bus;
import com.loveplusplus.zhengzhou.bean.LineTemp;
import com.loveplusplus.zhengzhou.bean.Station;

public class XmlUtil {

	public static List<Bus> loadBus(InputStream is)
			throws XmlPullParserException, NumberFormatException, IOException {
		List<Bus> list = new ArrayList<Bus>();
		Bus bus = null;
		XmlPullParser parser = XmlPullParserFactory.newInstance()
				.newPullParser();
		parser.setInput(is, "UTF-8");
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String nodeName = parser.getName();
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if ("RECORD".equals(nodeName)) {
					bus = new Bus();
				} else if ("id".equals(nodeName)) {
					bus.setId(Integer.parseInt(parser.nextText()));
				} else if ("name".equals(nodeName)) {
					bus.setName(parser.nextText());
				} else if ("start_time".equals(nodeName)) {
					bus.setStartTime(parser.nextText());
				} else if ("end_time".equals(nodeName)) {
					bus.setEndTime(parser.nextText());
				} else if ("price".equals(nodeName)) {
					bus.setPrice(parser.nextText());
				} else if ("card".equals(nodeName)) {
					bus.setCard(parser.nextText());
				} else if ("company".equals(nodeName)) {
					bus.setCompany(parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				if ("RECORD".equals(nodeName)) {
					list.add(bus);
				}
				break;
			default:
				break;
			}
			eventType = parser.next();
		}
		return list;
	}

	public static List<Station> loadStation(InputStream is)
			throws XmlPullParserException, NumberFormatException, IOException {
		List<Station> list = new ArrayList<Station>();
		Station station = null;
		XmlPullParser parser = XmlPullParserFactory.newInstance()
				.newPullParser();
		parser.setInput(is, "UTF-8");
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String nodeName = parser.getName();
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if ("RECORD".equals(nodeName)) {
					station = new Station();
				} else if ("id".equals(nodeName)) {
					station.setId(Integer.parseInt(parser.nextText()));
				} else if ("name".equals(nodeName)) {
					station.setName(parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				if ("RECORD".equals(nodeName)) {
					list.add(station);
				}
				break;
			default:
				break;
			}
			eventType = parser.next();
		}
		return list;
	}

	public static List<LineTemp> loadLine(InputStream is)
			throws XmlPullParserException, NumberFormatException, IOException {
		List<LineTemp> list = new ArrayList<LineTemp>();
		LineTemp line = null;
		XmlPullParser parser = XmlPullParserFactory.newInstance()
				.newPullParser();
		parser.setInput(is, "UTF-8");
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String nodeName = parser.getName();
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if ("RECORD".equals(nodeName)) {
					line = new LineTemp();
				} else if ("id".equals(nodeName)) {
					line.setId(Integer.parseInt(parser.nextText()));
				} else if ("bus_id".equals(nodeName)) {
					line.setBusId(Integer.parseInt(parser.nextText()));
				} else if ("station_id".equals(nodeName)) {
					line.setStationId(Integer.parseInt(parser.nextText()));
				} else if ("direct".equals(nodeName)) {
					line.setDirect(Integer.parseInt(parser.nextText()));
				} else if ("sno".equals(nodeName)) {
					line.setSno(Integer.parseInt(parser.nextText()));
				} 
				break;
			case XmlPullParser.END_TAG:
				if ("RECORD".equals(nodeName)) {
					list.add(line);
				}
				break;
			default:
				break;
			}
			eventType = parser.next();
		}
		return list;
	}

}
