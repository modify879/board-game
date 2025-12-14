package com.jsm.boardgame.game.lexio.domain.model

enum class GameStatus {
    WAITING, PLAYING, FINISHED
}

data class FieldState(
    val lastHand: LexioHand? = null,
    val lastPlayerId: Long? = null,
    val passCount: Int = 0
) {
    fun clear(): FieldState = FieldState()

    fun update(hand: LexioHand, playerId: Long): FieldState {
        return FieldState(lastHand = hand, lastPlayerId = playerId, passCount = 0)
    }

    fun addPass(): FieldState {
        return copy(passCount = passCount + 1)
    }
}

class LexioGame(
    val gameId: String,
    private val players: MutableList<LexioPlayer>
) {
    var status: GameStatus = GameStatus.WAITING
        private set

    var currentTurnPlayerId: Long? = null
        private set

    var field: FieldState = FieldState()
        private set

    var winnerId: Long? = null
        private set

    companion object {
        private const val MIN_PLAYERS = 3
        private const val MAX_PLAYERS = 5
        private const val CLOUD_SUIT_NUMBER = 3
    }

    fun addPlayer(player: LexioPlayer) {
        if (status != GameStatus.WAITING) throw IllegalStateException("Game already started")
        if (players.size >= MAX_PLAYERS) throw IllegalStateException("Room full")
        players.add(player)
    }

    fun toggleReady(userId: Long) {
        if (status != GameStatus.WAITING) throw IllegalStateException("Game already started")
        val player = getPlayer(userId)
        player.toggleReady()
    }

    fun areAllPlayersReady(): Boolean {
        if (players.size !in MIN_PLAYERS..MAX_PLAYERS) return false
        return players.all { it.isReady }
    }

    fun start() {
        validatePlayerCount()
        shufflePlayers() // 게임 시작 시에만 플레이어 순서 무작위로 섞기
        initializeRound()
    }

    fun startNewRound() {
        if (status != GameStatus.FINISHED) throw IllegalStateException("Can only start new round after finishing")
        validatePlayerCount()
        initializeRound()
    }

    private fun validatePlayerCount() {
        if (players.size !in MIN_PLAYERS..MAX_PLAYERS) {
            throw IllegalStateException("Need $MIN_PLAYERS-$MAX_PLAYERS players")
        }
    }

    private fun shufflePlayers() {
        players.shuffle()
    }

    private fun initializeRound() {
        val deck = createDeck(players.size)
        distributeTiles(deck)
        determineStarter()
        resetRoundState()
    }

    private fun determineStarter() {
        val starter = players.find { player ->
            player.hand.any { it.suit == LexioSuit.CLOUD && it.number == CLOUD_SUIT_NUMBER }
        } ?: throw IllegalStateException("No Cloud $CLOUD_SUIT_NUMBER found? Something wrong with deck")
        currentTurnPlayerId = starter.userId
    }

    private fun resetRoundState() {
        status = GameStatus.PLAYING
        field = FieldState()
        winnerId = null
    }

    fun playTurn(userId: Long, tiles: List<LexioTile>) {
        validateTurn(userId)

        val player = getPlayer(userId)
        if (!player.hasTiles(tiles)) throw IllegalArgumentException("User does not have these tiles")

        // 족보 생성 및 검증
        val newHand = LexioHand.from(tiles)

        // 필드 족보와 비교
        if (field.lastHand != null) {
            // 1. 개수 일치 확인
            if (field.lastHand!!.tiles.size != newHand.tiles.size) {
                throw IllegalArgumentException("Must match tile count")
            }
            // 2. 서열 확인 (newHand > lastHand)
            if (newHand <= field.lastHand!!) {
                throw IllegalArgumentException("Must play a stronger hand")
            }
        } else {
            // 필드가 비어있을 때 (첫 턴 or 전원 패스 후)
            // 첫 턴에 구름 3을 반드시 포함해야 하는 규칙은 PRD에 "첫 턴에 반드시 구름 3을 낼 필요는 없음"이라고 되어 있음.
            // 따라서 별도 제약 없음.
        }

        // 타일 제출 처리
        player.removeTiles(tiles)
        field = field.update(newHand, userId)

        // 승리 체크
        if (player.hand.isEmpty()) {
            winnerId = userId
            endRound()
        } else {
            nextTurn()
        }
    }

    fun passTurn(userId: Long) {
        validateTurn(userId)

        if (field.lastHand == null) {
            throw IllegalArgumentException("Cannot pass on free turn")
        }

        field = field.addPass()

        // 나를 제외한 전원 패스 확인 (플레이어 수 - 1 명이 연속 패스)
        if (field.passCount >= players.size - 1) {
            // 턴을 넘기지 않고, 현재 패스한 사람 다음 사람(즉, 마지막으로 낸 사람)이 턴을 가짐
            // 로직상: A 냄 -> B 패스 -> C 패스 -> (3인) -> A 차례.
            // nextTurn()을 호출하면 B->C->A 순서로 감.
            // 현재: userId가 패스함.
            // 만약 내가 패스해서 passCount가 꽉 찼다면, 
            // 마지막으로 낸 사람(field.lastPlayerId)이 선을 잡아야 함.
            // lastPlayerId로 턴을 강제 변경.
            currentTurnPlayerId = field.lastPlayerId
            field = field.clear() // 선 잡았으니 필드 초기화
        } else {
            nextTurn()
        }
    }

    private fun validateTurn(userId: Long) {
        if (status != GameStatus.PLAYING) throw IllegalStateException("Game not playing")
        if (currentTurnPlayerId != userId) throw IllegalStateException("Not your turn")
    }

    private fun nextTurn() {
        val currentIndex = players.indexOfFirst { it.userId == currentTurnPlayerId }
        val nextIndex = (currentIndex + 1) % players.size
        currentTurnPlayerId = players[nextIndex].userId
    }

    private fun createDeck(playerCount: Int): MutableList<LexioTile> {
        val maxNumber = when (playerCount) {
            3 -> 9
            4 -> 13
            5 -> 15
            else -> 15
        }

        val deck = mutableListOf<LexioTile>()
        for (num in 1..maxNumber) {
            for (suit in LexioSuit.entries) {
                deck.add(LexioTile(num, suit))
            }
        }
        deck.shuffle()
        return deck
    }

    private fun distributeTiles(deck: MutableList<LexioTile>) {
        // 인원별 타일 수
        // 3인: 36개 (12개씩)
        // 4인: 52개 (13개씩)
        // 5인: 60개 (12개씩)

        players.forEach { it.clearHand() }

        val tilesPerPlayer = deck.size / players.size
        var cursor = 0

        for (player in players) {
            val hand = deck.subList(cursor, cursor + tilesPerPlayer)
            player.receiveTiles(hand)
            cursor += tilesPerPlayer
        }
    }

    private fun endRound() {
        status = GameStatus.FINISHED
        calculateScore()
    }

    fun hasPlayerWithZeroOrLessScore(): Boolean {
        return players.any { it.score <= 0 }
    }

    fun resetGame() {
        // 모든 플레이어 초기화 (score, isReady, hand)
        players.forEach { it.reset() }

        // 게임 상태 초기화
        status = GameStatus.WAITING
        currentTurnPlayerId = null
        field = FieldState()
        winnerId = null
    }

    private fun calculateScore() {
        val winner = getPlayer(winnerId!!)
        val losers = players.filter { it.userId != winnerId }

        calculateWinnerLoserSettlement(winner, losers)
        calculateLosersMutualSettlement(losers)
    }

    private fun calculateWinnerLoserSettlement(winner: LexioPlayer, losers: List<LexioPlayer>) {
        losers.forEach { loser ->
            val tileDiff = loser.hand.size
            val multiplier = calculateTwoPenaltyMultiplier(loser.hand)
            val pointToPay = tileDiff * multiplier

            loser.score -= pointToPay
            winner.score += pointToPay
        }
    }

    private fun calculateLosersMutualSettlement(losers: List<LexioPlayer>) {
        for (i in losers.indices) {
            for (j in i + 1 until losers.size) {
                val p1 = losers[i]
                val p2 = losers[j]

                if (p1.hand.size == p2.hand.size) continue

                val (winnerL, loserL) = if (p1.hand.size < p2.hand.size) p1 to p2 else p2 to p1
                val diff = loserL.hand.size - winnerL.hand.size
                val multiplier = calculateTwoPenaltyMultiplier(loserL.hand)
                val amount = diff * multiplier

                loserL.score -= amount
                winnerL.score += amount
            }
        }
    }

    private fun calculateTwoPenaltyMultiplier(hand: List<LexioTile>): Int {
        val twoCount = hand.count { it.number == 2 }
        return if (twoCount > 0) 1 shl twoCount else 1
    }

    private fun getPlayer(userId: Long): LexioPlayer {
        return players.find { it.userId == userId } ?: throw IllegalArgumentException("User not in game")
    }

    fun getPlayers(): List<LexioPlayer> = players.toList()
}
