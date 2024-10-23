package com.project.messenger.utils;

import com.project.messenger.models.UserProfile;
import com.project.messenger.services.UserProfileDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProfileValidator implements Validator {

    private final UserProfileDetailsService userProfileDetailsService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserProfile.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.info("Loading user profile");
        UserProfile userProfile = (UserProfile) target;
        log.info("Validating of user's details");
//TODO
    }
}
