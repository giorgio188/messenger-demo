package com.project.messenger.repositories;

import com.project.messenger.models.FriendList;
import com.project.messenger.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendListRepository extends JpaRepository<FriendList, Integer> {
    List<FriendList> findByUserId(UserProfile userId);
    List<FriendList> findByFriendId(UserProfile friendId);
    void deleteByUserIdAndFriendId(Optional<UserProfile> userId, Optional<UserProfile> friendId);
}
