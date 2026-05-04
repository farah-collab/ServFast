package com.app.servicefinder.controller;
 
import com.app.servicefinder.dto.user.*;
import com.app.servicefinder.security.JwtUtil;
import com.app.servicefinder.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
 
    private final UserService userService;
    private final JwtUtil jwtUtil;
 
    // GET /api/users/me
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        return ResponseEntity.ok(userService.getProfile(userId));
    }
 
    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }
 
    // PUT /api/users/me
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UpdateProfileRequest request) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }
}