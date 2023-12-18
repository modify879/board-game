package com.jsm.boardgame.web.dto.request.member;

import com.jsm.boardgame.entity.rds.member.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.security.crypto.password.PasswordEncoder;

@NoArgsConstructor
@Getter
public class CreateMemberRequestDto {

    @NotNull(message = "아이디를 입력하세요.")
    @NotBlank(message = "아이디를 입력하세요.")
    @Size(min = 5, max = 20, message = "아이디는 {min}~{max}자 이내로 입력하세요.")
    private String username;

    @NotNull(message = "비밀번호를 입력하세요.")
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 {min}~{max}자 이내로 입력하세요.")
    private String password;

    @NotNull(message = "비밀번호 확인을 입력하세요.")
    @NotBlank(message = "비밀번호 확인을 입력하세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 확인은 {min}~{max}자 이내로 입력하세요.")
    private String rePassword;

    @NotNull(message = "닉네임을 입력하세요.")
    @NotBlank(message = "닉네임을 입력하세요.")
    @Size(min = 2, max = 15, message = "닉네임은 {min}~{max}자 이내로 입력하세요.")
    private String nickname;

    @URL(message = "올바른 URL 형식이 아닙니다.")
    private String profile;

    @Builder
    public CreateMemberRequestDto(String username, String password, String rePassword, String nickname, String profile) {
        this.username = username;
        this.password = password;
        this.rePassword = rePassword;
        this.nickname = nickname;
        this.profile = profile;
    }

    public Member toMember(PasswordEncoder encoder) {
        return Member.builder()
                .username(username.trim())
                .password(encoder.encode(password.trim()))
                .nickname(nickname.trim())
                .point(0)
                .profile(profile)
                .role(Member.Role.MEMBER)
                .build();
    }

    public boolean checkPassword() {
        return password.equals(rePassword);
    }
}
