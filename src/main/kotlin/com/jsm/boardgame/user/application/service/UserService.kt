package com.jsm.boardgame.user.application.service

import com.jsm.boardgame.common.utils.PasswordUtils
import com.jsm.boardgame.user.application.dto.request.CreateUserRequest
import com.jsm.boardgame.user.domain.model.User
import com.jsm.boardgame.user.domain.model.UserRole
import com.jsm.boardgame.user.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun createUser(createUserRequest: CreateUserRequest) {
        if (createUserRequest.password != createUserRequest.confirmPassword) {
            throw IllegalArgumentException("Password and confirm password must be the same")
        }

        val user = User(
            id = null,
            username = createUserRequest.username,
            password = PasswordUtils.encodePassword(createUserRequest.password),
            nickname = createUserRequest.nickname,
            userRole = listOf(UserRole.USER),
            profile = null,
        )

        userRepository.createUser(user)
    }
}