package com.jsm.boardgame.game.lexio.domain.repository

import com.jsm.boardgame.game.lexio.domain.model.LexioGame

interface LexioGameRepository {
    fun save(game: LexioGame): LexioGame
    fun findById(gameId: String): LexioGame?
    fun delete(gameId: String)
}

