package com.loveplusplus.zhengzhou.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class SocketUtil {

	public static String socket(String xml) throws IOException {
		Socket socket = new Socket();
		InetSocketAddress address = new InetSocketAddress("123.15.42.27", 8094);
		socket.connect(address, 30*1000);
		OutputStream os = socket.getOutputStream();
		os.write(xml.getBytes("GBK"));
		os.flush();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream(), "gbk"));
		String result = reader.readLine();
		reader.close();
		socket.close();
		return result;
	}

	

}
