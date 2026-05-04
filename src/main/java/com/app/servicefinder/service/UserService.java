package com.app.servicefinder.service;
 
import com.app.servicefinder.dto.user.*;
import com.app.servicefinder.model.User;
import com.app.servicefinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
@Service
@RequiredArgsConstructor
public class UserService {
 
    private final UserRepository userRepository;
 
    public UserResponseDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return mapToDTO(user);
    }
 
    public UserResponseDTO updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (request.getFullName() != null)    user.setFullName(request.getFullName());
        if (request.getPhone() != null)       user.setPhone(request.getPhone());
        if (request.getBio() != null)         user.setBio(request.getBio());
        if (request.getCity() != null)        user.setCity(request.getCity());
        if (request.getProfilePhoto() != null) user.setProfilePhoto(request.getProfilePhoto());
        return mapToDTO(userRepository.save(user));
    }
 
    public UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profilePhoto(user.getProfilePhoto())
                .bio(user.getBio())
                .city(user.getCity())
                .createdAt(user.getCreatedAt())
                .build();
    }
}