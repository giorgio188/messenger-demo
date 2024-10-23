package com.project.messenger.services;
import com.project.messenger.models.UserProfile;
import com.project.messenger.repositories.UserProfileRepository;
import com.project.messenger.security.UserProfileDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileDetailsService implements UserDetailsService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserProfile> userProfile = userProfileRepository.findByUsername(username);
        if (userProfile.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserProfileDetails(userProfile.get());
    }
}
