package com.jsm.boardgame.user.domain.model

import com.jsm.boardgame.user.infrastructure.jpa.entity.Role

data class User(
    val id: Long?,
    val username: String,
    val password: String,
    val nickname: String,
    val role: List<Role>,
    val profile: String?,
)