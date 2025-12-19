package com.insurance.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    @Column(name = "full_client_name", nullable = false, length = 150)
    private String fullClientName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "passport_series", length = 10)
    private String passportSeries;

    @Column(name = "passport_number", nullable = false, length = 20)
    private String passportNumber;

    @Column(name = "client_address", length = 255)
    private String clientAddress;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "email_address", length = 100)
    private String emailAddress;

    @Column(name = "inn_number", length = 12)
    private String innNumber;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User managerUser;

    @CreationTimestamp
    @Column(name = "registration_date", updatable = false)
    private LocalDateTime registrationDate;

    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
}