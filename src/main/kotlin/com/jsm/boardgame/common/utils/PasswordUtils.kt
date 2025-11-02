package com.jsm.boardgame.common.utils

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

object PasswordUtils {

    private val encoder: PasswordEncoder = BCryptPasswordEncoder()

    fun encodePassword(rawPassword: String): String = encoder.encode(rawPassword)

    fun matchesPassword(rawPassword: String, encodedPassword: String): Boolean =
        encoder.matches(rawPassword, encodedPassword)
}