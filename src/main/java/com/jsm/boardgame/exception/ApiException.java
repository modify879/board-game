package com.jsm.boardgame.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private final ErrorCodeType errorCodeType;
    private final HttpStatus httpStatus;

    public ApiException(ErrorCodeType errorCodeType) {
        super(errorCodeType.getMessage());
        this.errorCodeType = errorCodeType;
        this.httpStatus = errorCodeType.getHttpStatus();
    }

    public ApiException(ErrorCodeType errorCodeType, String cause) {
        super(errorCodeType.getMessage(), new Throwable(cause));
        this.errorCodeType = errorCodeType;
        this.httpStatus = errorCodeType.getHttpStatus();
    }
}
