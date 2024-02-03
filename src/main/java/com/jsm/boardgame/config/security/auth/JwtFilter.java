package com.jsm.boardgame.config.security.auth;

import com.jsm.boardgame.common.jwt.AccessTokenExtractor;
import com.jsm.boardgame.common.jwt.AuthToken;
import com.jsm.boardgame.common.jwt.AuthTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final AuthTokenProvider authTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AccessTokenExtractor.extract(request).ifPresent(accessToken -> {
            AuthToken authToken = authTokenProvider.convertAuthToken(accessToken);
            if (authToken.validate()) {
                Authentication authentication = authToken.getAuthentication();
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        });

        filterChain.doFilter(request, response);
    }
}
