package com.jsm.boardgame.web.controller.member;

import com.jsm.boardgame.entity.rds.member.Member;
import com.jsm.boardgame.exception.ErrorCodeType;
import com.jsm.boardgame.repository.rds.member.MemberRepository;
import com.jsm.boardgame.web.controller.support.AcceptanceTest;
import com.jsm.boardgame.web.dto.request.member.CreateMemberRequest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class MemberControllerTest extends AcceptanceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    class existsUsername {

        @Test
        void 아이디가_이미_존재한다() {
            // given
            String username = "existUsername";

            memberRepository.save(Member.builder()
                    .username(username)
                    .password("testPassword")
                    .nickname("testNickname")
                    .profile("http://localhost:8080/testProfile.jpg")
                    .point(0)
                    .role(Member.Role.MEMBER)
                    .build());

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .when().get("/api/v1/member/exists/username?username={username}", username)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.as(Boolean.class)).isEqualTo(true);
        }

        @Test
        void 아이디가_존재하지_않는다() {
            // given
            String username = "testUsername";

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .when().get("/api/v1/member/exists/username?username={username}", username)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.as(Boolean.class)).isEqualTo(false);
        }
    }

    @Nested
    class createMember {

        @Test
        void 회원을_생성한다() {
            // given
            CreateMemberRequest request = CreateMemberRequest.builder()
                    .username("testUsername")
                    .password("testPassword123")
                    .rePassword("testPassword123")
                    .nickname("testNick")
                    .profile("http://localhost:8080/testProfile.jpg")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/api/v1/member")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 이미_아이디가_존재한다() {
            // given
            String username = "existUsername";

            memberRepository.save(Member.builder()
                    .username(username)
                    .password("password")
                    .nickname("nickname")
                    .profile("http://localhost:8080/profile.jpg")
                    .role(Member.Role.MEMBER)
                    .point(0)
                    .build());

            CreateMemberRequest request = CreateMemberRequest.builder()
                    .username(username)
                    .password("testPassword123")
                    .rePassword("testPassword123")
                    .nickname("testNick")
                    .profile("http://localhost:8080/testProfile.jpg")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/api/v1/member")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getString("message")).isEqualTo(ErrorCodeType.EXISTS_USERNAME.getMessage());
        }

        @Test
        void 이미_닉네임이_존재한다() {
            // given
            String nickname = "existNickname";

            memberRepository.save(Member.builder()
                    .username("username")
                    .password("password")
                    .nickname(nickname)
                    .profile("http://localhost:8080/profile.jpg")
                    .role(Member.Role.MEMBER)
                    .point(0)
                    .build());

            CreateMemberRequest request = CreateMemberRequest.builder()
                    .username("testUsername")
                    .password("testPassword123")
                    .rePassword("testPassword123")
                    .nickname(nickname)
                    .profile("http://localhost:8080/testProfile.jpg")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/api/v1/member")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getString("message")).isEqualTo(ErrorCodeType.EXISTS_NICKNAME.getMessage());
        }
    }
}