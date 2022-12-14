package ink.wyy.websocket.configure;

import ink.wyy.websocket.handler.IMHandler;
import ink.wyy.websocket.interceptor.IMInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * 注册handler
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/im").addInterceptors(new IMInterceptor()).setAllowedOrigins("*");
    }

    public WebSocketHandler myHandler() {
        return new IMHandler();
    }
}
