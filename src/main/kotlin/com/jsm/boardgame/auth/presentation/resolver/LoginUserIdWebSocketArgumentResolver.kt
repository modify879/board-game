package com.jsm.boardgame.auth.presentation.resolver

import com.jsm.boardgame.auth.domain.model.AuthUserId
import com.jsm.boardgame.auth.presentation.annotation.LoginUserId
import org.springframework.core.MethodParameter
import org.springframework.messaging.Message
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component

@Component
class LoginUserIdWebSocketArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(LoginUserId::class.java) &&
                parameter.parameterType == Long::class.java
    }

    override fun resolveArgument(parameter: MethodParameter, message: Message<*>): Any {
        val accessor = StompHeaderAccessor.wrap(message)
        val principal = accessor.user
            ?: throw IllegalStateException("인증 정보가 없습니다.")

        // Principal이 UsernamePasswordAuthenticationToken인 경우
        if (principal is UsernamePasswordAuthenticationToken) {
            val authUserId = principal.principal as? AuthUserId
                ?: throw IllegalStateException("유효하지 않은 인증 정보입니다.")
            return authUserId.value
        }

        // Principal.name을 통해 userId 추출 (fallback)
        return principal.name.toLong()
    }
}
