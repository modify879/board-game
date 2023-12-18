package com.jsm.boardgame.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCodeType {

    ENUM_CODE_CONVERT("잘못된 요청입니다."),
    PASSWORD_NOT_EQUAL("비밀번호가 일치하지 않습니다."),
    EXISTS_USERNAME("이미 존재하는 아이디입니다."),
    EXISTS_NICKNAME("이미 존재하는 닉네임입니다."),
    LOGIN_MEMBER_NOT_FOUND("아이디 또는 비밀번호가 올바르지 않습니다."),
    NOT_MATCH_PASSWORD("아이디 또는 비밀번호가 올바르지 않습니다."),
    REISSUE_MEMBER_NOT_FOUND("잘못된 요청입니다."),
    AUTH_TOKEN_BEFORE_EXPIRED("잘못된 요청입니다."),
    LOGIN_AUTH_TOKEN_NOT_FOUND("잘못된 요청입니다."),
    VALIDATE_REISSUE("잘못된 요청입니다."),
    UNAUTHORIZED("로그인이 필요한 서비스입니다."),
    UPDATE_MEMBER_NOT_FOUND("존재하지 않는 회원입니다.");

    private final String message;
}
