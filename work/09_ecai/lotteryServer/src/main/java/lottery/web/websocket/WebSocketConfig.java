package lottery.web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Created by Nick on 2017/2/24.
 */
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer{
    @Autowired
    private SystemWebSocketHandler socketHandler;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // WebSocket
        registry.addHandler(socketHandler, "/websocket").addInterceptors(new HandshakeInterceptor());
        // 低版本IE浏览器不支持WebSocket时的SockJS解决方式
        registry.addHandler(socketHandler, "/websocket/sockjs").addInterceptors(new HandshakeInterceptor()).withSockJS();
    }
}
