package com.project.messenger.utils;

import com.project.messenger.dto.GroupChatCreatingDTO;
import com.project.messenger.models.GroupChat;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MapperForDTO {
    private final ModelMapper modelMapper;

    public GroupChatCreatingDTO convertToGroupChatDTO(GroupChat groupChat) {
        return this.modelMapper.map(groupChat, GroupChatCreatingDTO.class);
    }

    public GroupChat convertToGroupChat(GroupChatCreatingDTO groupChatDTO) {
        return this.modelMapper.map(groupChatDTO, GroupChat.class);
    }
}
