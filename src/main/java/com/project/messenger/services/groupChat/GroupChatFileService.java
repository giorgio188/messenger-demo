package com.project.messenger.services.groupChat;

import com.project.messenger.models.GroupChat;
import com.project.messenger.models.GroupChatFiles;
import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.FileType;
import com.project.messenger.repositories.GroupChatFileRepository;
import com.project.messenger.repositories.GroupChatRepository;
import com.project.messenger.services.S3Service;
import com.project.messenger.services.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GroupChatFileService {

    private final UserProfileService userProfileService;
    private final GroupChatService groupChatService;
    private final GroupChatRepository groupChatRepository;
    private final S3Service s3Service;
    private final String FILE_DIRECTORY = "group-files";
    private final GroupChatFileRepository groupChatFileRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public GroupChatFiles sendFile(int senderId, int groupChatId, MultipartFile file)
            throws IOException {
        UserProfile sender = userProfileService.getUserProfile(senderId);
        GroupChat groupChat = modelMapper.map(groupChatService.getGroupChat(groupChatId, senderId), GroupChat.class);
        String fileName = s3Service.uploadFile(file, FILE_DIRECTORY);
        FileType fileType = FileType.getByContentType(file.getContentType())
                .orElseThrow(() -> new RuntimeException("Неподдерживаемый формат файла: " + file.getContentType()));

        GroupChatFiles newFile = new GroupChatFiles(
                groupChat,
                LocalDateTime.now(),
                sender,
                fileName,
                fileType
        );
        groupChatFileRepository.save(newFile);
        return newFile;
    }

    @Transactional
    public void deleteFile(int fileId){
        GroupChatFiles file = groupChatFileRepository.findById(fileId).orElse(null);
        if(file != null){
            groupChatFileRepository.delete(file);
            s3Service.deleteFile(file.getFileName());
        }
    }

    public List<GroupChatFiles> getAllFiles(int groupChatId){
        GroupChat groupChat = groupChatRepository.findById(groupChatId).orElse(null);
        List<GroupChatFiles> files = groupChatFileRepository.getAllFilesByGroupChat(groupChat);
        return files;
    }

    public String getFileById(int fileId){
        GroupChatFiles file = groupChatFileRepository.findById(fileId).orElse(null);
        if(file != null){
            return s3Service.getFileUrl(file.getFileName());
        } else throw new EntityNotFoundException("No such file");
    }

}


