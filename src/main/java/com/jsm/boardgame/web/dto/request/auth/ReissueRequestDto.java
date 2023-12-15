package com.jsm.boardgame.web.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReissueRequestDto {

    @NotNull(message = "잘못된 요청입니다.")
    @NotBlank(message = "잘못된 요청입니다.")
    private String accessToken;

    @NotNull(message = "잘못된 요청입니다.")
    @NotBlank(message = "잘못된 요청입니다.")
    private String refreshToken;

    @Builder
    public ReissueRequestDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
