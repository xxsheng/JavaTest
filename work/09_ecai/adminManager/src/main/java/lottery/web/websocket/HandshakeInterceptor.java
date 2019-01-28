package lottery.web.websocket;

import admin.web.WSC;
import admin.web.helper.session.SessionUser;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
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
                if (session != null) {
                    SessionUser bean = (SessionUser) session.getAttribute(WSC.SESSION_USER_PROFILE);

                    if (bean != null) {

                        boolean check = checkType(params, bean);
                        if (!check) {
                            return false;
                        }

                        attributes.put(WSC.SESSION_USER_PROFILE, bean);
                        attributes.putAll(params);
                        return super.beforeHandshake(request, response, wsHandler, attributes);
                    }
                }
            }
        }
        return false;
    }

    private boolean checkType(Map<String, String> params, SessionUser bean) {
        if (!params.containsKey("type") || bean == null) {
            return false;
        }

        String type = params.get("type").toString();
        if ("1".equals(type)) {
            // 1:超级管理员;2:运营主管;10:风控专员;11:风控组长;
            if (bean.getRoleId() == 1 || bean.getRoleId() == 2 || bean.getRoleId() == 10 || bean.getRoleId() == 11) {
                return true;
            }

            return false;
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
