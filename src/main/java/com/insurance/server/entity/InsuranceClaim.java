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

@Entity
@Table(name = "insurance_claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "insuranceClaims", "policyPayments"})
    private InsurancePolicy relatedPolicy;

    @Column(name = "claim_number", unique = true, nullable = false, length = 50)
    private String claimNumber;

    @Column(name = "incident_occurrence_date", nullable = false)
    private LocalDate incidentOccurrenceDate;

    @CreationTimestamp
    @Column(name = "claim_report_date", updatable = false)
    private LocalDateTime claimReportDate;

    @Column(name = "incident_description", nullable = false, columnDefinition = "TEXT")
    private String incidentDescription;

    @Column(name = "claimed_compensation_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal claimedCompensationAmount;

    @Column(name = "approved_compensation_amount", precision = 15, scale = 2)
    private BigDecimal approvedCompensationAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_status", nullable = false, length = 50)
    private ClaimStatus claimStatus = ClaimStatus.UNDER_REVIEW;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processing_manager_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User processingManager;

    @Column(name = "resolution_decision_date")
    private LocalDate resolutionDecisionDate;

    @Column(name = "payment_execution_date")
    private LocalDate paymentExecutionDate;

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    @UpdateTimestamp
    @Column(name = "claim_updated_date")
    private LocalDateTime claimUpdatedDate;

    public enum ClaimStatus {
        UNDER_REVIEW,
        APPROVED,
        REJECTED,
        PAID,
        CANCELLED
    }
}