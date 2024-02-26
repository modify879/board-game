package com.jsm.boardgame.service.game;

import com.jsm.boardgame.entity.rds.game.GameRoom;
import com.jsm.boardgame.repository.rds.game.GameRoomRepository;
import com.jsm.boardgame.web.dto.request.game.CreateGameRoomRequestDto;
import com.jsm.boardgame.web.dto.response.game.CreateGameRoomResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final RedisTemplate<String, Object> redisTemplate;

    private HashMap<String, Object> topics;

    @PostConstruct
    private void init() {
        topics = new HashMap<>();
    }

    @Transactional
    public CreateGameRoomResponseDto createGameRoom(CreateGameRoomRequestDto requestDto) {
        GameRoom gameRoom = gameRoomRepository.save(requestDto.toGameRoom());
        return new CreateGameRoomResponseDto(gameRoom.getId());
    }
}
