package com.insurance.server.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false, length = 100)
    private String emailAddress;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false, length = 255)
    private String userPassword;

    @Column(name = "full_user_name", nullable = false, length = 150)
    private String fullUserName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserRole userRole = UserRole.ROLE_CLIENT;

    @Column(length = 20)
    private String phoneNumber;

    @Column(name = "account_active")
    private Boolean accountActive = true;

    @CreationTimestamp
    @Column(name = "registration_date", updatable = false)
    private LocalDateTime registrationDate;

    @UpdateTimestamp
    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    public enum UserRole {
        ROLE_ADMIN,
        ROLE_MANAGER,
        ROLE_AGENT,
        ROLE_CLIENT
    }
}