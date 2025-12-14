package com.jsm.boardgame.game.lexio.presentation.dto.response

import com.jsm.boardgame.game.lexio.domain.model.LexioPlayer

data class PlayerDto(
    val userId: Long,
    val name: String,
    val score: Int,
    val isReady: Boolean,
    val handCount: Int,
    val hand: List<LexioTileDto>? = null,
    val isCurrentTurn: Boolean = false
) {
    companion object {
        /**
         * @param viewerUserId 자신의 패를 볼 수 있는 플레이어의 userId. null이면 자신의 패를 포함하지 않음.
         * @param currentTurnPlayerId 현재 차례인 플레이어의 userId. null이면 아무도 차례가 아님.
         */
        fun from(user: LexioPlayer, viewerUserId: Long? = null, currentTurnPlayerId: Long? = null): PlayerDto {
            val isViewer = viewerUserId == user.userId
            val isCurrentTurn = currentTurnPlayerId == user.userId
            return PlayerDto(
                userId = user.userId,
                name = user.name,
                score = user.score,
                isReady = user.isReady,
                handCount = user.hand.size,
                hand = if (isViewer) user.hand.map { LexioTileDto.from(it) } else null,
                isCurrentTurn = isCurrentTurn
            )
        }
    }
}
