package com.jsm.boardgame.game.lexio.domain.model

data class LexioTile(
    val number: Int,
    val suit: LexioSuit
) : Comparable<LexioTile> {

    // 숫자의 힘: 2 > 1 > 15 > 14 ... > 3
    val power: Int
        get() = when (number) {
            2 -> 150
            1 -> 140
            else -> number
        }

    override fun compareTo(other: LexioTile): Int {
        if (this.power != other.power) {
            return this.power - other.power
        }
        return this.suit.rank - other.suit.rank
    }

    override fun toString(): String {
        return "${suit.name}($number)"
    }
}
