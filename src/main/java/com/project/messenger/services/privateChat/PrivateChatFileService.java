package com.project.messenger.services.privateChat;

import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.PrivateChatFiles;
import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.FileType;
import com.project.messenger.repositories.PrivateChatFileRepository;
import com.project.messenger.repositories.PrivateChatRepository;
import com.project.messenger.services.S3Service;
import com.project.messenger.services.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateChatFileService {

    private final S3Service s3Service;
    private final PrivateChatFileRepository privateChatFileRepository;
    private final PrivateChatRepository privateChatRepository;
    private final UserProfileService userProfileService;
    private static final String FILE_DIRECTORY = "private-files";


    public List<PrivateChatFiles> getPrivateChatFiles(int privateChatId) {
        PrivateChat privateChat = privateChatRepository.findById(privateChatId).orElse(null);
        List<PrivateChatFiles> privateChatFiles = privateChatFileRepository.findByPrivateChatOrderBySentAtDesc(privateChat);
        return privateChatFiles;
    }

    @Transactional
    public PrivateChatFiles sendPrivateChatFile(int senderId, int privateChatId, MultipartFile file) {
        PrivateChat privateChat = privateChatRepository.findById(privateChatId)
                .orElseThrow(() -> new RuntimeException("Чат не найден"));
        UserProfile sender = userProfileService.getUserProfile(senderId);
        UserProfile receiver = privateChat.getSender().getId() == senderId
                ? privateChat.getReceiver()
                : privateChat.getSender();
        FileType fileType = FileType.getByContentType(file.getContentType())
                .orElseThrow(() -> new RuntimeException("Неподдерживаемый формат файла: " + file.getContentType()));

        String filePath = s3Service.uploadFile(file, FILE_DIRECTORY);
        PrivateChatFiles privateChatFiles = new PrivateChatFiles(privateChat, sender, receiver,
                LocalDateTime.now(), filePath, fileType);
        FileType.getByContentType(file.getContentType());
        privateChatFileRepository.save(privateChatFiles);
        return privateChatFiles;
    }

    @Transactional
    public void deletePrivateChatFile(int fileId) {
        Optional<PrivateChatFiles> privateChatFile = privateChatFileRepository.findById(fileId);
        if (privateChatFile.isPresent()) {
            String fileName = privateChatFile.get().getFileName();
            s3Service.deleteFile(fileName);
            privateChatFileRepository.delete(privateChatFile.get());
        } else throw new EntityNotFoundException("File with id " + fileId + " not found");
    }

    public String getFileById(int fileId){
        PrivateChatFiles file = privateChatFileRepository.findById(fileId).orElse(null);
        if(file != null){
            return s3Service.getFileUrl(file.getFileName());
        } else throw new EntityNotFoundException("No such file");
    }
}
