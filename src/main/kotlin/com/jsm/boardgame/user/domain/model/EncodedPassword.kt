package com.jsm.boardgame.user.domain.model

@JvmInline
value class EncodedPassword private constructor(private val value: String) {

    fun value(): String = value

    companion object {
        fun from(value: String) = EncodedPassword(value)
    }
}
