package com.jpsycn.wggl.mobile.androidpn.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.jpsycn.wggl.mobile.androidpn.model.User;
import com.jpsycn.wggl.mobile.androidpn.service.ServiceLocator;
import com.jpsycn.wggl.mobile.androidpn.service.UserService;
import com.jpsycn.wggl.mobile.androidpn.xmpp.presence.PresenceManager;

public class UserServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger log = Logger.getLogger(UserServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		UserService userService = ServiceLocator.getUserService();
		PresenceManager presenceManager = new PresenceManager();
		List<User> userList = userService.getUsers();
		for (User user : userList) {
			if (presenceManager.isAvailable(user)) {
				// Presence presence = presenceManager.getPresence(user);
				user.setOnline(true);
			} else {
				user.setOnline(false);
			}
			log.info("user.online=" + user.isOnline());
		}

		request.setAttribute("userList", userList);
		request.getRequestDispatcher("/user/list.jsp").forward(request,
				response);
	}

}
