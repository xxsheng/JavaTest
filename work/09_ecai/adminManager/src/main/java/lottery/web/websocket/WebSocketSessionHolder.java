package lottery.web.websocket;

import admin.web.WSC;
import admin.web.helper.session.SessionUser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

@Component
public class WebSocketSessionHolder {
	
	private Hashtable<String, WebSocketSession> sessionTable = new Hashtable<>();

	// @Scheduled(cron = "0/3 * * * * *")
	// public void syncInitJob() {
	// 	Collection<WebSocketSession> sessions = listAllSessions();
	// 	for (WebSocketSession session : sessions) {
	// 		try {
	// 			SessionUser bean = (SessionUser) session.getAttributes().get(WSC.SESSION_USER_PROFILE);
	// 			Object type = session.getAttributes().get("type");
	// 			session.sendMessage(new TextMessage(bean.getUsername() + "你好，这是服务器发送的消息，订阅type是" + type.toString()));
	// 		} catch (IOException e) {
	// 			e.printStackTrace();
	// 		}
	// 	}
	// }

	public void closeSession(WebSocketSession session) {
		try {
			if(session != null && session.isOpen()) {
				session.close();
			}
		} catch (Exception e) {}
	}
	
	public List<WebSocketSession> listAllSessions() {
		Collection<WebSocketSession> values = sessionTable.values();

		List<WebSocketSession> sessions = new ArrayList<>();
		for (WebSocketSession value : values) {
			if (value != null && value.isOpen()) {
				sessions.add(value);
			}
		}
		return sessions;
	}
	
	/**
	 * 获取session
	 */
	public WebSocketSession getSession(String username) {
		return sessionTable.get(username);
	}
	
	/**
	 * 添加用户
	 * @param session
	 * @return
	 */
	public boolean addSession(WebSocketSession session) {
		if (session == null) {
			return false;
		}

		SessionUser bean = (SessionUser) session.getAttributes().get(WSC.SESSION_USER_PROFILE);
		if (bean == null) {
			return false;
		}

		WebSocketSession thisSession = sessionTable.get(bean.getUsername());
		if (thisSession == null) {
			sessionTable.put(bean.getUsername(), session);
		} else {
			// 避免重复登录
			if (!thisSession.getId().equals(session.getId())) {
				closeSession(thisSession);
				sessionTable.put(bean.getUsername(), session);
			}
		}
		return true;
	}

	/**
	 * 移除用户
	 */
	public boolean removeByUser(String username) {
		WebSocketSession thisSession = sessionTable.get(username);
		if(thisSession != null) {
			closeSession(thisSession);
			sessionTable.remove(username);
		}
		return true;
	}
	
	/**
	 * 移除用户
	 */
	public boolean removeBySession(WebSocketSession session) {
		if(session != null) {
			SessionUser bean = (SessionUser) session.getAttributes().get(WSC.SESSION_USER_PROFILE);
			if(bean != null) {
				WebSocketSession thisSession = sessionTable.get(bean.getUsername());
				if(thisSession != null) {
					closeSession(thisSession);
					sessionTable.remove(bean.getUsername());
				}
			}
		}
		return true;
	}
}