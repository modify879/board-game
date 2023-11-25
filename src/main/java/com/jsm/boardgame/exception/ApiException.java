package com.jsm.boardgame.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ErrorCodeType errorCodeType;

    public ApiException(ErrorCodeType errorCodeType) {
        super(errorCodeType.getMessage());
        this.errorCodeType = errorCodeType;
    }

    public ApiException(ErrorCodeType errorCodeType, String cause) {
        super(errorCodeType.getMessage(), new Throwable(cause));
        this.errorCodeType = errorCodeType;
    }
}
