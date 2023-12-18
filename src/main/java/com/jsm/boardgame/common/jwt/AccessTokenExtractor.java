package com.jsm.boardgame.common.jwt;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.Optional;

public class AccessTokenExtractor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer";

    public static Optional<String> extract(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION);
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.startsWith(BEARER)) {
                return Optional.of(value.split(" ")[1]);
            }
        }

        return Optional.empty();
    }
}
