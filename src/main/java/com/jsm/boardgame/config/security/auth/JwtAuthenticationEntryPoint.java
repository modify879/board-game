package com.jsm.boardgame.config.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsm.boardgame.exception.ErrorCodeType;
import com.jsm.boardgame.web.dto.response.error.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        sendResponse(response, ErrorCodeType.UNAUTHORIZED, objectMapper);
    }

    static void sendResponse(HttpServletResponse response, ErrorCodeType errorCodeType, ObjectMapper objectMapper) throws IOException {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(errorCodeType.getMessage());
        String responseBody = objectMapper.writeValueAsString(apiErrorResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorCodeType.getHttpStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(responseBody);
    }
}
