package com.jsm.boardgame.game.lexio.domain.model

class LexioPlayer(
    val userId: Long,
    val name: String,
    var score: Int = INITIAL_SCORE
) {
    companion object {
        const val INITIAL_SCORE = 64
    }

    var isReady: Boolean = false
        private set

    private val _hand = mutableListOf<LexioTile>()
    val hand: List<LexioTile> get() = _hand.toList()

    fun toggleReady() {
        isReady = !isReady
    }

    fun reset() {
        score = INITIAL_SCORE
        isReady = false
        clearHand()
    }

    fun receiveTiles(tiles: List<LexioTile>) {
        _hand.addAll(tiles)
        _hand.sort() // 항상 정렬 상태 유지 (편의성)
    }

    fun removeTiles(tiles: List<LexioTile>) {
        _hand.removeAll(tiles)
    }

    fun hasTiles(tiles: List<LexioTile>): Boolean {
        // 단순 containsAll은 중복 타일 처리에 약할 수 있으나, 렉시오는 중복 타일이 없음.
        return _hand.containsAll(tiles)
    }

    fun clearHand() {
        _hand.clear()
    }
}
