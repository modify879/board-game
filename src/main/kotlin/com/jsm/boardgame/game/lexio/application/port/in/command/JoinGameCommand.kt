package com.jsm.boardgame.game.lexio.application.port.`in`.command

data class JoinGameCommand(
    val gameId: String,
    val userId: Long
)
