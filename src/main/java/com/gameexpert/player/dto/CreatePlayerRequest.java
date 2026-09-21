package com.gameexpert.player.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreatePlayerRequest {

    // 2~12글자의 영문 대소문자, 숫자, 밑줄(_)만 허용합니다.
    // - @NotBlank   : null, "", 공백만 있는 문자열을 모두 거절합니다.
    // - @Size(2~12) : 길이가 범위를 벗어나면 거절합니다.
    // - @Pattern    : 정규식(^[a-zA-Z0-9_]+$)에 맞지 않는 문자(한글, 공백, 특수문자 등)를 거절합니다.
    @NotBlank
    @Size(min = 2, max = 12)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    private final String nickname;

    public CreatePlayerRequest(String nickname) {
        this.nickname = nickname;
    }
}
