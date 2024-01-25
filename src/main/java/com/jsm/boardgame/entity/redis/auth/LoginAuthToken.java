package com.jsm.boardgame.entity.redis.auth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@RedisHash(value = "LoginAuthToken")
public class LoginAuthToken {

    @Id
    private String identifier;

    private Long memberId;

    private String accessToken;

    private String refreshToken;

    private LocalDateTime refreshTokenDeadlineDateTime;

    @TimeToLive
    private long expirySeconds;

    @Builder
    public LoginAuthToken(String identifier, Long memberId, String accessToken, String refreshToken, long expirySeconds, LocalDateTime refreshTokenDeadlineDateTime) {
        this.identifier = identifier;
        this.memberId = memberId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshTokenDeadlineDateTime = refreshTokenDeadlineDateTime;
        this.expirySeconds = expirySeconds;
    }

    public boolean validateReissue(String accessToken, String refreshToken) {
        return this.accessToken.equals(accessToken) && this.refreshToken.equals(refreshToken);
    }

    public LoginAuthToken reissue(String newAccessToken, int refreshTokenLength, long refreshDeadlineDays, long expirySeconds) {
        this.accessToken = newAccessToken;
        this.expirySeconds = expirySeconds;

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(refreshTokenDeadlineDateTime)) {
            this.refreshToken = RandomStringUtils.randomAlphanumeric(refreshTokenLength);
            this.refreshTokenDeadlineDateTime = now.plusDays(refreshDeadlineDays);
        }

        return this;
    }
}
