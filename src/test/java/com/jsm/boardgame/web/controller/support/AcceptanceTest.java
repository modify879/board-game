package com.jsm.boardgame.web.controller.support;

import com.jsm.boardgame.common.jwt.AuthToken;
import com.jsm.boardgame.common.jwt.AuthTokenProvider;
import com.jsm.boardgame.entity.rds.member.Member;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class AcceptanceTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AuthTokenProvider authTokenProvider;

    static final DockerComposeContainer DOCKER_COMPOSE_CONTAINER;

    static {
        DOCKER_COMPOSE_CONTAINER = new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
                .withExposedService("postgres_1", 5432, Wait.forListeningPort())
                .withExposedService("redis_1", 6379, Wait.forListeningPort());
        DOCKER_COMPOSE_CONTAINER.start();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    protected AuthToken getAuthToken(Member member) {
        return authTokenProvider.createAuthToken(member);
    }
}
