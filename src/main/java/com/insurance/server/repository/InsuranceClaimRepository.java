package com.insurance.server.repository;

import com.insurance.server.entity.InsuranceClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, Long> {


    List<InsuranceClaim> findByRelatedPolicy_PolicyId(Long policyId);


    List<InsuranceClaim> findByClaimStatus(InsuranceClaim.ClaimStatus status);


    List<InsuranceClaim> findByProcessingManager_UserId(Long managerId);


    @Query("SELECT COUNT(c) FROM InsuranceClaim c WHERE c.claimStatus = :status")
    long countByStatus(InsuranceClaim.ClaimStatus status);


    @Query("SELECT SUM(c.approvedCompensationAmount) FROM InsuranceClaim c WHERE c.claimStatus = 'PAID'")
    java.math.BigDecimal getTotalPaidAmount();
}
