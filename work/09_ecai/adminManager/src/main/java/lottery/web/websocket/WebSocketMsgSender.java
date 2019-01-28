package lottery.web.websocket;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * WebSocket消息发布器
 * Created by Nick on 2017/2/25.
 */
@Component
public class WebSocketMsgSender {
    private static final Logger log = LoggerFactory.getLogger(WebSocketMsgSender.class);

    @Autowired
    private WebSocketSessionHolder webSocketSessionHolder;

    /**
     * 发送大额中奖消息
     */
    public boolean sendHighPrizeNotice(String msg) {
        List<WebSocketSession> sessions = webSocketSessionHolder.listAllSessions();
        if (CollectionUtils.isEmpty(sessions)) {
            return true;
        }

        for (WebSocketSession session : sessions) {
            Map<String, Object> attributes = session.getAttributes();
            int _type = Integer.valueOf(attributes.get("type").toString());

            if (_type == WebSocketReceiveType.HIGH_PRIZE) {
                try {
                    if (session.isOpen()) {
                        synchronized (session) {
                            session.sendMessage(new TextMessage(msg));
                        }
                    }
                } catch (IOException e) {
                    log.error("发送WebSocket消息时出错", e);
                    return false;
                }
            }
        }

        return true;
    }
}
