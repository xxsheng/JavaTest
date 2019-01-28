package lottery.web.websocket;

import javautils.http.HttpUtil;
import lottery.web.helper.session.SessionUser;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Web Socket使用token连接，低版本浏览器，有时是不会带cookie给web socket的，这时候使用token来做个中间层，可以通过token来获取当前登录的用户
 * Created by Nick on 2017-04-15.
 */
public final class WebSocketSessionUserHolder {
    private static final Logger log = LoggerFactory.getLogger(WebSocketSessionUserHolder.class);

    private static ConcurrentHashMap<String, WebSocketInfo> HOLDER = new ConcurrentHashMap<>(); // key: token; value: SessionUser

    public static SessionUser getSessionUser(String token, HttpServletRequest request) {
        if (!checkToken(token)) {
            log.error("token长度不符合要求，请使用WebSocketSessionUserHolder.generateToken生成token,token=[" + token + "]");
            throw new RuntimeException("token长度不符合要求，请使用WebSocketSessionUserHolder.generateToken生成token,token=[" + token + "]");
        }

        WebSocketInfo info = HOLDER.get(token);
        if (info == null) {
            return null;
        }

        // 检查时效性
        if (hashExpired(info)) {
            remove(info.getToken());
            return null;
        }

        // 检查有效性
        if (!checkValidate(info, request)) {
            return null;
        }

        return info.getUser();
    }

    public static boolean contains(String token) {
        if (!checkToken(token)) {
            log.error("token长度不符合要求，请使用WebSocketSessionUserHolder.generateToken生成token,token=[" + token + "]");
            throw new RuntimeException("token长度不符合要求，请使用WebSocketSessionUserHolder.generateToken生成token,token=[" + token + "]");
        }

        return HOLDER.containsKey(token);
    }

    /**
     * 生成token
     */
    public static WebSocketInfo generateToken(HttpServletRequest request, SessionUser sessionUser) {
        String token = org.bson.types.ObjectId.get().toString() + RandomStringUtils.random(6, true, true);

        String realIp = HttpUtil.getRealIp(null, request);

        return new WebSocketInfo(token, realIp, sessionUser);
    }

    public static void save(String token, WebSocketInfo info) {
        if (!checkToken(token)) {
            log.error("token长度不符合要求，请使用WebSocketSessionUserHolder.generateToken生成token,token=[" + token + "]");
            throw new RuntimeException("token长度不符合要求，请使用WebSocketSessionUserHolder.generateToken生成token,token=[" + token + "]");
        }

        if (HOLDER.containsKey(token)) {
            log.error("token["+token+"]已经存在，请重新设置一个唯一的token");
            throw new RuntimeException("token["+token+"]已经存在，请重新设置一个唯一的token");
        }

        HOLDER.put(token, info);
    }

    /**
     * 根据token删除值
     */
    public static void remove(String token) {
        if (!checkToken(token)) {
            log.error("token长度不符合要求，请使用WebSocketSessionUserHolder.generateToken生成token,token=[" + token + "]");
            throw new RuntimeException("token长度不符合要求，请使用WebSocketSessionUserHolder.generateToken生成token,token=[" + token + "]");
        }

        HOLDER.remove(token);
    }

    /**
     * 根据http session id删除值
     */
    public static void removeBySessionId(String sessionId) {
        try {
            long start = System.currentTimeMillis();

            Set<Map.Entry<String, WebSocketInfo>> entries = HOLDER.entrySet();
            Iterator<Map.Entry<String, WebSocketInfo>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, WebSocketInfo> next = iterator.next();
                String userSessionId = next.getValue().getUser().getSessionId();
                if (sessionId.equals(userSessionId)) {
                    iterator.remove();
                    break;
                }
            }
            long spend = System.currentTimeMillis() - start;

            if (spend >= 200) {
                log.warn("根据sessionId删除token较耗时,达到{}ms,总token数量{}个,请检查", spend, HOLDER.size());
            }
        } catch (Exception e) {
            log.error("根据sessionId删除token时出错, sessionId:" + sessionId, e);
        }
    }

    private static boolean checkToken(String token) {
        if (StringUtils.isEmpty(token) || token.length() > 50) {
            return false;
        }

        return true;
    }

    public static boolean hashExpired(WebSocketInfo info) {
        if (info == null || info.hasExpired()) {
            if (info != null) {
                remove(info.getToken());
            }
            return true;
        }

        return false;
    }

    public static boolean checkValidate(WebSocketInfo info, HttpServletRequest request) {
        // String realIp = HttpUtil.getRealIp(request);
        //
        // if (!realIp.equals(info.getConnectIp())) {
        //     remove(info.getToken());
        //     log.warn("socket创建IP与连接IP不一致，创建IP：{}，连接IP：{}, 用户ID：{},已删除上个token", info.getConnectIp(), realIp, info.getUser().getId());
        //     return false;
        // }

        return true;
    }
}
