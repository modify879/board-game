package com.jsm.boardgame.auth.domain.model

/**
 * auth 컨텍스트 내부에서 사용하는 UserId
 * user 컨텍스트의 UserId와는 독립적
 */
@JvmInline
value class AuthUserId(val value: Long) {

    override fun toString(): String = value.toString()

    companion object {
        fun of(value: Long): AuthUserId = AuthUserId(value)
    }
}
