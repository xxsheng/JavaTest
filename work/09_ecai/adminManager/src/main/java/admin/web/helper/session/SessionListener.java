package admin.web.helper.session;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import admin.web.WSC;

public class SessionListener implements HttpSessionListener {
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		if(session != null) {
			SessionUser sessionUser = (SessionUser) session.getAttribute(WSC.SESSION_USER_PROFILE);
			if(sessionUser != null) {
				HttpSession onlineSession = SessionManager.onlineUser.get(sessionUser.getUsername());
				if(onlineSession != null) {
					String onlineSId = onlineSession.getId();
					// 如果在线用户信息和销毁的信息一致，那么用户下线；否则说明用户已经在其他地方登录了，不需要下线。
					if(onlineSId.equals(session.getId())) {
						SessionManager.onlineUser.remove(sessionUser.getUsername());
					}
				}
			}
		}
	}

}
