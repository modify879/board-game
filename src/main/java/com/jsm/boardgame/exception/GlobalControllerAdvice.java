package com.jsm.boardgame.exception;

import com.jsm.boardgame.web.dto.response.error.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ApiErrorResponse> handlerApiException(ApiException e) {
        Throwable throwable = e.getCause();
        if (throwable != null) {
            log.error(throwable.getMessage(), e);
        }

        return ResponseEntity.status(e.getHttpStatus()).body(new ApiErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiErrorResponse> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = "";
        if (fieldError != null) {
            message = fieldError.getDefaultMessage();
        }

        return ResponseEntity.badRequest().body(new ApiErrorResponse(message));
    }

    @ExceptionHandler(MissingRequestValueException.class)
    protected ResponseEntity<ApiErrorResponse> handlerMissingRequestValue(MissingRequestValueException e) {
        return ResponseEntity.ok(new ApiErrorResponse("잘못된 요청입니다."));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    protected ResponseEntity<ApiErrorResponse> handlerMissingRequestValue(MissingServletRequestPartException e) {
        return ResponseEntity.ok(new ApiErrorResponse("잘못된 요청입니다."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiErrorResponse> handlerHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.ok(new ApiErrorResponse("잘못된 요청입니다."));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiErrorResponse> handlerException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.internalServerError().body(new ApiErrorResponse("예상치 못한 오류가 발생했습니다.\n관리자에게 문의 바랍니다."));
    }
}
