package com.gameexpert.chat.service;

import com.gameexpert.ws.WorldBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalChatSender {
    private final WorldBroadcaster broadcaster;
    //채팅=같은 월드에 있는 모두가 봐야 함=broadcast()
    public void send(Long worldId, Object message) {
        broadcaster.broadcast(worldId, message);
    }
}
