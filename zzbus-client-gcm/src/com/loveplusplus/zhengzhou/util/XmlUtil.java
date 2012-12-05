package com.loveplusplus.zhengzhou.util;


public class XmlUtil {

//	public static BusLine getLine(InputStream is) {
//		BusLine line = null;
//		List<Station> shangxing = null;
//		List<Station> xiaxing = null;
//
//		try {
//			XmlPullParser parser = XmlPullParserFactory.newInstance()
//					.newPullParser();
//			parser.setInput(is, "UTF-8");
//
//			int eventType = parser.getEventType();
//			boolean isShangXing = true;
//			while (eventType != XmlPullParser.END_DOCUMENT) {
//				String nodeName = parser.getName();
//				switch (eventType) {
//				case XmlPullParser.START_TAG:
//					if ("line".equals(nodeName)) {
//						line = new BusLine(parser.getAttributeValue(0));
//					} else if ("shangxing".equals(nodeName)) {
//						shangxing = new ArrayList<Station>();
//					} else if ("xiaxing".equals(nodeName)) {
//						xiaxing = new ArrayList<Station>();
//					} else if ("sta".equals(nodeName)) {
//						if (isShangXing) {
//							if (null != shangxing)
//								shangxing.add(new Station(parser
//										.getAttributeValue(0), parser.
//										nextText()));
//						} else {
//							if (null != xiaxing)
//								xiaxing.add(new Station(parser
//										.getAttributeValue(0), parser.
//										nextText()));
//						}
//					}
//					break;
//				case XmlPullParser.END_TAG:
//					if ("shangxing".equals(nodeName)) {
//						line.setShangxing(shangxing);
//						isShangXing = false;
//					} else if ("xiaxing".equals(nodeName)) {
//						line.setXiaxing(xiaxing);
//					}
//					break;
//				default:
//					break;
//				}
//				eventType = parser.next();
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (XmlPullParserException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return line;
//	}
}
