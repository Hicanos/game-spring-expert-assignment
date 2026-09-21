package com.gameexpert.chat.service;

import com.gameexpert.chat.entity.ChatMessage;
import com.gameexpert.chat.event.ChatSavedEvent;
import org.springframework.context.ApplicationEventPublisher;
import com.gameexpert.chat.repository.ChatMessageRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gameexpert.chat.dto.ChatMessageResponse;
import com.gameexpert.common.NotFoundException;
import com.gameexpert.world.repository.WorldRepository;
import com.gameexpert.world.entity.World;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int MAX_LIMIT = 100;

    private final ChatMessageRepository chatMessageRepository;
    private final WorldRepository worldRepository;
    private final ApplicationEventPublisher events;

    @Transactional
    public ChatMessageResponse saveMessage(Long worldId, String sender, String content) {
        // 채팅저장 전 worldId가 실제로 존재하는 월드인지 확인
        // World 없이 ChatMessage를 저장 시 world_id의 FK 제약을 DB에서 위반
        // 그 전에 "WORLD_NOT_FOUND"라는 명확한 의미의 오류로 필터링
        World world = worldRepository.findById(worldId)
                .orElseThrow(() -> new NotFoundException("WORLD_NOT_FOUND"));

        ChatMessage saved = chatMessageRepository.save(new ChatMessage(world, sender, content));
        return savedResponse(worldId, saved);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getRecentMessages(Long worldId, int limit) {
        if (!worldRepository.existsById(worldId)) {
            throw new NotFoundException("WORLD_NOT_FOUND");
        }

        int capped = Math.min(Math.max(limit, 1), MAX_LIMIT);

        List<ChatMessage> recent = chatMessageRepository
                .findByWorldIdOrderByCreatedAtDescIdDesc(worldId, PageRequest.of(0, capped));

        // 가장 최근 N건 조회 후 뒤집기
        // 최신순으로 가져온 뒤 뒤집어 응답 DTO 목록으로 변환
        List<ChatMessage> ascending = new ArrayList<>(recent);
        Collections.reverse(ascending);

        return ascending.stream()
                .map(message -> new ChatMessageResponse(
                        message.getSenderNickname(),
                        message.getContent(),
                        message.getCreatedAt()))
                .toList();
    }

    private ChatMessageResponse savedResponse(Long worldId, ChatMessage saved) {
        events.publishEvent(new ChatSavedEvent(
                worldId,
                saved.getSenderNickname(),
                saved.getContent(),
                saved.getCreatedAt()
        ));
        return new ChatMessageResponse(
                saved.getSenderNickname(),
                saved.getContent(),
                saved.getCreatedAt()
        );
    }
}
