package com.gameexpert.player.service;

import com.gameexpert.player.repository.PlayerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gameexpert.common.ConflictException;
import com.gameexpert.player.dto.CreatePlayerRequest;
import com.gameexpert.player.entity.Player;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Transactional
    public void createPlayer(CreatePlayerRequest request) {
        // 저장 전 닉네임 중복 먼저 확인, 대부분의 중복 요청은 DB에 INSERT를 시도하지도 않고 거절
        if (playerRepository.existsByNickname(request.getNickname())) {
            throw new ConflictException("DUPLICATE_NICKNAME");
        }
        // 중복이 아니면 저장. 두 요청이 동시에 이 지점을 통과하는 경쟁 상태는
        // savePlayer() 안에서 DB의 UNIQUE 제약 위반(DataIntegrityViolationException)을 잡아
        // ConflictException으로 변환
        savePlayer(new Player(request.getNickname()));
    }

    private void savePlayer(Player player) {
        try {
            playerRepository.saveAndFlush(player);
        } catch (org.springframework.dao.DataIntegrityViolationException failure) {
            throw new ConflictException("DUPLICATE_NICKNAME");
        }
    }
}
