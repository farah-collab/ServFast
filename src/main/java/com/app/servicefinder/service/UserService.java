package com.app.servicefinder.service;

import com.app.servicefinder.dto.user.*;
import com.app.servicefinder.model.User;
import com.app.servicefinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponseDTO getById(Long id) {
        return toDTO(userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Transactional
    public UserResponseDTO updateProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getProfilePhoto() != null) user.setProfilePhoto(request.getProfilePhoto());

        return toDTO(userRepository.save(user));
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private UserResponseDTO toDTO(User u) {
        return UserResponseDTO.builder()
            .id(u.getId())
            .firstName(u.getFirstName())
            .lastName(u.getLastName())
            .fullName(u.getFullName())
            .email(u.getEmail())
            .phone(u.getPhone())
            .profilePhoto(u.getProfilePhoto())
            .bio(u.getBio())
            .city(u.getCity())
            .role(u.getRole().name())
            .createdAt(u.getCreatedAt())
            .build();
    }
}