package com.jsm.boardgame.web.dto.request.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@NoArgsConstructor
@Getter
public class UpdateProfileRequestDto {

    @NotNull(message = "닉네임을 입력하세요.")
    @NotBlank(message = "닉네임을 입력하세요.")
    @URL(message = "올바른 URL 형식이 아닙니다.")
    private String profile;

    @Builder
    public UpdateProfileRequestDto(String profile) {
        this.profile = profile;
    }
}
