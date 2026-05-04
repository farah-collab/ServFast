package com.app.servicefinder.dto.user;
 
import lombok.Data;
 
@Data
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private String bio;
    private String city;
    private String profilePhoto;
}