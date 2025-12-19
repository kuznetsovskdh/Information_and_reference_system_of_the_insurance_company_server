package com.insurance.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "policy_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "insuranceClaims", "policyPayments"})
    private InsurancePolicy relatedPolicy;

    @Column(name = "payment_execution_date", nullable = false)
    private LocalDate paymentExecutionDate;

    @Column(name = "payment_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 50)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_reference_id", length = 100)
    private String transactionReferenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 50)
    private PaymentStatus paymentStatus = PaymentStatus.COMPLETED;

    @Column(name = "payment_notes", columnDefinition = "TEXT")
    private String paymentNotes;

    @CreationTimestamp
    @Column(name = "payment_created_date", updatable = false)
    private LocalDateTime paymentCreatedDate;

    public enum PaymentType {
        PREMIUM,
        INSTALLMENT,
        PENALTY_FEE,
        REFUND
    }

    public enum PaymentMethod {
        CASH,
        BANK_CARD,
        BANK_TRANSFER,
        ONLINE_PAYMENT
    }

    public enum PaymentStatus {
        COMPLETED,
        PENDING,
        FAILED,
        CANCELLED
    }
}