package com.jsm.boardgame.game.lexio.presentation.rest

import com.jsm.boardgame.auth.presentation.annotation.LoginUserId
import com.jsm.boardgame.game.lexio.application.port.`in`.GameManagementUseCase
import com.jsm.boardgame.game.lexio.application.port.`in`.command.CreateGameCommand
import com.jsm.boardgame.game.lexio.application.port.`in`.command.GameActionCommand
import com.jsm.boardgame.game.lexio.application.port.`in`.command.JoinGameCommand
import com.jsm.boardgame.game.lexio.presentation.dto.response.GameResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/lexio/games")
class LexioGameRestController(
    private val gameManagementUseCase: GameManagementUseCase
) {

    /**
     * 게임 방 생성
     * 호스트(방장)가 새로운 게임 방을 만듭니다.
     */
    @PostMapping
    fun createGame(@LoginUserId hostId: Long): GameResponse {
        val command = CreateGameCommand(hostId)
        val game = gameManagementUseCase.createGame(command)
        return GameResponse.from(game)
    }

    /**
     * 게임 방 참여
     * 다른 플레이어가 생성된 게임 방에 입장합니다.
     * 입장 후, 변경된 게임 상태를 구독자들에게 브로드캐스트합니다.
     */
    @PostMapping("/{gameId}/join")
    fun joinGame(
        @PathVariable gameId: String,
        @LoginUserId userId: Long
    ): GameResponse {
        val command = JoinGameCommand(gameId, userId)
        val game = gameManagementUseCase.joinGame(command)
        // broadcastUpdate는 Service 레이어에서 처리됨
        return GameResponse.from(game)
    }

    /**
     * 게임 수동 시작 (현재는 자동 시작 로직이 있어 잘 사용되지 않을 수 있음)
     * 호스트가 강제로 게임을 시작할 때 사용합니다.
     */
    @PostMapping("/{gameId}/start")
    fun startGame(
        @PathVariable gameId: String,
        @LoginUserId userId: Long
    ): GameResponse {
        val command = GameActionCommand(gameId, userId)
        val game = gameManagementUseCase.startGame(command)
        // broadcastUpdate는 Service 레이어에서 처리됨
        return GameResponse.from(game)
    }

    /**
     * 게임 상태 조회
     * 현재 게임의 전체 상태(플레이어, 필드, 점수 등)를 반환합니다.
     */
    @GetMapping("/{gameId}")
    fun getGame(@PathVariable gameId: String): GameResponse {
        val game = gameManagementUseCase.getGame(gameId)
        return GameResponse.from(game)
    }
}
