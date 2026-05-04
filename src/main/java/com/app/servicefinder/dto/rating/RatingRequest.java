package com.app.servicefinder.dto.rating;
 
import jakarta.validation.constraints.*;
import lombok.Data;
 
@Data
public class RatingRequest {
    @NotNull
    @Min(1) @Max(5)
    private Integer score;
 
    @Size(max = 500)
    private String comment;
 
    @NotNull
    private Long serviceId;
}