package com.jsm.boardgame.common.jwt;

import com.jsm.boardgame.entity.rds.member.Member;
import com.jsm.boardgame.utils.EnumCodeConverterUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AuthToken {

    private final SecretKey secretKey;

    @Getter
    private final String token;

    private static final String MEMBER_ID = "memberId";
    private static final String IDENTIFIER = "identifier";
    private static final String ROLE = "role";

    public AuthToken(SecretKey secretKey, Member member, String identifier, long expirySeconds) {
        this.secretKey = secretKey;
        this.token = createAuthToken(member, identifier, expirySeconds);
    }

    public boolean validate() {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String createAuthToken(Member member, String identifier, long expirySeconds) {
        return Jwts.builder()
                .signWith(secretKey, Jwts.SIG.HS256)
                .claim(MEMBER_ID, member.getId())
                .claim(IDENTIFIER, identifier)
                .claim(ROLE, member.getRole().getCode())
                .expiration(new Date(new Date().getTime() + (expirySeconds * 1000)))
                .compact();
    }

    public Long getMemberId() {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get(MEMBER_ID, Long.class);
        } catch (ExpiredJwtException e) {
            return e.getClaims().get(MEMBER_ID, Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getIdentifier() {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get(IDENTIFIER, String.class);
        } catch (ExpiredJwtException e) {
            return e.getClaims().get(IDENTIFIER, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public LocalDateTime getExpiration() {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getExpiration()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }

    private Member.Role getRole() {
        try {
            return EnumCodeConverterUtils.ofCode(Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get(ROLE, String.class), Member.Role.class);
        } catch (ExpiredJwtException e) {
            return EnumCodeConverterUtils.ofCode(e.getClaims().get(ROLE, String.class), Member.Role.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Authentication getAuthentication() {
        Authentication authentication = null;

        Member.Role role = getRole();
        if (role != null) {
            authentication = new UsernamePasswordAuthenticationToken(
                    this.getMemberId(),
                    this.token,
                    Arrays.stream(Member.Role.values())
                            .filter(v -> role.ordinal() <= v.ordinal())
                            .map(v -> new SimpleGrantedAuthority(v.name()))
                            .collect(Collectors.toSet())
            );
        }

        return authentication;
    }
}
