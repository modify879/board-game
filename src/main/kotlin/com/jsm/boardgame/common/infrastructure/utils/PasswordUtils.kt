package com.jsm.boardgame.common.infrastructure.utils

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

object PasswordUtils {

    private val encoder: PasswordEncoder = BCryptPasswordEncoder()

    fun encodePassword(rawPassword: String): String = encoder.encode(rawPassword)
}