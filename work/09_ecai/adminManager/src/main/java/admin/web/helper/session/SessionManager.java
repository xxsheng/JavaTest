package admin.web.helper.session;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import admin.web.WSC;

public final class SessionManager {
	
	public static Map<String, HttpSession> onlineUser = new HashMap<String, HttpSession>();
	
	private SessionManager() {}

	public static void cleanUserSession(final HttpSession session) {
		SessionUser sessionUser = (SessionUser) session.getAttribute(WSC.SESSION_USER_PROFILE);
		onlineUser.remove(sessionUser.getUsername());
		session.removeAttribute(WSC.SESSION_USER_PROFILE);
	}

	public static void setCurrentUser(final HttpSession session, final SessionUser sessionUser) {
		session.setAttribute(WSC.SESSION_USER_PROFILE, sessionUser);
		onlineUser.put(sessionUser.getUsername(), session);
	}

	public static SessionUser getCurrentUser(final HttpSession session) {
		SessionUser sessionUser = (SessionUser) session.getAttribute(WSC.SESSION_USER_PROFILE);
		if(sessionUser != null) {
			HttpSession onlineSession = onlineUser.get(sessionUser.getUsername());
			if(onlineSession != null) {
				String onlineSId = onlineSession.getId();
				// 如果在线用户信息和现在登录信息不一样，说明用户已经在其他地方登录了，销毁session，重新登录吧。
				if(!onlineSId.equals(session.getId())) {
					cleanUserSession(session);
					return null;
				}
			}
			
		}
		return sessionUser;
	}
	
}