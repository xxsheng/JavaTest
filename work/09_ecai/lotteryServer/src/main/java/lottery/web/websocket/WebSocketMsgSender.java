package lottery.web.websocket;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
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
     * 给所有订阅了某个彩种的用户发最新开奖号码
     */
    public boolean sendLotteryOpenCodeMsg(int lotteryId, String expect, String code) {
        List<WebSocketSession> sessions = webSocketSessionHolder.listAllSessions();
        if (CollectionUtils.isEmpty(sessions)) {
            return true;
        }

        for (WebSocketSession session : sessions) {
            Map<String, Object> attributes = session.getAttributes();
            int _type = Integer.valueOf(attributes.get("type").toString());
            int _lotteryId = Integer.valueOf(attributes.get("lotteryId").toString());

            if (_type == WebSocketReceiveType.LOTTERY && _lotteryId == lotteryId) {
                try {
                    if (session.isOpen()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("expect", expect);
                        data.put("code", code);
                        data.put("type", NoticeSendType.LOTTERY_OPEN_CODE);
                        synchronized (session) {
                            session.sendMessage(new TextMessage(JSON.toJSONString(data)));
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

    /**
     * 给单个用户发送急速秒秒彩消息
     */
    public boolean sendUserJSMMCOpenCodeMsg(int userId, String code) {
        WebSocketSession session = webSocketSessionHolder.getSession(userId);

        if (session != null && session.isOpen()) {
            try {
                Map<String, Object> attributes = session.getAttributes();
                int _type = Integer.valueOf(attributes.get("type").toString());
                int _lotteryId = Integer.valueOf(attributes.get("lotteryId").toString());

                if (_type == 1 && 117 == _lotteryId) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("code", code);
                    data.put("type", NoticeSendType.LOTTERY_OPEN_CODE);
                    synchronized (session) {
                        session.sendMessage(new TextMessage(JSON.toJSONString(data)));
                    }
                }

                return true;
            } catch (IOException e) {
                log.error("发送WebSocket消息时出错", e);
            }
        }

        return false;
    }

    /**
     * 给单个用户发送消息
     */
    public boolean sendUserMsg(int userId, String msg) {
        WebSocketSession session = webSocketSessionHolder.getSession(userId);

        if (session != null && session.isOpen()) {
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(msg));
                }
                return true;
            } catch (IOException e) {
                log.error("发送WebSocket消息时出错", e);
            }
        }

        return false;
    }

    /**
     * 给所有订阅了彩票的用户发送红包雨开始消息
     */
    public void sendUserRedPacketRainStart(int userId, String startTime, String endTime) {
        WebSocketSession session = webSocketSessionHolder.getSession(userId);
        if (session == null || !session.isOpen()) {
            return;
        }

        Map<String, Object> attributes = session.getAttributes();
        int _type = Integer.valueOf(attributes.get("type").toString());

        // 必须是订阅了彩票才可以
        if (_type == WebSocketReceiveType.LOTTERY) {
            try {
                if (session.isOpen()) {
                    synchronized (session) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("startTime", startTime);
                        data.put("endTime", endTime);
                        data.put("type", NoticeSendType.REDPACKET_RAIN_START);
                        session.sendMessage(new TextMessage(JSON.toJSONString(data)));
                    }
                }
            } catch (IOException e) {
                log.error("发送WebSocket消息时出错", e);
            }
        }
    }

    /**
     * 给所有订阅了彩票的用户发送红包雨结束消息
     */
    public void sendUserRedPacketRainEnd(int userId, boolean todayHasMore) {
        WebSocketSession session = webSocketSessionHolder.getSession(userId);
        if (session == null || !session.isOpen()) {
            return;
        }

        Map<String, Object> attributes = session.getAttributes();
        int _type = Integer.valueOf(attributes.get("type").toString());

        // 必须是订阅了彩票才可以
        if (_type == WebSocketReceiveType.LOTTERY) {
            try {
                if (session.isOpen()) {
                    synchronized (session) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("todayHasMore", todayHasMore);
                        data.put("type", NoticeSendType.REDPACKET_RAIN_END);
                        session.sendMessage(new TextMessage(JSON.toJSONString(data)));
                    }
                }
            } catch (IOException e) {
                log.error("发送WebSocket消息时出错", e);
            }
        }
    }
}
