package com.jsm.boardgame.entity.redis.auth;

import com.jsm.boardgame.common.jwt.AuthToken;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Duration;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@RedisHash(value = "LockedAuthToken")
public class LockedAuthToken {

    @Id
    private String accessToken;

    @TimeToLive
    private long expirySeconds;

    @Builder
    public LockedAuthToken(String accessToken, long expirySeconds) {
        this.accessToken = accessToken;
        this.expirySeconds = expirySeconds;
    }

    public LockedAuthToken(AuthToken authToken) {
        this.accessToken = authToken.getToken();
        this.expirySeconds = Duration.between(LocalDateTime.now(), authToken.getExpiration()).getSeconds();
    }
}
