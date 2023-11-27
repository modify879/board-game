package com.jsm.boardgame.web.controller.auth;

import com.jsm.boardgame.common.jwt.AuthToken;
import com.jsm.boardgame.common.jwt.AuthTokenProvider;
import com.jsm.boardgame.entity.rds.member.Member;
import com.jsm.boardgame.entity.redis.auth.LoginAuthToken;
import com.jsm.boardgame.exception.ErrorCodeType;
import com.jsm.boardgame.repository.rds.member.MemberRepository;
import com.jsm.boardgame.repository.redis.auth.LoginAuthTokenRepository;
import com.jsm.boardgame.web.controller.support.AcceptanceTest;
import com.jsm.boardgame.web.dto.request.auth.LoginRequestDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

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

            LoginAuthToken loginAuthToken = loginAuthTokenRepository.findById(member.getId() + ":" + authToken.getIdentifier()).orElseThrow();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(accessToken).isNotBlank();
            assertThat(refreshToken).isNotBlank();

            assertThat(loginAuthToken.getMemberId()).isEqualTo(member.getId());
            assertThat(loginAuthToken.getKey()).isEqualTo(member.getId() + ":" + authToken.getIdentifier());
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
}