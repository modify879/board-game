package com.jsm.boardgame.common.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsm.boardgame.web.dto.request.game.GameWebSocketMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessageSendingOperations operations;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String publishMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());
        GameWebSocketMessage gameWebSocketMessage = objectMapper.readValue(publishMessage, GameWebSocketMessage.class);
        operations.convertAndSend("/");
    }
}
