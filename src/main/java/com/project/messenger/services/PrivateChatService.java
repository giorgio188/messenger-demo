package com.project.messenger.services;

import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.UserProfile;
import com.project.messenger.repositories.PrivateChatRepository;
import com.project.messenger.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateChatService {

    private final UserProfileRepository userProfileRepository;
    private final PrivateChatRepository privateChatRepository;



    public Optional<PrivateChat> getAllChatsByMemberId(int id) {
        UserProfile userProfile = userProfileRepository.findById(id).orElse(null);
        return privateChatRepository.findPrivateChatByMember(userProfile);
    }



}
