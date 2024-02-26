package com.jsm.boardgame.common.redis;

import com.jsm.boardgame.web.dto.request.game.GameWebSocketMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic channelTopic, GameWebSocketMessage message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
