package com.jsm.boardgame.config

import com.jsm.boardgame.auth.presentation.interceptor.StompAuthInterceptor
import com.jsm.boardgame.auth.presentation.resolver.LoginUserIdWebSocketArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val stompAuthInterceptor: StompAuthInterceptor,
    private val loginUserIdWebSocketArgumentResolver: LoginUserIdWebSocketArgumentResolver
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic", "/queue")
        config.setApplicationDestinationPrefixes("/app")
        // /user/{userId}/queue/... 경로를 사용하기 위해 user destination prefix 설정
        config.setUserDestinationPrefix("/user")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS()
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(stompAuthInterceptor)
    }

    override fun addArgumentResolvers(argumentResolvers: MutableList<org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver>) {
        argumentResolvers.add(loginUserIdWebSocketArgumentResolver)
    }
}
