package com.loveplusplus.zhengzhou.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import android.util.Log;

public class SendHttpRequestter {

	/**
	 * 发送Get请求
	 * 
	 * @param urlString
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String sendGet(String urlStr, Map<String, String> params) throws UnsupportedEncodingException {
		HttpURLConnection conn = null;
		StringBuffer param = new StringBuffer();
		int i = 0;
		for (String key : params.keySet()) {
			if (i == 0)
				param.append("?");
			else
				param.append("&");
			param.append(key).append("=").append(URLEncoder.encode(params.get(key),"UTF-8"));
			i++;
		}
		urlStr += param;
		Log.d("------->", urlStr);
		try {
			URL url = new URL(urlStr);
			// 请求配置，可根据实际情况采用灵活配置
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			// 调用http请求
			return this.makeContent(conn);
		} catch (Exception e) {
			e.printStackTrace();
			return "0000";// 异常返回0000
		}
	}

	/**
	 * 得到响应对象
	 * 
	 * @param conn
	 * @return 响应对象
	 * @throws Exception
	 */
	private String makeContent(HttpURLConnection conn) {
		try {
			InputStream in = conn.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in, "UTF-8"));
			StringBuffer temp = new StringBuffer();
			String line = bufferedReader.readLine();
			while (line != null) {
				temp.append(line);
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
			return temp.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "0000";// 异常返回0000
		} finally {
			if (conn != null)
				conn.disconnect();
		}
	}
	/**
	 * 发送Get请求
	 * 
	 * @param urlString
	 * @param params
	 * @return
	 */
	public String sendGet(String urlStr, Map<String, String> params,
			String sessionId) {
		HttpURLConnection conn = null;
		StringBuffer param = new StringBuffer();
		int i = 0;
		if (null != params) {
			for (String key : params.keySet()) {
				if (i == 0)
					param.append("?");
				else
					param.append("&");
				param.append(key).append("=").append(params.get(key));
				i++;
			}
		}
		urlStr += param;

		try {
			URL url = new URL(urlStr);
			// 请求配置，可根据实际情况采用灵活配置
			conn = (HttpURLConnection) url.openConnection();
			if (null != sessionId) {
				conn.setRequestProperty("Cookie", sessionId);
			}
			conn.connect();
			// 调用http请求
			return this.makeContent(conn);
		} catch (Exception e) {
			e.printStackTrace();
			return "0000";// 异常返回0000
		}
	}
}
