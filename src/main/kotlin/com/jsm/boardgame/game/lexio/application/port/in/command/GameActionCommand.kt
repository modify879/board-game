package com.jsm.boardgame.game.lexio.application.port.`in`.command

data class GameActionCommand(
    val gameId: String,
    val userId: Long
)
