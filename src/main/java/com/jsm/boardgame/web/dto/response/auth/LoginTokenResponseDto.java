package com.jsm.boardgame.web.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginTokenResponseDto {

    private String accessToken;
    private String refreshToken;
}
