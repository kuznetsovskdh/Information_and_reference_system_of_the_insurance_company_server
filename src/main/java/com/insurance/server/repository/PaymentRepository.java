package com.insurance.server.repository;

import com.insurance.server.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {


    List<Payment> findByRelatedPolicy_PolicyId(Long policyId);


    List<Payment> findByPaymentType(Payment.PaymentType paymentType);


    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);


    List<Payment> findByRelatedPolicy_PolicyIdAndPaymentStatus(
            Long policyId,
            Payment.PaymentStatus status
    );


    @Query("SELECT SUM(p.paymentAmount) FROM Payment p WHERE p.relatedPolicy.policyId = :policyId AND p.paymentStatus = 'COMPLETED'")
    BigDecimal calculateTotalPaymentsByPolicy(Long policyId);


    @Query("SELECT SUM(p.paymentAmount) FROM Payment p WHERE p.paymentStatus = 'COMPLETED'")
    BigDecimal getTotalCompletedPaymentsAmount();
}
