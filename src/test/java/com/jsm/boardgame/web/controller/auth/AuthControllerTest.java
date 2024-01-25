package com.jsm.boardgame.web.controller.auth;

import com.jsm.boardgame.common.jwt.AuthToken;
import com.jsm.boardgame.common.jwt.AuthTokenProvider;
import com.jsm.boardgame.entity.rds.member.Member;
import com.jsm.boardgame.entity.redis.auth.LockedAuthToken;
import com.jsm.boardgame.entity.redis.auth.LoginAuthToken;
import com.jsm.boardgame.exception.ErrorCodeType;
import com.jsm.boardgame.repository.rds.member.MemberRepository;
import com.jsm.boardgame.repository.redis.auth.LockedAuthTokenRepository;
import com.jsm.boardgame.repository.redis.auth.LoginAuthTokenRepository;
import com.jsm.boardgame.web.controller.support.AcceptanceTest;
import com.jsm.boardgame.web.dto.request.auth.LoginRequestDto;
import com.jsm.boardgame.web.dto.request.auth.ReissueRequestDto;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest extends AcceptanceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginAuthTokenRepository loginAuthTokenRepository;

    @Autowired
    private AuthTokenProvider authTokenProvider;

    @Autowired
    private LockedAuthTokenRepository lockedAuthTokenRepository;

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.refresh.expiry-seconds}")
    private long refreshExpirySeconds;

    @Nested
    class login {

        @Test
        void 로그인을_한다() {
            // given
            String username = "testUsername";
            String password = "testPassword";

            Member member = createMember(username, password);
            LoginRequestDto requestDto = LoginRequestDto.builder()
                    .username(username)
                    .password(password)
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(requestDto)
                    .when().post("/api/v1/auth/login")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();
            String accessToken = jsonPath.getString("accessToken");
            String refreshToken = jsonPath.getString("refreshToken");

            AuthToken authToken = authTokenProvider.convertAuthToken(accessToken);

            LoginAuthToken loginAuthToken = loginAuthTokenRepository.findById(authToken.getIdentifier()).orElseThrow();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(accessToken).isNotBlank();
            assertThat(refreshToken).isNotBlank();

            assertThat(loginAuthToken.getMemberId()).isEqualTo(member.getId());
            assertThat(loginAuthToken.getMemberId()).isEqualTo(member.getId());
            assertThat(loginAuthToken.getIdentifier()).isEqualTo(authToken.getIdentifier());
            assertThat(loginAuthToken.getAccessToken()).isEqualTo(accessToken);
            assertThat(loginAuthToken.getRefreshToken()).isEqualTo(refreshToken);
            assertThat(loginAuthToken.getExpirySeconds()).isNotZero();
        }

        @Test
        void 존재하지_않는_계정이다() {
            // given
            String username = "testUsername";
            String password = "testPassword";

            LoginRequestDto requestDto = LoginRequestDto.builder()
                    .username(username)
                    .password(password)
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(requestDto)
                    .when().post("/api/v1/auth/login")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("message")).isEqualTo(ErrorCodeType.LOGIN_MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        void 비밀번호가_일치하지_않는다() {
            // given
            String username = "testUsername";
            String password = "testPassword";

            createMember(username, password);
            LoginRequestDto requestDto = LoginRequestDto.builder()
                    .username(username)
                    .password("notCorrect")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(requestDto)
                    .when().post("/api/v1/auth/login")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("message")).isEqualTo(ErrorCodeType.NOT_MATCH_PASSWORD.getMessage());
        }

        private Member createMember(String username, String password) {
            return memberRepository.save(Member.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .nickname("testNickname")
                    .profile("http://localhost:8080/profile.jpg")
                    .role(Member.Role.MEMBER)
                    .point(0)
                    .build());
        }
    }

    @Nested
    class reissue {

        @Test
        void 토큰을_재발급한다() {
            // given
            Member member = createMember();
            LoginAuthToken loginAuthToken = createLoginAuthToken(true, member, 6);

            ReissueRequestDto requestDto = ReissueRequestDto.builder()
                    .accessToken(loginAuthToken.getAccessToken())
                    .refreshToken(loginAuthToken.getRefreshToken())
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(requestDto)
                    .when().put("/api/v1/auth/reissue")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            LoginAuthToken getLoginAuthToken = loginAuthTokenRepository.findById(loginAuthToken.getIdentifier()).orElseThrow();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getString("accessToken")).isNotEqualTo(loginAuthToken.getAccessToken());
            assertThat(jsonPath.getString("refreshToken")).isEqualTo(loginAuthToken.getRefreshToken());

            assertThat(getLoginAuthToken.getMemberId()).isEqualTo(loginAuthToken.getMemberId());
            assertThat(getLoginAuthToken.getIdentifier()).isEqualTo(loginAuthToken.getIdentifier());
            assertThat(getLoginAuthToken.getAccessToken()).isNotEqualTo(loginAuthToken.getAccessToken());
            assertThat(getLoginAuthToken.getRefreshToken()).isEqualTo(loginAuthToken.getRefreshToken());
            assertThat(getLoginAuthToken.getRefreshTokenDeadlineDateTime()).isEqualTo(loginAuthToken.getRefreshTokenDeadlineDateTime());
        }

        @Test
        void refresh_token까지_재발급한다() {
            // given
            Member member = createMember();
            LoginAuthToken loginAuthToken = createLoginAuthToken(true, member, -1);

            ReissueRequestDto requestDto = ReissueRequestDto.builder()
                    .accessToken(loginAuthToken.getAccessToken())
                    .refreshToken(loginAuthToken.getRefreshToken())
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(requestDto)
                    .when().put("/api/v1/auth/reissue")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            LoginAuthToken getLoginAuthToken = loginAuthTokenRepository.findById(loginAuthToken.getIdentifier()).orElseThrow();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getString("accessToken")).isNotEqualTo(loginAuthToken.getAccessToken());
            assertThat(jsonPath.getString("refreshToken")).isNotEqualTo(loginAuthToken.getRefreshToken());

            assertThat(getLoginAuthToken.getMemberId()).isEqualTo(loginAuthToken.getMemberId());
            assertThat(getLoginAuthToken.getIdentifier()).isEqualTo(loginAuthToken.getIdentifier());
            assertThat(getLoginAuthToken.getAccessToken()).isNotEqualTo(loginAuthToken.getAccessToken());
            assertThat(getLoginAuthToken.getRefreshToken()).isNotEqualTo(loginAuthToken.getRefreshToken());
            assertThat(getLoginAuthToken.getRefreshTokenDeadlineDateTime()).isAfterOrEqualTo(loginAuthToken.getRefreshTokenDeadlineDateTime());
        }

        @Test
        void 토큰이_만료_전이다() {
            // given
            Member member = createMember();
            LoginAuthToken loginAuthToken = createLoginAuthToken(false, member, 6);

            ReissueRequestDto requestDto = ReissueRequestDto.builder()
                    .accessToken(loginAuthToken.getAccessToken())
                    .refreshToken(loginAuthToken.getRefreshToken())
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(requestDto)
                    .when().put("/api/v1/auth/reissue")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            LoginAuthToken getLoginAuthToken = loginAuthTokenRepository.findById(loginAuthToken.getIdentifier()).orElse(null);
            LockedAuthToken lockedAuthToken = lockedAuthTokenRepository.findById(loginAuthToken.getAccessToken()).orElseThrow();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getString("message")).isEqualTo(ErrorCodeType.AUTH_TOKEN_BEFORE_EXPIRED.getMessage());

            assertThat(getLoginAuthToken).isNull();
            assertThat(lockedAuthToken.getAccessToken()).isEqualTo(loginAuthToken.getAccessToken());
            assertThat(lockedAuthToken.getExpirySeconds()).isLessThanOrEqualTo(3600);
        }

        @Test
        void 토큰에_회원_id가_존재하지_않는다() {
            // given
            Member member = Member.builder()
                    .role(Member.Role.MEMBER)
                    .build();
            LoginAuthToken loginAuthToken = createLoginAuthToken(true, member, 6);

            ReissueRequestDto requestDto = ReissueRequestDto.builder()
                    .accessToken(loginAuthToken.getAccessToken())
                    .refreshToken(loginAuthToken.getRefreshToken())
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(requestDto)
                    .when().put("/api/v1/auth/reissue")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            LoginAuthToken getLoginAuthToken = loginAuthTokenRepository.findById(loginAuthToken.getIdentifier()).orElse(null);
            LockedAuthToken lockedAuthToken = lockedAuthTokenRepository.findById(loginAuthToken.getAccessToken()).orElseThrow();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("message")).isEqualTo(ErrorCodeType.REISSUE_MEMBER_NOT_FOUND.getMessage());

            assertThat(getLoginAuthToken).isNull();
            assertThat(lockedAuthToken.getAccessToken()).isEqualTo(loginAuthToken.getAccessToken());
            assertThat(lockedAuthToken.getExpirySeconds()).isLessThanOrEqualTo(3600);
        }

        @Test
        void LoginAuthToken_Redis가_존재하지_않는다() {
            // given
            Member member = createMember();
            LoginAuthToken loginAuthToken = createLoginAuthToken(true, member, 6);

            loginAuthTokenRepository.delete(loginAuthToken);

            ReissueRequestDto requestDto = ReissueRequestDto.builder()
                    .accessToken(loginAuthToken.getAccessToken())
                    .refreshToken(loginAuthToken.getRefreshToken())
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(requestDto)
                    .when().put("/api/v1/auth/reissue")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            LoginAuthToken getLoginAuthToken = loginAuthTokenRepository.findById(loginAuthToken.getIdentifier()).orElse(null);
            List<LockedAuthToken> lockedAuthToken = IteratorUtils.toList(lockedAuthTokenRepository.findAll().iterator());

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("message")).isEqualTo(ErrorCodeType.LOGIN_AUTH_TOKEN_NOT_FOUND.getMessage());

            assertThat(getLoginAuthToken).isNull();
            assertThat(lockedAuthToken).isEmpty();
        }

        @Test
        void LoginAuthToken_검증_실패() {
            // given
            Member member = createMember();
            LoginAuthToken loginAuthToken = createLoginAuthToken(true, member, 6);

            ReissueRequestDto requestDto = ReissueRequestDto.builder()
                    .accessToken(loginAuthToken.getAccessToken())
                    .refreshToken("notExist")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(requestDto)
                    .when().put("/api/v1/auth/reissue")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            LoginAuthToken getLoginAuthToken = loginAuthTokenRepository.findById(loginAuthToken.getAccessToken()).orElse(null);
            LockedAuthToken lockedAuthToken = lockedAuthTokenRepository.findById(loginAuthToken.getAccessToken()).orElseThrow();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("message")).isEqualTo(ErrorCodeType.REISSUE_MEMBER_NOT_FOUND.getMessage());

            assertThat(getLoginAuthToken).isNull();
            assertThat(lockedAuthToken.getAccessToken()).isEqualTo(loginAuthToken.getAccessToken());
            assertThat(lockedAuthToken.getExpirySeconds()).isLessThanOrEqualTo(3600);
        }

        private Member createMember() {
            return memberRepository.save(Member.builder()
                    .username("testUsername")
                    .password(passwordEncoder.encode("testPassword"))
                    .nickname("testNickname")
                    .profile("http://localhost:8080/profile.jpg")
                    .role(Member.Role.MEMBER)
                    .point(0)
                    .build());
        }

        private LoginAuthToken createLoginAuthToken(boolean isAuthTokenExpired, Member member, int refreshDeadlineDays) {
            AuthToken authToken = new AuthToken(Keys.hmacShaKeyFor(secretKey.getBytes()), member, UUID.randomUUID().toString(), isAuthTokenExpired ? 0 : 3600);
            return loginAuthTokenRepository.save(
                    LoginAuthToken.builder()
                            .identifier(authToken.getIdentifier())
                            .memberId(member.getId())
                            .accessToken(authToken.getToken())
                            .refreshToken(RandomStringUtils.randomAlphanumeric(256))
                            .refreshTokenDeadlineDateTime(LocalDateTime.now().plusDays(refreshDeadlineDays))
                            .expirySeconds(refreshExpirySeconds)
                            .build()
            );
        }
    }
}