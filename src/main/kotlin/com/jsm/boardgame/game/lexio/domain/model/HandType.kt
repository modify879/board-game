package com.jsm.boardgame.game.lexio.domain.model

enum class HandType(val rank: Int, val count: Int) {
    SINGLE(1, 1),
    PAIR(2, 2),
    TRIPLE(3, 3),
    STRAIGHT(4, 5),
    FLUSH(5, 5),
    FULL_HOUSE(6, 5),
    FOUR_OF_A_KIND(7, 5),
    STRAIGHT_FLUSH(8, 5);
}

