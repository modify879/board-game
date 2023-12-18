package com.jsm.boardgame.web.dto.request.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ExistsUsernameRequestDto {

    @NotNull(message = "아이디를 입력하세요.")
    @NotBlank(message = "아이디를 입력하세요.")
    @Size(min = 5, max = 20, message = "아이디는 {min}~{max}자 이내로 입력하세요.")
    private String username;

    @Builder
    public ExistsUsernameRequestDto(String username) {
        this.username = username;
    }
}
