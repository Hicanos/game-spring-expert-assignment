package com.gameexpert.chat.service;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRateLimitService {

    // GET(조회)=>INCR(증가)=>EXPIRE(최초 만료 설정)을 하나의 Lua스크립트로
    // Redis 서버 내 원자적으로 실행.
    private static final RedisScript<Long> ALLOW_SCRIPT = new DefaultRedisScript<>("""
            local count = tonumber(redis.call('GET', KEYS[1]) or '0')
            if count >= 5 then
                return 0
            end
            redis.call('INCR', KEYS[1])
            if count == 0 then
                redis.call('EXPIRE', KEYS[1], 10)
            end
            return 1
            """, Long.class);

    private final StringRedisTemplate redisTemplate;

    public boolean allow(Long playerId) {
        String key = "chat:limit:" + playerId;
        Long allowed = redisTemplate.execute(ALLOW_SCRIPT, List.of(key));
        return allowed != null && allowed == 1L;
    }
}
