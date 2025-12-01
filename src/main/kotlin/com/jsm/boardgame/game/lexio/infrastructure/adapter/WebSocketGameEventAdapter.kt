package com.jsm.boardgame.game.lexio.infrastructure.adapter

import com.jsm.boardgame.game.lexio.application.port.out.GameEventPort
import com.jsm.boardgame.game.lexio.domain.model.LexioGame
import com.jsm.boardgame.game.lexio.presentation.dto.response.GameResponse
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class WebSocketGameEventAdapter(
    private val messagingTemplate: SimpMessagingTemplate
) : GameEventPort {

    override fun broadcastGameUpdated(game: LexioGame) {
        // 각 플레이어에게 개인화된 메시지 전송
        // 자신의 패는 전체 정보로, 다른 플레이어의 패는 개수만 전송
        game.getPlayers().forEach { player ->
            val response = GameResponse.from(game, player.userId)
            // 각 플레이어에게 개별 메시지 전송
            // Spring WebSocket의 /user/{userId}/queue/... 경로 사용
            messagingTemplate.convertAndSendToUser(
                player.userId.toString(),
                "/queue/game/${game.gameId}",
                response
            )
        }
    }
}
