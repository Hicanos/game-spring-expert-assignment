package com.gameexpert.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.gameexpert.ws.GameWebSocketHandler;
import com.gameexpert.ws.NicknameHandshakeInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@EnableScheduling
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final GameWebSocketHandler gameWebSocketHandler;
    private final NicknameHandshakeInterceptor nicknameInterceptor;
    private final EngineProperties properties;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // registerWebSocketHandlers()=>핸들러(GameWebSocketHandler)를 URL 패턴에 연결만 함
        // NicknameHandshakeInterceptor=>addInterceptors()로 명시적으로 등록해야 실제 연결 요청마다 beforeHandshake() 호출.
        // 인터셉터 클래스가 빈으로 등록되어 있어도 핸들러 등록 체인 미연결 시 호출 불가
        registry.addHandler(gameWebSocketHandler, "/ws/worlds/{worldId}")
                .addInterceptors(nicknameInterceptor)
                .setAllowedOriginPatterns(properties.wsAllowedOrigins().toArray(String[]::new));
    }
}
