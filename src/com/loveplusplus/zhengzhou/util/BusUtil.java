package com.loveplusplus.zhengzhou.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusUtil {

	public static String getGps1(String lineName,String direction,String sno,String hczd) throws IOException {
		String url = "http://wap.zhengzhoubus.com/gps.asp";
		Map<String, String> map = new HashMap<String, String>();
		map.put("xl", lineName);
		map.put("ud", direction);
		//map.put("fx", value);
		map.put("sno", sno);
		map.put("hczd", hczd);
		map.put("ref", "4");

		String str= HttpUtil.post(url, map);
		
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
