package com.jsm.boardgame.game.lexio.infrastructure.repository

import com.jsm.boardgame.game.lexio.domain.model.LexioGame
import com.jsm.boardgame.game.lexio.domain.repository.LexioGameRepository
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class LexioGameRepositoryImpl : LexioGameRepository {
    private val store = ConcurrentHashMap<String, LexioGame>()

    override fun save(game: LexioGame): LexioGame {
        store[game.gameId] = game
        return game
    }

    override fun findById(gameId: String): LexioGame? {
        return store[gameId]
    }

    override fun delete(gameId: String) {
        store.remove(gameId)
    }
}

