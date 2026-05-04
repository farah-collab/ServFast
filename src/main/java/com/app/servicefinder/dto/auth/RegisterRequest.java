package com.app.servicefinder.dto.auth;
 
import jakarta.validation.constraints.*;
import lombok.Data;
 
@Data
public class RegisterRequest {
    @NotBlank(message = "Nom complet requis")
    private String fullName;
 
    @Email(message = "Email invalide")
    @NotBlank(message = "Email requis")
    private String email;
 
    @Size(min = 6, message = "Mot de passe minimum 6 caractères")
    @NotBlank(message = "Mot de passe requis")
    private String password;
 
    private String phone;
    private String city;
}