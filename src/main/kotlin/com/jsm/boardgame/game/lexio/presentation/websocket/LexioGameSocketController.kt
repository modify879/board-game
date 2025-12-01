package com.jsm.boardgame.game.lexio.presentation.websocket

import com.jsm.boardgame.auth.presentation.annotation.LoginUserId
import com.jsm.boardgame.game.lexio.application.port.`in`.GameManagementUseCase
import com.jsm.boardgame.game.lexio.application.port.`in`.GamePlayUseCase
import com.jsm.boardgame.game.lexio.application.port.`in`.command.GameActionCommand
import com.jsm.boardgame.game.lexio.application.port.`in`.command.PlayTurnCommand
import com.jsm.boardgame.game.lexio.presentation.dto.request.PlayTurnRequest
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller

@Controller
class LexioGameSocketController(
    private val gamePlayUseCase: GamePlayUseCase,
    private val gameManagementUseCase: GameManagementUseCase
) {

    /**
     * [WebSocket] 턴 플레이 (타일 제출)
     * 플레이어가 자신의 턴에 타일(족보)을 냅니다.
     * STOMP 경로: /app/game/{gameId}/play
     */
    @MessageMapping("/game/{gameId}/play")
    fun playTurn(
        @DestinationVariable gameId: String,
        @Payload request: PlayTurnRequest,
        @LoginUserId userId: Long
    ) {
        try {
            val tiles = request.tiles.map { it.toDomain() }
            val command = PlayTurnCommand(gameId, userId, tiles)
            // Service 내부에서 broadcastUpdate 자동 호출됨
            gamePlayUseCase.playTurn(command)
        } catch (e: Exception) {
            // 에러 처리: 실제로는 에러 메시지를 해당 유저에게 전송해야 함 (/user/queue/errors 등)
            e.printStackTrace()
        }
    }

    /**
     * [WebSocket] 턴 패스
     * 플레이어가 낼 타일이 없거나 전략적으로 턴을 넘깁니다.
     * STOMP 경로: /app/game/{gameId}/pass
     */
    @MessageMapping("/game/{gameId}/pass")
    fun passTurn(
        @DestinationVariable gameId: String,
        @LoginUserId userId: Long
    ) {
        try {
            val command = GameActionCommand(gameId, userId)
            // Service 내부에서 broadcastUpdate 자동 호출됨
            gamePlayUseCase.passTurn(command)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * [WebSocket] 준비 상태 토글 (Ready/Unready)
     * 대기 방에서 플레이어가 준비 완료 상태를 변경합니다.
     * 모든 플레이어가 준비되면 3초 후 게임이 자동 시작됩니다 (Service 로직).
     * STOMP 경로: /app/game/{gameId}/ready
     */
    @MessageMapping("/game/{gameId}/ready")
    fun toggleReady(
        @DestinationVariable gameId: String,
        @LoginUserId userId: Long
    ) {
        try {
            val command = GameActionCommand(gameId, userId)
            // 준비 상태 토글 (Service 내부에서 broadcastUpdate 및 3초 후 자동 시작 처리)
            gameManagementUseCase.toggleReady(command)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
