package com.jsm.boardgame.auth.domain.port.out.dto

data class UserAuthenticationInfo(
    val id: Long,
    val password: String,
    val userRoles: List<String>,
)
