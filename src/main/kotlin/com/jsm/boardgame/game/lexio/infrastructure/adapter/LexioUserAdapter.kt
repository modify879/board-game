package com.jsm.boardgame.game.lexio.infrastructure.adapter

import com.jsm.boardgame.game.lexio.application.port.out.UserInfoPort
import com.jsm.boardgame.game.lexio.application.port.out.UserQueryResult
import com.jsm.boardgame.user.application.service.UserQueryService
import org.springframework.stereotype.Component

@Component
class LexioUserAdapter(
    private val userQueryService: UserQueryService
) : UserInfoPort {

    override fun getUserInfo(userId: Long): UserQueryResult {
        val user = userQueryService.findById(userId) ?: throw IllegalArgumentException("User not found: $userId")

        return UserQueryResult(
            userId = user.id,
            nickname = user.nickname,
            profile = user.profile,
        )
    }
}
