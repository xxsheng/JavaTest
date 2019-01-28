package lottery.web.helper.session;

import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.UserService;
import lottery.web.websocket.WebSocketSessionUserHolder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

@Component
public class WebSessionListener implements ApplicationListener<ApplicationEvent> {

	@Autowired
	private UserService uService;

	@Autowired
	private JedisTemplate jedisTemplate;
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof SessionDestroyedEvent) {
			String sessionId = ((SessionDestroyedEvent) event).getSessionId();
			uService.setOffline(sessionId);
			jedisTemplate.del("spring:session:sessions:" + sessionId);

			if (StringUtils.isNotEmpty(sessionId)) {
				WebSocketSessionUserHolder.removeBySessionId(sessionId);
			}
		}
	}

}