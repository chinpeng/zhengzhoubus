package com.jpsycn.wggl.mobile.androidpn.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.jpsycn.wggl.mobile.androidpn.util.Config;
import com.jpsycn.wggl.mobile.androidpn.xmpp.push.NotificationManager;

public class SendMessageServlet extends HttpServlet {

	private final static Logger log = Logger.getLogger(SendMessageServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		 	String broadcast = request.getParameter("broadcast");
	        String username = request.getParameter("username");
	        String title = request.getParameter("title");
	        
	        String message =  request.getParameter("message");
	        String uri =request.getParameter("uri"); 

	        String apiKey = Config.getString("apiKey", "");
	        log.info("broadcast="+broadcast+" username="+username+" title="+title+" message="+message+" uri="+uri+" apiKey=" + apiKey);
	        
	        NotificationManager notificationManager = new NotificationManager();
	        
	        //在线用户
	        if (broadcast.equalsIgnoreCase("Y")) {
	            notificationManager.sendBroadcast(apiKey, title, message, uri);
	        //所有用户
	        }else if (broadcast.equalsIgnoreCase("A")) {
	            notificationManager.sendAllBroadcast(apiKey, title, message, uri);
	        //指定用户
	        }else {
	            notificationManager.sendNotifications(apiKey, username, title,message, uri);
	        }
	        request.getRequestDispatcher("/notification/form.jsp").forward(request, response);
	       
		
	}


}
