package com.insurance.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "insurance_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @Column(name = "policy_number", unique = true, nullable = false, length = 50)
    private String policyNumber;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Client policyClient;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_type_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private InsuranceType insuranceType;

    @Column(name = "policy_start_date", nullable = false)
    private LocalDate policyStartDate;

    @Column(name = "policy_end_date", nullable = false)
    private LocalDate policyEndDate;

    @Column(name = "insured_sum_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal insuredSumAmount;

    @Column(name = "premium_payment_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal premiumPaymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_status", nullable = false, length = 50)
    private PolicyStatus policyStatus = PolicyStatus.ACTIVE;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User managerUser;

    @Column(name = "policy_notes", columnDefinition = "TEXT")
    private String policyNotes;


    @OneToMany(mappedBy = "relatedPolicy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"relatedPolicy"})
    private List<InsuranceClaim> insuranceClaims = new ArrayList<>();

    @OneToMany(mappedBy = "relatedPolicy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"relatedPolicy"})
    private List<Payment> policyPayments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "policy_created_date", updatable = false)
    private LocalDateTime policyCreatedDate;

    @UpdateTimestamp
    @Column(name = "policy_updated_date")
    private LocalDateTime policyUpdatedDate;

    public enum PolicyStatus {
        ACTIVE,
        EXPIRED,
        SUSPENDED,
        TERMINATED
    }
}