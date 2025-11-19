package com.jsm.boardgame.user.domain.model

@JvmInline
value class Username(private val value: String) {

    init {
        require(value.isNotBlank()) { "username must not be blank" }
        require(value.length in 3..30) { "username must be 3~30 characters" }
        require(VALID_PATTERN.matches(value)) { "username allows lowercase letters, numbers, '-', '_' only" }
    }

    fun value(): String = value

    override fun toString(): String = value

    companion object {
        private val VALID_PATTERN = "^[a-z0-9-_]+$".toRegex()

        fun of(value: String) = Username(value)
    }
}

