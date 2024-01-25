package com.jsm.boardgame.service.auth;

import com.jsm.boardgame.common.jwt.AuthToken;
import com.jsm.boardgame.common.jwt.AuthTokenProvider;
import com.jsm.boardgame.entity.rds.member.Member;
import com.jsm.boardgame.entity.redis.auth.LockedAuthToken;
import com.jsm.boardgame.entity.redis.auth.LoginAuthToken;
import com.jsm.boardgame.exception.ApiException;
import com.jsm.boardgame.exception.ErrorCodeType;
import com.jsm.boardgame.repository.rds.member.MemberRepository;
import com.jsm.boardgame.repository.redis.auth.LockedAuthTokenRepository;
import com.jsm.boardgame.repository.redis.auth.LoginAuthTokenRepository;
import com.jsm.boardgame.web.dto.request.auth.LoginRequestDto;
import com.jsm.boardgame.web.dto.request.auth.ReissueRequestDto;
import com.jsm.boardgame.web.dto.response.auth.LoginTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenProvider authTokenProvider;
    private final LoginAuthTokenRepository loginAuthTokenRepository;
    private final LockedAuthTokenRepository lockedAuthTokenRepository;

    @Value("${security.jwt.refresh.length}")
    private int refreshTokenLength;

    @Value("${security.jwt.refresh.expiry-seconds}")
    private long refreshExpirySeconds;

    @Value("${security.jwt.refresh.deadline-days}")
    private long refreshDeadlineDays;

    @Transactional
    public LoginTokenResponseDto login(LoginRequestDto requestDto) {
        Member member = memberRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new ApiException(ErrorCodeType.LOGIN_MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new ApiException(ErrorCodeType.NOT_MATCH_PASSWORD);
        }

        AuthToken authToken = authTokenProvider.createAuthToken(member);
        String accessToken = authToken.getToken();
        String refreshToken = RandomStringUtils.randomAlphanumeric(refreshTokenLength);

        loginAuthTokenRepository.save(
                LoginAuthToken.builder()
                        .identifier(authToken.getIdentifier())
                        .memberId(member.getId())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .refreshTokenDeadlineDateTime(LocalDateTime.now().plusDays(refreshDeadlineDays))
                        .expirySeconds(refreshExpirySeconds)
                        .build()
        );

        return new LoginTokenResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public LoginTokenResponseDto reissue(ReissueRequestDto requestDto) {
        AuthToken authToken = authTokenProvider.convertAuthToken(requestDto.getAccessToken());

        if (authToken.validate()) {
            lockAuthToken(authToken);

            throw new ApiException(ErrorCodeType.AUTH_TOKEN_BEFORE_EXPIRED);
        }

        Long memberId = authToken.getMemberId();
        if (memberId == null) {
            lockAuthToken(authToken);

            throw new ApiException(ErrorCodeType.REISSUE_MEMBER_NOT_FOUND);
        }

        LoginAuthToken loginAuthToken = loginAuthTokenRepository.findById(authToken.getIdentifier())
                .orElseThrow(() -> new ApiException(ErrorCodeType.LOGIN_AUTH_TOKEN_NOT_FOUND));
        if (!loginAuthToken.validateReissue(requestDto.getAccessToken(), requestDto.getRefreshToken())) {
            lockAuthToken(authToken);

            throw new ApiException(ErrorCodeType.VALIDATE_REISSUE);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCodeType.REISSUE_MEMBER_NOT_FOUND));
        LoginAuthToken newLoginAuthToken = loginAuthToken.reissue(
                authTokenProvider.reissueAuthToken(member, authToken.getIdentifier()).getToken(),
                refreshTokenLength,
                refreshDeadlineDays,
                refreshExpirySeconds
        );
        loginAuthTokenRepository.save(newLoginAuthToken);

        return new LoginTokenResponseDto(newLoginAuthToken.getAccessToken(), newLoginAuthToken.getRefreshToken());
    }

    private void lockAuthToken(AuthToken authToken) {
        loginAuthTokenRepository.deleteById(authToken.getIdentifier());
        lockedAuthTokenRepository.save(new LockedAuthToken(authToken));
    }
}
