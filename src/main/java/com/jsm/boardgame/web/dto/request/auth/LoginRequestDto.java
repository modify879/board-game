package com.jsm.boardgame.web.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginRequestDto {

    @NotNull(message = "아이디를 입력하세요.")
    @NotBlank(message = "아이디를 입력하세요.")
    @Size(min = 5, max = 20, message = "아이디 또는 비밀번호가 올바르지 않습니다.")
    private String username;

    @NotNull(message = "비밀번호를 입력하세요.")
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 8, max = 20, message = "아이디 또는 비밀번호가 올바르지 않습니다.")
    private String password;

    @Builder
    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
