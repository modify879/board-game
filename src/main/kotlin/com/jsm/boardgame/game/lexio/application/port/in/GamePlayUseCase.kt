package com.jsm.boardgame.game.lexio.application.port.`in`

import com.jsm.boardgame.game.lexio.application.port.`in`.command.GameActionCommand
import com.jsm.boardgame.game.lexio.application.port.`in`.command.PlayTurnCommand
import com.jsm.boardgame.game.lexio.domain.model.LexioGame

interface GamePlayUseCase {
    fun playTurn(command: PlayTurnCommand): LexioGame
    fun passTurn(command: GameActionCommand): LexioGame
}
