package com.gameexpert.ws.handler;

import org.springframework.stereotype.Component;

import com.gameexpert.ws.WorldBroadcaster;
import com.gameexpert.ws.WorldSessionRegistry;
import com.gameexpert.presence.PresenceService;
import com.gameexpert.ws.WsMessageContext;
import com.gameexpert.ws.dto.PongResponse;

import tools.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PingWsHandler implements WsMessageHandler {

    private final WorldBroadcaster broadcaster;
    private final WorldSessionRegistry registry;
    private final PresenceService presenceService;

    @Override
    public String type() {
        return "ping";
    }

    @Override
    public void handle(WsMessageContext context, JsonNode message) {
        WorldSessionRegistry.Entry connection = registry.get(context.worldId(), context.nickname());
        if (connection == null || connection.session() != context.session()) {
            return;
        }
        // Redis 접속 정보를 갱신
        // 클라이언트가 보내는 ping=생존 신호(신호가 나타날 때마다 만료시간 재설정)
        presenceService.heartbeat(context.worldId(), connection.connectionId());
        // pong은 ping을 보낸 연결에만 응답
        // 특정 세션 하나에만 보내는 sendTo() 사용
        broadcaster.sendTo(context.session(), new PongResponse());
    }
}
