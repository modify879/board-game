package com.jsm.boardgame.web.controller.game;

import com.jsm.boardgame.entity.rds.game.GameRoom;
import com.jsm.boardgame.entity.rds.member.Member;
import com.jsm.boardgame.repository.rds.member.MemberRepository;
import com.jsm.boardgame.web.controller.support.AcceptanceTest;
import com.jsm.boardgame.web.dto.request.game.CreateGameRoomRequestDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class GameRoomControllerTest extends AcceptanceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    class createGameRoom {

        @Test
        void 게임방을_생성한다() {
            // given
            Member member = createMember();

            CreateGameRoomRequestDto requestDto = CreateGameRoomRequestDto.builder()
                    .roomName("test room name")
                    .gameType(GameRoom.GameType.HOLD_EM.getCode())
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .auth().oauth2(getAuthToken(member).getToken())
                    .body(requestDto)
                    .when().post("/api/v1/game-room")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("id")).isNotZero();
        }

        private Member createMember() {
            return memberRepository.save(Member.builder()
                    .username("username")
                    .password("password")
                    .nickname("nickname")
                    .profile("http://localhost:8080/profile.jpg")
                    .role(Member.Role.MEMBER)
                    .point(0)
                    .build());
        }
    }
}