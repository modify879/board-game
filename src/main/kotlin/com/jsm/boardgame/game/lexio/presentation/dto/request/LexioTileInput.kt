package com.jsm.boardgame.game.lexio.presentation.dto.request

import com.jsm.boardgame.game.lexio.domain.model.LexioSuit
import com.jsm.boardgame.game.lexio.domain.model.LexioTile

data class LexioTileInput(
    val number: Int,
    val suit: String
) {
    fun toDomain(): LexioTile {
        return LexioTile(number, LexioSuit.valueOf(suit))
    }
}


