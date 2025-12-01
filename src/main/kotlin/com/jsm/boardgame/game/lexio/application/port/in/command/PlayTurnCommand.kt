package com.jsm.boardgame.game.lexio.application.port.`in`.command

import com.jsm.boardgame.game.lexio.domain.model.LexioTile

data class PlayTurnCommand(
    val gameId: String,
    val userId: Long,
    val tiles: List<LexioTile>
)
