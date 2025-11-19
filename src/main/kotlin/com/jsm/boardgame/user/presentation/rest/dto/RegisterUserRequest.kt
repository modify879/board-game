package com.jsm.boardgame.user.presentation.rest.dto

import com.jsm.boardgame.user.application.port.`in`.RegisterUserCommand
import com.jsm.boardgame.user.domain.model.UserRole
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterUserRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 30)
    val username: String,

    @field:NotBlank
    @field:Size(min = 8, max = 60)
    val password: String,

    @field:NotBlank
    @field:Size(min = 8, max = 60)
    val passwordConfirm: String,

    val role: UserRole = UserRole.USER,

    @field:NotBlank
    @field:Size(max = 30)
    val nickname: String,
) {

    fun toCommand(): RegisterUserCommand = RegisterUserCommand(
        username = username,
        rawPassword = password,
        confirmPassword = passwordConfirm,
        role = role,
        nickname = nickname,
    )
}

