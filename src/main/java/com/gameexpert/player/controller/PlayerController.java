package com.gameexpert.player.controller;

import com.gameexpert.player.dto.CreatePlayerRequest;
import com.gameexpert.player.service.PlayerService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    // API 명세: POST /players, 성공 시 본문 없는 201.
    // @Valid가 컨트롤러 메서드 진입 전 CreatePlayerRequest의 검증 어노테이션을 실행
    @PostMapping("/players")
    public ResponseEntity<Void> create(@Valid @RequestBody CreatePlayerRequest request) {
        playerService.createPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
