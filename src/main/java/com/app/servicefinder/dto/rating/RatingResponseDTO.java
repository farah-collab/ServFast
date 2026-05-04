package com.app.servicefinder.dto.rating;
 
import lombok.*;
import java.time.LocalDateTime;
 
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RatingResponseDTO {
    private Long id;
    private Integer score;
    private String comment;
    private String reviewerName;
    private String reviewerPhoto;
    private LocalDateTime createdAt;
}