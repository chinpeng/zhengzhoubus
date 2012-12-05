package com.jpsycn.wggl.mobile.androidpn.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.xmpp.packet.Presence;

import com.jpsycn.wggl.mobile.androidpn.model.SessionVO;
import com.jpsycn.wggl.mobile.androidpn.xmpp.session.ClientSession;
import com.jpsycn.wggl.mobile.androidpn.xmpp.session.Session;
import com.jpsycn.wggl.mobile.androidpn.xmpp.session.SessionManager;

public class SessionServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static Logger log = Logger.getLogger(SessionServlet.class);
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ClientSession[] sessions = new ClientSession[0];
		sessions = SessionManager.getInstance().getSessions().toArray(sessions);

		List<SessionVO> voList = new ArrayList<SessionVO>();
		for (ClientSession sess : sessions) {
			SessionVO vo = new SessionVO();
				vo.setUsername(sess.getUsername());
				vo.setResource(sess.getAddress().getResource());
				// Status
				if (sess.getStatus() == Session.STATUS_CONNECTED) {
					vo.setStatus("CONNECTED");
				} else if (sess.getStatus() == Session.STATUS_AUTHENTICATED) {
					vo.setStatus("AUTHENTICATED");
				} else if (sess.getStatus() == Session.STATUS_CLOSED) {
					vo.setStatus("CLOSED");
				} else {
					vo.setStatus("UNKNOWN");
				}
				// Presence
				if (!sess.getPresence().isAvailable()) {
					vo.setPresence("Offline");
				} else {
					Presence.Show show = sess.getPresence().getShow();
					if (show == null) {
						vo.setPresence("Online");
					} else if (show == Presence.Show.away) {
						vo.setPresence("Away");
					} else if (show == Presence.Show.chat) {
						vo.setPresence("Chat");
					} else if (show == Presence.Show.dnd) {
						vo.setPresence("Do Not Disturb");
					} else if (show == Presence.Show.xa) {
						vo.setPresence("eXtended Away");
					} else {
						vo.setPresence("Unknown");
					}
				}
				vo.setClientIP(sess.getHostAddress());
				vo.setCreatedDate(sess.getCreationDate());
				voList.add(vo);
			
			
		}

		request.setAttribute("voList", voList);
		request.getRequestDispatcher("/session/list.jsp").forward(request, response);
	}

}
