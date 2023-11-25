package com.jsm.boardgame.exception;

import com.jsm.boardgame.web.dto.response.error.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ApiErrorResponse> handlerApiException(ApiException e) {
        Throwable throwable = e.getCause();
        if (throwable != null) {
            log.error(throwable.getMessage(), e);
        }

        return ResponseEntity.ok(new ApiErrorResponse(e.getMessage()));
    }
}
