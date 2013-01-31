package com.loveplusplus.zhengzhou.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

public class AssetsUtil {


	public static String loadJson(String name, Context context)
			throws IOException {

		StringBuilder sb = new StringBuilder();
		sb.append("db").append("/");
		sb.append(name);
		sb.append(".json");

		String fileName = sb.toString();
		InputStream is = context.getAssets().open(fileName.toLowerCase(Locale.CHINA));

		// 下一步可以把字符串进行加密
		return makeContent(is);
	}

	private static String makeContent(InputStream is) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		br.close();
		return sb.toString();
	}

	public static List<String> loadBusList(Context context) throws IOException {
		InputStream is = context.getAssets().open("list.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		List<String> list = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null) {
			list.add(line);
		}
		br.close();
		return list;
	}

}
