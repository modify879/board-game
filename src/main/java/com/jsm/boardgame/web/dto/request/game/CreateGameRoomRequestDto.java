package com.jsm.boardgame.web.dto.request.game;

import com.jsm.boardgame.entity.rds.game.GameRoom;
import com.jsm.boardgame.utils.EnumCodeConverterUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreateGameRoomRequestDto {

    @NotNull(message = "방 이름을 입력하세요.")
    @NotBlank(message = "방 이름을 입력하세요.")
    @Size(min = 1, max = 25, message = "방 이름은 {min}~{max}자 이내로 입력하세요.")
    private String roomName;

    @NotNull(message = "게임 종류를 입력하세요.")
    @NotBlank(message = "게임 종류를 입력하세요.")
    private String gameType;

    @Builder
    public CreateGameRoomRequestDto(String roomName, String gameType) {
        this.roomName = roomName;
        this.gameType = gameType;
    }

    public GameRoom toGameRoom() {
        return GameRoom.builder()
                .roomName(roomName)
                .gameType(EnumCodeConverterUtils.ofCode(gameType, GameRoom.GameType.class))
                .build();
    }
}
