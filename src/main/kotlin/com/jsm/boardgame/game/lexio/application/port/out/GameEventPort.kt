package com.jsm.boardgame.game.lexio.application.port.out

import com.jsm.boardgame.game.lexio.domain.model.LexioGame

interface GameEventPort {
    /**
     * 게임 상태 업데이트를 각 플레이어에게 개인화된 메시지로 브로드캐스트합니다.
     * 각 플레이어는 자신의 패만 볼 수 있고, 다른 플레이어의 타일 개수만 알 수 있습니다.
     */
    fun broadcastGameUpdated(game: LexioGame)
}
