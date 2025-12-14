package com.jsm.boardgame.game.lexio.presentation.dto.response

import com.jsm.boardgame.game.lexio.domain.model.FieldState

data class FieldStateDto(
    val lastHand: List<LexioTileDto>?,
    val lastHandType: String?,
    val lastPlayerId: Long?,
    val passCount: Int
) {
    companion object {
        fun from(field: FieldState): FieldStateDto {
            return FieldStateDto(
                lastHand = field.lastHand?.tiles?.map { LexioTileDto.from(it) },
                lastHandType = field.lastHand?.type?.name,
                lastPlayerId = field.lastPlayerId,
                passCount = field.passCount
            )
        }
    }
}


