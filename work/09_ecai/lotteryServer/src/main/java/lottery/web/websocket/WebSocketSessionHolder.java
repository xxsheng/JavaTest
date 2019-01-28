package lottery.web.websocket;

import lottery.web.WSC;
import lottery.web.helper.session.SessionUser;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Component
public class WebSocketSessionHolder {
	
	private Hashtable<Integer, WebSocketSession> sessionTable = new Hashtable<>();

	// @Scheduled(cron = "0/3 * * * * *")
	// public void syncInitJob() {
	// 	Collection<WebSocketSession> sessions = listAllSessions();
	// 	for (WebSocketSession session : sessions) {
	// 		try {
	// 			SessionUser bean = (SessionUser) session.getAttributes().get(WSC.HTTP_SESSION_USER);
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

	public Set<Integer> listAllUserIds() {
		return sessionTable.keySet();
	}

	/**
	 * 获取session
	 */
	public WebSocketSession getSession(int userId) {
		return sessionTable.get(userId);
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

		SessionUser bean = (SessionUser) session.getAttributes().get(WSC.HTTP_SESSION_USER);
		if (bean == null) {
			return false;
		}

		WebSocketSession thisSession = sessionTable.get(bean.getId());
		if (thisSession == null) {
			sessionTable.put(bean.getId(), session);
		} else {
			// 避免重复登录
			if (!thisSession.getId().equals(session.getId())) {
				closeSession(thisSession);
				sessionTable.put(bean.getId(), session);
			}
		}
		return true;
	}
	
	/**
	 * 移除用户
	 */
	public boolean removeByUser(int userId) {
		WebSocketSession thisSession = sessionTable.get(userId);
		if(thisSession != null) {
			closeSession(thisSession);
			sessionTable.remove(userId);
		}
		return true;
	}
	
	/**
	 * 移除用户
	 */
	public boolean removeBySession(WebSocketSession session) {
		if(session != null) {
			SessionUser bean = (SessionUser) session.getAttributes().get(WSC.HTTP_SESSION_USER);
			if(bean != null) {
				WebSocketSession thisSession = sessionTable.get(bean.getId());
				if(thisSession != null) {
					closeSession(thisSession);
					sessionTable.remove(bean.getId());
				}
			}
		}
		return true;
	}
}