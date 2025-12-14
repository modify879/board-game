package com.jsm.boardgame.auth.presentation.interceptor

import com.jsm.boardgame.auth.infrastructure.jwt.JwtTokenProvider
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class StompAuthInterceptor(
    private val jwtTokenProvider: JwtTokenProvider
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java) ?: return message

        // CONNECT 명령이 올 때 토큰 검증
        if (accessor.command == StompCommand.CONNECT) {
            val token = accessor.getFirstNativeHeader("Authorization")
                ?.removePrefix("Bearer ")

            if (token != null && jwtTokenProvider.validateToken(token)) {
                val userId = jwtTokenProvider.getUserId(token)
                val role = jwtTokenProvider.getRole(token)

                // 인증 객체 생성
                val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
                val auth = UsernamePasswordAuthenticationToken(
                    userId, // Principal (ID)
                    null,
                    authorities
                )

                // Accessor에 인증 정보 저장 (WebSocket 세션 동안 유지됨)
                accessor.user = auth
            } else {
                // 유효하지 않은 토큰이거나 토큰이 없는 경우 예외 발생 (연결 거부)
                throw IllegalArgumentException("Invalid or missing JWT token")
            }
        }
        return message
    }
}
