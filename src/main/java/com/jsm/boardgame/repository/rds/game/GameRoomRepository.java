package com.jsm.boardgame.repository.rds.game;

import com.jsm.boardgame.entity.rds.game.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
}
