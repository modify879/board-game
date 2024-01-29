package com.jsm.boardgame.config.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsm.boardgame.exception.ErrorCodeType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.jsm.boardgame.config.security.auth.JwtAuthenticationEntryPoint.sendResponse;

@RequiredArgsConstructor
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        sendResponse(response, ErrorCodeType.FORBIDDEN, objectMapper);
    }
}
