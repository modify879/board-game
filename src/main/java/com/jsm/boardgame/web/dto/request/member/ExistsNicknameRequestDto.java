package com.jsm.boardgame.web.dto.request.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ExistsNicknameRequestDto {

    @NotNull(message = "닉네임을 입력하세요.")
    @NotBlank(message = "닉네임을 입력하세요.")
    @Size(min = 2, max = 15, message = "닉네임은 {min}~{max}자 이내로 입력하세요.")
    private String nickname;

    @Builder
    public ExistsNicknameRequestDto(String nickname) {
        this.nickname = nickname;
    }
}
