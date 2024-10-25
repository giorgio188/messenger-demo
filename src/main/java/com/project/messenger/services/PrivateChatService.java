package com.project.messenger.services;

import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.UserProfile;
import com.project.messenger.repositories.PrivateChatRepository;
import com.project.messenger.repositories.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateChatService {

    private final UserProfileRepository userProfileRepository;
    private final PrivateChatRepository privateChatRepository;

    public PrivateChat getPrivateChat(int senderId, int receiverId) {
        UserProfile sender = userProfileRepository.findById(senderId).orElse(null);
        UserProfile receiver = userProfileRepository.findById(receiverId).orElse(null);

        PrivateChat privateChat = privateChatRepository.findPrivateChatBySenderAndReceiver(sender, receiver);
        if (privateChat == null) {
            privateChat = privateChatRepository.findPrivateChatBySenderAndReceiver(receiver, sender);
        }
        if (privateChat == null) {
            throw new EntityNotFoundException("Private chat not found");
        }
        return privateChat;
    }

    public List<PrivateChat> getAllChatsOfOneUser(int id) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(id);
        List<PrivateChat> allChatsAsSender = privateChatRepository.findPrivateChatBySender(userProfile.orElse(null));
        List<PrivateChat> allChatsAsReceiver = privateChatRepository.findPrivateChatByReceiver(userProfile.orElse(null));
        List<PrivateChat> allChats = new ArrayList<>();
        allChats.addAll(allChatsAsSender);
        allChats.addAll(allChatsAsReceiver);
        return allChats;
    }

    @Transactional
    public void createPrivateChat(int senderId, int receiverId) {
        UserProfile sender = userProfileRepository.findById(senderId).orElse(null);
        UserProfile receiver = userProfileRepository.findById(receiverId).orElse(null);
        PrivateChat privateChat = new PrivateChat();
        privateChat.setSender(sender);
        privateChat.setReceiver(receiver);
        privateChat.setCreatedAt(LocalDateTime.now());
        privateChatRepository.save(privateChat);
    }

    @Transactional
    public void deletePrivateChat(int id) {
        privateChatRepository.deleteById(id);
    }

}
