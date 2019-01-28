package lottery.web.websocket;

import lottery.web.WSC;
import lottery.web.helper.session.SessionUser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 2017/2/24.
 */
@Component
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    private static final Logger log = LoggerFactory.getLogger(HandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        // 解决The extension [x-webkit-deflate-frame] is not supported问题
        // 可能是iOS手机safari浏览器版本问题引起的。
        // org.springframework.web.socket.server.HandshakeFailureException: Uncaught failure for request
        // nested exception is Java.lang.IllegalArgumentException: The extension [x-webkit-deflate-frame] is not supported
        if(request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {
            request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");
        }

        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            Map<String, String> params = getParams(httpServletRequest);

            // 客户端必须带有type参数，用来区分是订阅了什么通知
            if (params.containsKey("type")) {
                HttpSession session = httpServletRequest.getSession(false);
                SessionUser bean = null;
                if (session != null) {
                    bean = (SessionUser) session.getAttribute(WSC.HTTP_SESSION_USER);
                }

                // 没有获取到session
                if (bean == null) {
                    String token = params.get("token");
                    if (StringUtils.isNotEmpty(token)) {
                        bean = WebSocketSessionUserHolder.getSessionUser(token, httpServletRequest);
                        if (bean == null) {
                            log.warn("注册WebSocket时没有根据token获取到用户,请检查,User-Agent:{}, token:{}", httpServletRequest.getHeader("User-Agent"), token);
                        }
                    }
                    else {
                        log.warn("注册WebSocket时没有找到session,即没有cookie也没有token,请检查,User-Agent:{}", httpServletRequest.getHeader("User-Agent"));
                    }
                }

                if (bean != null) {
                    attributes.put(WSC.HTTP_SESSION_USER, bean);
                    attributes.putAll(params);
                    return super.beforeHandshake(request, response, wsHandler, attributes);
                }
            }
        }
        return false;
    }

    private Map<String, String> getParams(HttpServletRequest httpServletRequest) {
        Enumeration parameterNames = httpServletRequest.getParameterNames();

        Map<String, String> params = new HashMap<>();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement().toString();
            String paramValue = httpServletRequest.getParameter(paramName);
            params.put(paramName, paramValue);
        }

        return params;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
