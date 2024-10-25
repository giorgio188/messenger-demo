package com.project.messenger.services;

import com.project.messenger.models.PrivateChatMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateChatMessageService {


    private RedisTemplate<String, PrivateChatMessages> redisTemplate;

    public void saveMessage(PrivateChatMessages message) {
        String key = "messages:" + message.getPrivateChat();
        redisTemplate.opsForList().rightPush(key, message);
    }

    public List<PrivateChatMessages> getMessages(String receiver) {
        String key = "messages:" + receiver;
        return redisTemplate.opsForList().range(key, 0, -1)
                .stream()
                .collect(Collectors.toList());
    }
}
