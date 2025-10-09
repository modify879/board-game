package com.jsm.boardgame.user.domain.repository

import com.jsm.boardgame.user.domain.model.User

interface UserRepository {

    fun createUser(user: User): User
}