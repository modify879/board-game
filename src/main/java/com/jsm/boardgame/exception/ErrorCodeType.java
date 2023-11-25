package com.jsm.boardgame.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCodeType {

    ENUM_CODE_CONVERT("잘못된 요청입니다."),
    PASSWORD_NOT_CORRECT("비밀번호가 일치하지 않습니다."),
    EXISTS_USERNAME("이미 존재하는 아이디입니다."),
    EXISTS_NICKNAME("이미 존재하는 닉네임입니다.");

    private final String message;
}
