package com.jsm.boardgame.auth.domain.port.out

import com.jsm.boardgame.auth.domain.port.out.dto.UserAuthenticationInfo

interface UserAuthenticationPort {

    fun getUserByUsername(username: String): UserAuthenticationInfo?
}