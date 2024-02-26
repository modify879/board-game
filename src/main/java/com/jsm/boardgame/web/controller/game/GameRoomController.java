package com.jsm.boardgame.web.controller.game;

import com.jsm.boardgame.service.game.GameRoomService;
import com.jsm.boardgame.web.dto.request.game.CreateGameRoomRequestDto;
import com.jsm.boardgame.web.dto.response.game.CreateGameRoomResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/game-room")
public class GameRoomController {

    private final GameRoomService gameRoomService;

    @PostMapping
    public ResponseEntity<CreateGameRoomResponseDto> createGameRoom(@Valid @RequestBody CreateGameRoomRequestDto requestDto) {
        CreateGameRoomResponseDto responseDto = gameRoomService.createGameRoom(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
