package com.app.servicefinder.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    // Kept for backward compatibility — computed from firstName + lastName
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String password;

    private String phone;
    private String profilePhoto;
    private String bio;
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.CLIENT;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.fullName == null && this.firstName != null) {
            this.fullName = this.firstName + (this.lastName != null ? " " + this.lastName : "");
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.firstName != null) {
            this.fullName = this.firstName + (this.lastName != null ? " " + this.lastName : "");
        }
    }

    public enum Role {
        CLIENT, PROVIDER, ADMIN
    }
}