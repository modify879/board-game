package com.jsm.boardgame.auth.application.port.`in`.command

data class LoginCommand(
    val username: String,
    val password: String,
)
