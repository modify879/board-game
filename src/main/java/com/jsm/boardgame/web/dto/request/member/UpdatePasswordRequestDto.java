package com.jsm.boardgame.web.dto.request.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdatePasswordRequestDto {

    @NotNull(message = "비밀번호를 입력하세요.")
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 {min}~{max}자 이내로 입력하세요.")
    private String password;

    @NotNull(message = "비밀번호 확인을 입력하세요.")
    @NotBlank(message = "비밀번호 확인을 입력하세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 확인은 {min}~{max}자 이내로 입력하세요.")
    private String rePassword;

    @Builder
    public UpdatePasswordRequestDto(String password, String rePassword) {
        this.password = password;
        this.rePassword = rePassword;
    }

    public boolean checkPassword() {
        return password.equals(rePassword);
    }
}
