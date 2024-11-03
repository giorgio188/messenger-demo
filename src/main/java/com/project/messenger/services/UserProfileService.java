package com.project.messenger.services;

import com.project.messenger.models.FriendList;
import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.ProfileStatus;
import com.project.messenger.repositories.FriendListRepository;
import com.project.messenger.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final FriendListRepository friendListRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final S3Service s3Service;
    private static final String AVATAR_DIRECTORY = "avatars";

    public UserProfile getUserProfile(int id) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(id);
        return userProfile.orElse(null);
    }

    public List<UserProfile> searchUsers(String query) {
        return userProfileRepository.findByUsernameContainingOrNicknameContaining(query, query);
    }

    @Transactional
    public void updateUserProfile(int id, UserProfile updatedUserProfile) {
        updatedUserProfile.setId(id);
        UserProfile savedProfile = userProfileRepository.save(updatedUserProfile);
        List<UserProfile> friendList = getFriendList(id);
        messagingTemplate.convertAndSend("/topic/user/" + id, savedProfile);
        friendList.forEach(friend ->
                messagingTemplate.convertAndSend("/topic/user/" + friend.getId() + "/friend-update", savedProfile)
        );
    }

    @Transactional
    public void deleteUserProfile(int id) {
        userProfileRepository.deleteById(id);
    }

    @Transactional
    public void addFriend(int userId, int friendId) {
        Optional<UserProfile> user = userProfileRepository.findById(userId);
        Optional<UserProfile> friend = userProfileRepository.findById(friendId);
        FriendList friendList = new FriendList(user.get(), friend.get(), LocalDateTime.now());
        friendListRepository.save(friendList);

        messagingTemplate.convertAndSend(
                "/topic/friends/" + userId,
                getFriendList(userId)
        );

        messagingTemplate.convertAndSend(
                "/topic/friends/" + friendId,
                getFriendList(friendId)
        );
    }


    public List<UserProfile> getFriendList(int userId) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<FriendList> friendsAsUser = friendListRepository.findByUserId(userProfile);
        List<FriendList> friendsAsFriend = friendListRepository.findByFriendId(userProfile);
        Set<UserProfile> friendsSet = new HashSet<>();
        friendsAsUser.forEach(friendList -> friendsSet.add(friendList.getFriendId()));
        friendsAsFriend.forEach(friendList -> friendsSet.add(friendList.getUserId()));
        return new ArrayList<>(friendsSet);
    }

    @Transactional
    public void deleteFriend(int userId, int friendToBeDeleted) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(userId);
        Optional<UserProfile> friend = userProfileRepository.findById(friendToBeDeleted);
        friendListRepository.deleteByUserIdAndFriendId(userProfile, friend);
        friendListRepository.deleteByUserIdAndFriendId(friend, userProfile);

        messagingTemplate.convertAndSend(
                "/topic/friends/" + userId,
                getFriendList(userId)
        );

        messagingTemplate.convertAndSend(
                "/topic/friends/" + friendToBeDeleted,
                getFriendList(friendToBeDeleted)
        );
    }

    @Transactional
    public void setUserOnlineStatus(int id, ProfileStatus status) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(id);
        if (userProfile.isPresent()) {
            UserProfile user = userProfile.get();
            user.setStatus(status);
            UserProfile savedProfile = userProfileRepository.save(user);

            // Получаем список друзей пользователя
            List<UserProfile> friendList = getFriendList(id);

            // Отправляем обновление статуса всем друзьям пользователя
            friendList.forEach(friend ->
                    messagingTemplate.convertAndSend("/topic/user/" + friend.getId() + "/friend-update", savedProfile)
            );
        }
    }

    @Transactional
    public void handleLogout(int userId) {
        setUserOnlineStatus(userId, ProfileStatus.OFFLINE);
    }

    @Transactional
    public String uploadAvatar(int userId, MultipartFile file) {
//        log.debug("Starting avatar upload for user: {}", userId);
//
//        // Проверяем файл
//        if (file == null || file.isEmpty()) {
//            log.error("File is empty for user: {}", userId);
//            throw new IllegalArgumentException("File is empty");
//        }
//
//        // Проверяем тип файла
//        String contentType = file.getContentType();
//        if (contentType == null || !contentType.startsWith("image/")) {
//            log.error("Invalid file type: {} for user: {}", contentType, userId);
//            throw new IllegalArgumentException("Invalid file type. Only images are allowed");
//        }
//
//        // Получаем пользователя
//        UserProfile userProfile = getUserProfile(userId);
//        if (userProfile == null) {
//            log.error("User not found: {}", userId);
//            throw new RuntimeException("User not found");
//        }
//
//        try {
//            log.debug("Checking existing avatar for user: {}", userId);
//            // Удаляем старый аватар если есть
//            String currentAvatar = userProfile.getAvatar();
//            if (currentAvatar != null && !currentAvatar.isEmpty()) {
//                try {
//                    log.debug("Deleting old avatar: {} for user: {}", currentAvatar, userId);
//                    s3Service.deleteFile(currentAvatar);
//                } catch (Exception e) {
//                    log.warn("Failed to delete old avatar: {} for user: {}. Error: {}",
//                            currentAvatar, userId, e.getMessage());
//                    // Продолжаем выполнение даже если не удалось удалить старый файл
//                }
//            }
//
//            // Загружаем новый аватар
//            log.debug("Uploading new avatar for user: {}", userId);
//            String fileName = s3Service.uploadFile(file, AVATAR_DIRECTORY);
//            log.debug("Successfully uploaded new avatar: {} for user: {}", fileName, userId);
//
//            // Сохраняем имя файла в БД
//            log.debug("Updating user profile with new avatar filename: {}", fileName);
//            userProfile.setAvatar(fileName);
//            updateUserProfile(userId, userProfile);
//            log.debug("Successfully updated user profile with new avatar");
//
//            // Получаем URL для клиента
//            String avatarUrl = s3Service.getFileUrl(fileName);
//            log.debug("Generated avatar URL: {}", avatarUrl);
//
//            return avatarUrl;
//        } catch (Exception e) {
//            log.error("Error during avatar upload for user: {}. Error: {}", userId, e.getMessage(), e);
//            throw new RuntimeException("Failed to upload avatar: " + e.getMessage(), e);
//        }
//        ----------------------------
        UserProfile userProfile = getUserProfile(userId);
        try {
            if (!userProfile.getAvatar().isEmpty() && userProfile.getAvatar() != null) {
                s3Service.deleteFile(userProfile.getAvatar());
            }

            String fileName = s3Service.uploadFile(file, AVATAR_DIRECTORY);
            userProfile.setAvatar(fileName);
            userProfileRepository.save(userProfile);
            return s3Service.getFileUrl(fileName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload avatar", e);
        }
    }

    @Transactional
    public void deleteAvatar(int userId) {
        UserProfile userProfile = getUserProfile(userId);
        try {
            String avatar = userProfile.getAvatar();
            if (!avatar.isEmpty() && userProfile.getAvatar() != null) {
                s3Service.deleteFile(avatar);
                userProfile.setAvatar(null);
                userProfileRepository.save(userProfile);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete avatar", e);
        }
    }

    public String getAvatarLink(String fileName) {
        return s3Service.getFileUrl(fileName);
    }
}
