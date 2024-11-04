package com.project.messenger.controllers;

import com.project.messenger.dto.AuthDTO;
import com.project.messenger.dto.RegistrationDTO;
import com.project.messenger.models.UserProfile;
import com.project.messenger.repositories.UserProfileRepository;
import com.project.messenger.security.JWTUtil;
import com.project.messenger.services.RegistrationService;
import com.project.messenger.utils.UserProfileValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserProfileValidator userProfileValidator;
    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;

    @PostMapping("/registration")
    public ResponseEntity<?> performRegistration (@RequestBody RegistrationDTO registrationDTO,
                                                  BindingResult bindingResult) {
        UserProfile userProfile = modelMapper.map(registrationDTO, UserProfile.class);
        userProfileValidator.validate(userProfile, bindingResult);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        registrationService.register(userProfile);
        jwtUtil.generateToken(userProfile.getUsername(), userProfile.getId());
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> performLogin(
            @RequestBody AuthDTO authDTO) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword());
        try {
            authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().build();
        }
        int id = userProfileRepository.findByUsername(authDTO.getUsername()).get().getId();
        String token = jwtUtil.generateToken(authDTO.getUsername(), id);
        return ResponseEntity.ok(Map.of("token", token));
    }

}
