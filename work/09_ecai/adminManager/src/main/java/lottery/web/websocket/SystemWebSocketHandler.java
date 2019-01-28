package lottery.web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by Nick on 2017/2/24.
 */
@Component
public class SystemWebSocketHandler implements WebSocketHandler {
    @Autowired
    private WebSocketSessionHolder sessionHolder;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionHolder.addSession(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // Object payload = message.getPayload();
        // System.out.println(payload);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        sessionHolder.removeBySession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessionHolder.removeBySession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
