package com.jsm.boardgame.web.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jsm.boardgame.entity.rds.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AuthCheckResponseDto {

    private boolean login;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nickname;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String profile;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String role;

    @Builder
    public AuthCheckResponseDto(boolean login, String nickname, String profile, String role) {
        this.login = login;
        this.nickname = nickname;
        this.profile = profile;
        this.role = role;
    }

    public static AuthCheckResponseDto success(Member member) {
        return AuthCheckResponseDto.builder()
                .login(true)
                .nickname(member.getNickname())
                .profile(member.getProfile())
                .role(member.getRole().getCode())
                .build();
    }

    public static AuthCheckResponseDto fail() {
        return AuthCheckResponseDto.builder()
                .login(false)
                .build();
    }
}
