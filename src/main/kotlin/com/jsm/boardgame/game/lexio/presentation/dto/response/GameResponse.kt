package com.jsm.boardgame.game.lexio.presentation.dto.response

import com.jsm.boardgame.game.lexio.domain.model.GameStatus
import com.jsm.boardgame.game.lexio.domain.model.LexioGame

data class GameResponse(
    val gameId: String,
    val status: GameStatus,
    val players: List<PlayerDto>,
    val currentTurnPlayerId: Long?,
    val field: FieldStateDto,
    val winnerId: Long? = null
) {
    companion object {
        /**
         * @param viewerUserId 자신의 패를 볼 수 있는 플레이어의 userId. null이면 모든 플레이어의 패를 숨김.
         */
        fun from(game: LexioGame, viewerUserId: Long? = null): GameResponse {
            return GameResponse(
                gameId = game.gameId,
                status = game.status,
                players = game.getPlayers().map { PlayerDto.from(it, viewerUserId, game.currentTurnPlayerId) },
                currentTurnPlayerId = game.currentTurnPlayerId,
                field = FieldStateDto.from(game.field),
                winnerId = game.winnerId
            )
        }
    }
}

