package com.jsm.boardgame.auth.presentation.resolver

import com.jsm.boardgame.auth.domain.model.AuthUserId
import com.jsm.boardgame.auth.presentation.annotation.LoginUserId
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class LoginUserIdArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(LoginUserId::class.java) &&
                parameter.parameterType == Long::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("인증 정보가 없습니다.")

        val principal = authentication.principal as? AuthUserId
            ?: throw IllegalStateException("유효하지 않은 인증 정보입니다.")

        return principal.value
    }
}
