package com.jsm.boardgame.user.domain.repository

import com.jsm.boardgame.user.domain.model.User
import com.jsm.boardgame.user.domain.model.UserId

interface UserRepository {
    fun save(user: User): User
    fun existsByUsername(username: String): Boolean
    fun findById(id: UserId): User?
    fun findByUsername(username: String): User?
}
