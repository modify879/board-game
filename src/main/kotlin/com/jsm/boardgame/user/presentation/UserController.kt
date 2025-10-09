package com.jsm.boardgame.user.presentation

import com.jsm.boardgame.user.application.command.CreateUserCommand
import com.jsm.boardgame.user.application.service.UserService
import com.jsm.boardgame.user.presentation.dto.request.CreateUserRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest) {
        userService.createUser(
            CreateUserCommand(
                username = request.username,
                password = request.password,
                confirmPassword = request.confirmPassword,
                nickname = request.nickname,
            )
        )
    }
}