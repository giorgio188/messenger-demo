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

}
