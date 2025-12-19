package com.insurance.server.repository;

import com.insurance.server.entity.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Long> {


    Optional<InsurancePolicy> findByPolicyNumber(String policyNumber);


    List<InsurancePolicy> findByPolicyClient_ClientId(Long clientId);


    List<InsurancePolicy> findByManagerUser_UserId(Long managerId);


    List<InsurancePolicy> findByPolicyStatus(InsurancePolicy.PolicyStatus status);


    List<InsurancePolicy> findByPolicyStatusAndPolicyEndDateAfter(
            InsurancePolicy.PolicyStatus status,
            LocalDate currentDate
    );


    @Query("SELECT p FROM InsurancePolicy p WHERE p.policyEndDate BETWEEN :today AND :futureDate AND p.policyStatus = 'ACTIVE'")
    List<InsurancePolicy> findExpiringPolicies(
            @Param("today") LocalDate today,
            @Param("futureDate") LocalDate futureDate
    );


    @Query("SELECT SUM(p.insuredSumAmount) FROM InsurancePolicy p WHERE p.policyStatus = 'ACTIVE'")
    java.math.BigDecimal getTotalInsuredAmount();
}
