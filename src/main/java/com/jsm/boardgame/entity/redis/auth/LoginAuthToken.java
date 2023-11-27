package com.jsm.boardgame.entity.redis.auth;

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
    private String key;

    private Long memberId;

    private String identifier;

    private String accessToken;

    private String refreshToken;

    @TimeToLive
    private long expirySeconds;

    @Builder
    public LoginAuthToken(String key, Long memberId, String identifier, String accessToken, String refreshToken, long expirySeconds) {
        this.key = key;
        this.memberId = memberId;
        this.identifier = identifier;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirySeconds = expirySeconds;
    }
}
