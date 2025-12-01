package com.jsm.boardgame.game.lexio.application.port.`in`

import com.jsm.boardgame.game.lexio.application.port.`in`.command.CreateGameCommand
import com.jsm.boardgame.game.lexio.application.port.`in`.command.GameActionCommand
import com.jsm.boardgame.game.lexio.application.port.`in`.command.JoinGameCommand
import com.jsm.boardgame.game.lexio.domain.model.LexioGame

interface GameManagementUseCase {
    fun createGame(command: CreateGameCommand): LexioGame
    fun joinGame(command: JoinGameCommand): LexioGame
    fun toggleReady(command: GameActionCommand): Boolean // returns isAllReady
    fun startGame(command: GameActionCommand): LexioGame
    fun getGame(gameId: String): LexioGame
}
