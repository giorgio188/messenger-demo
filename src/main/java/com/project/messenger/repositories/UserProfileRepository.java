package com.project.messenger.repositories;

import com.project.messenger.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    Optional<UserProfile> findById(int id);
    Optional<UserProfile> findByUsername(String username);
}
