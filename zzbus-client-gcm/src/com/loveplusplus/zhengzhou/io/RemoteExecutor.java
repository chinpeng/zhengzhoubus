package com.loveplusplus.zhengzhou.io;

import java.io.UnsupportedEncodingException;

import com.loveplusplus.zhengzhou.util.GpsUtil;


public class RemoteExecutor {
	public static final String s="http://wap.zhengzhoubus.com/gps.asp";
	
	/**
	 * 发送http请求
	 * 
	 * @param databyte
	 *            字节数组
	 * @param file
	 *            文件字节数组
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String request(String lineName,String direction,String sno,String hczd) throws UnsupportedEncodingException {
		return GpsUtil.getGps(lineName, direction, sno,hczd);
	}

	
	
	
}
