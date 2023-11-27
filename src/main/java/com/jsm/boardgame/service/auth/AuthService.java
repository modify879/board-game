package com.jsm.boardgame.service.auth;

import com.jsm.boardgame.common.jwt.AuthToken;
import com.jsm.boardgame.common.jwt.AuthTokenProvider;
import com.jsm.boardgame.entity.rds.member.Member;
import com.jsm.boardgame.entity.redis.auth.LoginAuthToken;
import com.jsm.boardgame.exception.ApiException;
import com.jsm.boardgame.exception.ErrorCodeType;
import com.jsm.boardgame.repository.rds.member.MemberRepository;
import com.jsm.boardgame.repository.redis.auth.LoginAuthTokenRepository;
import com.jsm.boardgame.web.dto.request.auth.LoginRequestDto;
import com.jsm.boardgame.web.dto.response.auth.LoginTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenProvider authTokenProvider;
    private final LoginAuthTokenRepository loginAuthTokenRepository;

    @Value("${security.jwt.refresh.expiry-seconds}")
    long refreshExpirySeconds;

    @Transactional
    public LoginTokenResponseDto login(LoginRequestDto requestDto) {
        Member member = memberRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new ApiException(ErrorCodeType.LOGIN_MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new ApiException(ErrorCodeType.NOT_MATCH_PASSWORD);
        }

        AuthToken authToken = authTokenProvider.createAuthToken(member);
        LoginTokenResponseDto responseDto = new LoginTokenResponseDto(authToken.getToken(), authToken.getToken());
        loginAuthTokenRepository.save(
                LoginAuthToken.builder()
                        .memberId(member.getId())
                        .loginToken(responseDto)
                        .expirySeconds(refreshExpirySeconds)
                        .build()
        );

        return responseDto;
    }
}
