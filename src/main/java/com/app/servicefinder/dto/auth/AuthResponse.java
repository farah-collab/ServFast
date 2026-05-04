package com.app.servicefinder.dto.auth;
 
import lombok.*;
 
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private Long userId;
    private String fullName;
    private String email;
    private String profilePhoto;
}