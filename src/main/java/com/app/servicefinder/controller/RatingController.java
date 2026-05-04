package com.app.servicefinder.controller;
 
import com.app.servicefinder.dto.rating.*;
import com.app.servicefinder.security.JwtUtil;
import com.app.servicefinder.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
 
@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
 
    private final RatingService ratingService;
    private final JwtUtil jwtUtil;
 
    // POST /api/ratings
    @PostMapping
    public ResponseEntity<RatingResponseDTO> addRating(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody RatingRequest request) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.addRating(userId, request));
    }
 
    // GET /api/ratings/service/{serviceId}
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<RatingResponseDTO>> getByService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(ratingService.getByService(serviceId));
    }
}