package com.gameexpert.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gameexpert.chat.dto.ChatMessageResponse;
import com.gameexpert.chat.service.RecentChatQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WorldChatController {

    private final RecentChatQueryService chatService;

    // API 명세: GET /worlds/{worldId}/chats?limit=50, 성공 200.
    // {worldId}=경로 변수=> @PathVariable
    // limit=>선택 파라미터이므로 @RequestParam(defaultValue = "50")
    // 쿼리스트링에 limit가 없어도 자동으로 50이 적용=>별도 null 체크 불필요.
    @GetMapping("/worlds/{worldId}/chats")
    public ResponseEntity<List<ChatMessageResponse>> chats(
            @PathVariable Long worldId,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(chatService.getRecentMessages(worldId, limit));
    }
}
