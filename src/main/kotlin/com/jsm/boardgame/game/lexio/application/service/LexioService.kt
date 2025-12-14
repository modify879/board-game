package com.jsm.boardgame.game.lexio.application.service

import com.jsm.boardgame.game.lexio.application.port.`in`.GameManagementUseCase
import com.jsm.boardgame.game.lexio.application.port.`in`.GamePlayUseCase
import com.jsm.boardgame.game.lexio.application.port.`in`.command.CreateGameCommand
import com.jsm.boardgame.game.lexio.application.port.`in`.command.GameActionCommand
import com.jsm.boardgame.game.lexio.application.port.`in`.command.JoinGameCommand
import com.jsm.boardgame.game.lexio.application.port.`in`.command.PlayTurnCommand
import com.jsm.boardgame.game.lexio.application.port.out.GameEventPort
import com.jsm.boardgame.game.lexio.application.port.out.UserInfoPort
import com.jsm.boardgame.game.lexio.domain.model.GameStatus
import com.jsm.boardgame.game.lexio.domain.model.LexioGame
import com.jsm.boardgame.game.lexio.domain.model.LexioPlayer
import com.jsm.boardgame.game.lexio.domain.repository.LexioGameRepository
import kotlinx.coroutines.*
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class LexioService(
    private val lexioGameRepository: LexioGameRepository,
    private val gameEventPort: GameEventPort,
    private val userInfoPort: UserInfoPort
) : GameManagementUseCase, GamePlayUseCase {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val gameStartJobs = ConcurrentHashMap<String, Job>()
    private val roundRestartJobs = ConcurrentHashMap<String, Job>()

    companion object {
        private const val GAME_START_DELAY_MS = 3000L
        private const val ROUND_RESTART_DELAY_MS = 3000L
    }

    private fun findGameOrThrow(gameId: String): LexioGame {
        return lexioGameRepository.findById(gameId)
            ?: throw IllegalArgumentException("Game not found: $gameId")
    }

    override fun createGame(command: CreateGameCommand): LexioGame {
        val userInfo = userInfoPort.getUserInfo(command.hostId)
        val gameId = UUID.randomUUID().toString()
        val host = LexioPlayer(userInfo.userId, userInfo.nickname)
        val game = LexioGame(gameId, mutableListOf(host))
        val savedGame = lexioGameRepository.save(game)
        broadcastUpdate(savedGame)
        return savedGame
    }

    override fun joinGame(command: JoinGameCommand): LexioGame {
        val game = findGameOrThrow(command.gameId)

        synchronized(game) {
            // 이미 참여 중인지 확인
            if (game.getPlayers().none { it.userId == command.userId }) {
                val userInfo = userInfoPort.getUserInfo(command.userId)
                game.addPlayer(LexioPlayer(userInfo.userId, userInfo.nickname))
            }
        }
        broadcastUpdate(game)
        return game
    }

    override fun toggleReady(command: GameActionCommand): Boolean {
        val game = findGameOrThrow(command.gameId)

        synchronized(game) {
            game.toggleReady(command.userId)

            cancelScheduledGameStart(command.gameId)

            val allReady = game.areAllPlayersReady()
            if (allReady) {
                scheduleGameStart(command)
            }
            broadcastUpdate(game)
            return allReady
        }
    }

    private fun cancelScheduledGameStart(gameId: String) {
        gameStartJobs[gameId]?.cancel()
        gameStartJobs.remove(gameId)
    }

    private fun scheduleGameStart(command: GameActionCommand) {
        val job = scope.launch {
            delay(GAME_START_DELAY_MS)
            try {
                startGame(command)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        gameStartJobs[command.gameId] = job
    }

    override fun startGame(command: GameActionCommand): LexioGame {
        val game = findGameOrThrow(command.gameId)

        synchronized(game) {
            gameStartJobs.remove(command.gameId)

            if (game.areAllPlayersReady()) {
                game.start()
                broadcastUpdate(game)
            }
        }
        return game
    }

    private fun broadcastUpdate(game: LexioGame) {
        gameEventPort.broadcastGameUpdated(game)
    }

    override fun playTurn(command: PlayTurnCommand): LexioGame {
        val game = findGameOrThrow(command.gameId)

        synchronized(game) {
            val previousStatus = game.status
            game.playTurn(command.userId, command.tiles)
            broadcastUpdate(game)

            if (isRoundFinished(previousStatus, game.status)) {
                handleRoundEnd(game)
            }
        }
        return game
    }

    private fun isRoundFinished(previousStatus: GameStatus, currentStatus: GameStatus): Boolean {
        return previousStatus == GameStatus.PLAYING && currentStatus == GameStatus.FINISHED
    }

    private fun handleRoundEnd(game: LexioGame) {
        cancelScheduledRoundRestart(game.gameId)

        if (game.hasPlayerWithZeroOrLessScore()) {
            game.resetGame()
            broadcastUpdate(game)
        } else {
            scheduleRoundRestart(game)
        }
    }

    private fun cancelScheduledRoundRestart(gameId: String) {
        roundRestartJobs[gameId]?.cancel()
        roundRestartJobs.remove(gameId)
    }

    private fun scheduleRoundRestart(game: LexioGame) {
        val job = scope.launch {
            delay(ROUND_RESTART_DELAY_MS)
            try {
                synchronized(game) {
                    if (game.status == GameStatus.FINISHED && !game.hasPlayerWithZeroOrLessScore()) {
                        game.startNewRound()
                        broadcastUpdate(game)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                roundRestartJobs.remove(game.gameId)
            }
        }
        roundRestartJobs[game.gameId] = job
    }

    override fun passTurn(command: GameActionCommand): LexioGame {
        val game = findGameOrThrow(command.gameId)

        synchronized(game) {
            game.passTurn(command.userId)
            broadcastUpdate(game)
        }
        return game
    }

    override fun getGame(gameId: String): LexioGame {
        return findGameOrThrow(gameId)
    }
}
