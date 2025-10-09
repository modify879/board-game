package com.jsm.boardgame.user.application.service

import com.jsm.boardgame.common.infrastructure.utils.PasswordUtils
import com.jsm.boardgame.user.application.command.CreateUserCommand
import com.jsm.boardgame.user.domain.model.User
import com.jsm.boardgame.user.domain.repository.UserRepository
import com.jsm.boardgame.user.infrastructure.jpa.entity.Role
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun createUser(createUserCommand: CreateUserCommand) {
        if (createUserCommand.password != createUserCommand.confirmPassword) {
            throw IllegalArgumentException("Password and confirm password must be the same")
        }

        val user = User(
            id = null,
            username = createUserCommand.username,
            password = PasswordUtils.encodePassword(createUserCommand.password),
            nickname = createUserCommand.nickname,
            role = listOf(Role.USER),
            profile = null,
        )

        userRepository.createUser(user)
    }
}