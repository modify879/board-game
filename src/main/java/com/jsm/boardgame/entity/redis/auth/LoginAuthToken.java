package com.jsm.boardgame.entity.redis.auth;

import com.jsm.boardgame.web.dto.response.auth.LoginTokenResponseDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@RedisHash(value = "LoginAuthToken")
public class LoginAuthToken {

    @Id
    private Long memberId;

    private LoginTokenResponseDto loginToken;

    @TimeToLive
    private long expirySeconds;

    @Builder
    public LoginAuthToken(Long memberId, LoginTokenResponseDto loginToken, long expirySeconds) {
        this.memberId = memberId;
        this.loginToken = loginToken;
        this.expirySeconds = expirySeconds;
    }
}
