package com.gameexpert.ws;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WorldSessionRegistry implements com.gameexpert.api.SessionRegistry {

    private final Map<Long, ConcurrentHashMap<String, Entry>> worlds = new ConcurrentHashMap<>();

    public Entry register(Long worldId, String nickname, WebSocketSession session) {
        String nicknameKey = key(nickname);
        Entry candidate = new Entry(session);
        AtomicReference<Entry> registered = new AtomicReference<>();
        worlds.compute(worldId, (ignored, current) -> {
            ConcurrentHashMap<String, Entry> sessions = current == null
                    ? new ConcurrentHashMap<>() : current;
            // putIfAbsent(): 키가 이미 있으면 손대지 않고 기존값 반환, 없었으면 새로 넣고 null 반환
            // 반환값==null => 새로 등록한 것
            // putIfAbsent()=>확인과 등록을 원자적으로 처리해 경쟁 상태 방지
            Entry previous = sessions.putIfAbsent(nicknameKey, candidate);
            boolean added = previous == null;
            if (added) {
                registered.set(candidate);
            }
            return sessions;
        });
        return registered.get();
    }

    public Entry remove(Long worldId, String nickname, WebSocketSession session) {
        String nicknameKey = key(nickname);
        AtomicReference<Entry> removed = new AtomicReference<>();
        worlds.computeIfPresent(worldId, (ignored, sessions) -> {
            Entry current = sessions.get(nicknameKey);
            if (current != null && current.session() == session
                    && sessions.remove(nicknameKey, current)) {
                removed.set(current);
            }
            return sessions.isEmpty() ? null : sessions;
        });
        return removed.get();
    }

    public Entry get(Long worldId, String nickname) {
        ConcurrentHashMap<String, Entry> sessions = worlds.get(worldId);
        if (sessions == null) {
            return null;
        }
        return sessions.get(key(nickname));
    }

    public Collection<Entry> entries(Long worldId) {
        ConcurrentHashMap<String, Entry> sessions = worlds.get(worldId);

        return sessions == null ? java.util.List.of() : sessions.values();
    }

    public Set<Long> worldIds() {
        return Set.copyOf(worlds.keySet());
    }

    private static String key(String nickname) {
        return nickname.toLowerCase(Locale.ROOT);
    }
}
