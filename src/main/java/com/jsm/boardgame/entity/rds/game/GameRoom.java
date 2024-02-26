package com.jsm.boardgame.entity.rds.game;

import com.jsm.boardgame.common.enums.AbstractEnumCodeAttributeConverter;
import com.jsm.boardgame.common.enums.EnumCodeType;
import com.jsm.boardgame.entity.rds.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "game_room")
public class GameRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_name", nullable = false, length = 25)
    private String roomName;

    @Convert(converter = GameTypeConverter.class)
    @Column(name = "game_type", nullable = false)
    private GameType gameType;

    @Builder
    public GameRoom(String roomName, GameType gameType) {
        this.roomName = roomName;
        this.gameType = gameType;
    }

    @RequiredArgsConstructor
    @Getter
    public enum GameType implements EnumCodeType {

        HOLD_EM("홀덤", "1"),
        SEOTDA("섯다", "2");

        private final String desc;
        private final String code;
    }

    private static class GameTypeConverter extends AbstractEnumCodeAttributeConverter<GameType> {

        private static final String ENUM_NAME = "게임 종류";

        public GameTypeConverter() {
            super(false, ENUM_NAME);
        }
    }
}
