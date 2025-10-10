package com.jsm.boardgame.user.application.service

import com.jsm.boardgame.user.domain.model.User
import com.jsm.boardgame.user.domain.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserQueryServiceImpl(
    private val userRepository: UserRepository
) : UserQueryService {

    override fun getUserByUsername(username: String): User? = userRepository.findByUsername(username)
}