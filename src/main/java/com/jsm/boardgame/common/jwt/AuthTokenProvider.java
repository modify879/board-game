package com.jsm.boardgame.common.jwt;

import com.jsm.boardgame.entity.rds.member.Member;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.UUID;

@Component
public class AuthTokenProvider {

    private final SecretKey secretKey;
    private final long expirySeconds;

    public AuthTokenProvider(@Value("${security.jwt.secret-key}") String secretKey,
                             @Value("${security.jwt.expiry-seconds}") long expirySeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirySeconds = expirySeconds;
    }

    public AuthToken createAuthToken(Member member) {
        return new AuthToken(secretKey, member, UUID.randomUUID().toString(), expirySeconds);
    }

    public AuthToken reissueAuthToken(Member member, String identifier) {
        return new AuthToken(secretKey, member, identifier, expirySeconds);
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(secretKey, token);
    }
}
