package com.jsm.boardgame.game.lexio.domain.model

enum class LexioSuit(val rank: Int) {
    SUN(4),    // 해 (최강)
    MOON(3),   // 달
    STAR(2),   // 별
    CLOUD(1);  // 구름 (최약)
}
