package com.jsm.boardgame.web.controller.file;

import com.jsm.boardgame.entity.rds.member.Member;
import com.jsm.boardgame.exception.ErrorCodeType;
import com.jsm.boardgame.repository.rds.member.MemberRepository;
import com.jsm.boardgame.web.controller.support.AcceptanceTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class FileControllerTest extends AcceptanceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Value("${file.image-server-host}")
    private String imageHost;

    @Nested
    class uploadProfile {

        @Test
        void 프로필을_업로드_한다() throws IOException {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(ContentType.MULTIPART)
                    .multiPart("profile", "test.png", outputStream.toByteArray())
                    .when().post("/api/v1/file/profile")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.asString()).contains(imageHost);

            outputStream.close();
        }
    }

    @Nested
    class uploadImage {

        @Test
        void 이미지를_업로드_한다() throws IOException {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            Member member = createMember();
            String accessToken = getAuthToken(member).getToken();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(ContentType.MULTIPART)
                    .multiPart("image", "test.png", outputStream.toByteArray())
                    .when().post("/api/v1/file/image")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.asString()).contains(imageHost);

            outputStream.close();
        }

        @Test
        void 이미지가_크기를_넘었다() throws IOException {
            // given
            BufferedImage bufferedImage = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            Member member = createMember();
            String accessToken = getAuthToken(member).getToken();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(ContentType.MULTIPART)
                    .multiPart("image", "test.png", outputStream.toByteArray())
                    .when().post("/api/v1/file/image")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("errorMessage")).isEqualTo(ErrorCodeType.IMAGE_SIZE_LIMIT_EXCEEDED.getMessage());

            outputStream.close();
        }

        @Test
        void 이미지_형식이_아니다() {
            // given
            Member member = createMember();
            String accessToken = getAuthToken(member).getToken();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(ContentType.MULTIPART)
                    .multiPart("image", "test.png", "text".getBytes())
                    .when().post("/api/v1/file/image")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("errorMessage")).isEqualTo(ErrorCodeType.NOT_IMAGE.getMessage());
        }

        private Member createMember() {
            return memberRepository.save(Member.builder()
                    .username("mockUser")
                    .password("mockPassword")
                    .nickname("mockNick")
                    .role(Member.Role.MEMBER)
                    .point(0)
                    .profile("")
                    .build());
        }
    }
}