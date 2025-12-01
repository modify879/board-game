package com.jsm.boardgame.game.lexio.presentation.dto.response

import com.jsm.boardgame.game.lexio.domain.model.LexioTile

data class LexioTileDto(
    val number: Int,
    val suit: String
) {
    companion object {
        fun from(tile: LexioTile): LexioTileDto {
            return LexioTileDto(tile.number, tile.suit.name)
        }
    }
}


