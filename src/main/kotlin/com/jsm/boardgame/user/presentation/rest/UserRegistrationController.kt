package com.jsm.boardgame.user.presentation.rest

import com.jsm.boardgame.user.application.port.`in`.RegisterUserUseCase
import com.jsm.boardgame.user.presentation.dto.request.RegisterUserRequest
import com.jsm.boardgame.user.presentation.dto.response.UserResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserRegistrationController(
    private val registerUserUseCase: RegisterUserUseCase
) {

    @PostMapping
    fun register(@Valid @RequestBody request: RegisterUserRequest): UserResponse {
        val result = registerUserUseCase.register(request.toCommand())
        return UserResponse.from(result)
    }
}
