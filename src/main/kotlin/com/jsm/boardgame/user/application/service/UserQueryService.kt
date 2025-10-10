package com.jsm.boardgame.user.application.service

import com.jsm.boardgame.user.domain.model.User

interface UserQueryService {

    fun getUserByUsername(username: String): User?
}