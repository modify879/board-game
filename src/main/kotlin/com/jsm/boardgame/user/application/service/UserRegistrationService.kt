package com.jsm.boardgame.user.application.service

import com.jsm.boardgame.user.application.port.`in`.RegisterUserCommand
import com.jsm.boardgame.user.application.port.`in`.RegisterUserUseCase
import com.jsm.boardgame.user.application.port.out.RegisteredUserResult
import com.jsm.boardgame.user.domain.exception.PasswordMismatchException
import com.jsm.boardgame.user.domain.exception.UserAlreadyExistsException
import com.jsm.boardgame.user.domain.model.EncodedPassword
import com.jsm.boardgame.user.domain.model.User
import com.jsm.boardgame.user.domain.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserRegistrationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : RegisterUserUseCase {

    @Transactional
    override fun register(command: RegisterUserCommand): RegisteredUserResult {
        if (userRepository.existsByUsername(command.username)) {
            throw UserAlreadyExistsException(command.username)
        }
        if (command.rawPassword != command.confirmPassword) {
            throw PasswordMismatchException()
        }

        val encodedPassword = EncodedPassword.from(passwordEncoder.encode(command.rawPassword))
        val user = User.create(
            username = command.username,
            nickname = command.nickname,
            encodedPassword = encodedPassword,
            role = command.role,
        )

        val saved = userRepository.save(user)
        return RegisteredUserResult.from(saved)
    }
}

