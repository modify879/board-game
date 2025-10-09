package com.jsm.boardgame.user.application.command

data class CreateUserCommand(
    val username: String,
    val password: String,
    val confirmPassword: String,
    val nickname: String,
)
