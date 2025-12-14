package com.jsm.boardgame.user.domain.model

@JvmInline
value class UserId(val value: Long) {

    override fun toString(): String = value.toString()

    companion object {
        fun of(value: Long): UserId = UserId(value)
    }
}
