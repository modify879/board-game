package com.jsm.boardgame.config.security.auth;

import com.jsm.boardgame.entity.rds.member.Member;
import com.jsm.boardgame.exception.ErrorCodeType;
import com.jsm.boardgame.repository.rds.member.MemberRepository;
import com.jsm.boardgame.web.controller.support.AcceptanceTest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;

@Import({TestSecurityConfig.class})
public class SecurityControllerTest extends AcceptanceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    class allowOnlyAdmin {

        @Test
        void admin_권한이_있는_member() {
            // given
            Member member = createMember(Member.Role.ADMIN);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(getAuthToken(member).getToken())
                    .when().get("/api/t1/auth/allow/only-admin")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void admin_권한이_없는_member() {
            // given
            Member member = createMember(Member.Role.MEMBER);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(getAuthToken(member).getToken())
                    .when().get("/api/t1/auth/allow/only-admin")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(jsonPath.getString("message")).isEqualTo(ErrorCodeType.FORBIDDEN.getMessage());
        }

        @Test
        void 로그인을_안한_member() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .when().get("/api/t1/auth/allow/only-admin")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
            assertThat(jsonPath.getString("message")).isEqualTo(ErrorCodeType.UNAUTHORIZED.getMessage());
        }

        private Member createMember(Member.Role role) {
            return memberRepository.save(Member.builder()
                    .username("testUsername")
                    .password("testPassword")
                    .nickname("testNick")
                    .profile("http://localhost:8080/testProfile.jpg")
                    .point(0)
                    .role(role)
                    .build());
        }
    }

    @RestController
    @RequestMapping("/api/t1/auth")
    public static class AuthController {

        @GetMapping("/allow/only-admin")
        public ResponseEntity<Void> allowOnlyAdmin() {
            return ResponseEntity.ok().build();
        }
    }
}
