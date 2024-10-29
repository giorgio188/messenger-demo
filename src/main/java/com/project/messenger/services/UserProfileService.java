package com.project.messenger.services;

import com.project.messenger.models.FriendList;
import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.ProfileStatus;
import com.project.messenger.repositories.FriendListRepository;
import com.project.messenger.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final FriendListRepository friendListRepository;


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
        userProfileRepository.save(updatedUserProfile);
    }

    @Transactional
    public void deleteUserProfile(int id) {
        userProfileRepository.deleteById(id);
    }

    @Transactional
    public void addFriend(int userId, int friendId) {
        Optional<UserProfile> user = userProfileRepository.findById(userId);
        Optional<UserProfile> friend = userProfileRepository.findById(friendId);
        FriendList friendList = new FriendList();
        friendList.setUserId(user.get());
        friendList.setFriendId(friend.get());
        friendList.setAddedAt(LocalDateTime.now());
        friendListRepository.save(friendList);
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
    }

    @Transactional
    public void setUserOnlineStatus(int id, ProfileStatus status) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(id);
        if (userProfile.isPresent()) {
            UserProfile user = userProfile.get();
            user.setStatus(status);
            userProfileRepository.save(user);
        }
    }
}
