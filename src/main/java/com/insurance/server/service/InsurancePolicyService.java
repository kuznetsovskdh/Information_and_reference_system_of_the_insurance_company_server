package com.insurance.server.service;

import com.insurance.server.entity.InsurancePolicy;
import com.insurance.server.exception.ResourceNotFoundException;
import com.insurance.server.repository.InsurancePolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class InsurancePolicyService {

    private final InsurancePolicyRepository policyRepository;

    public List<InsurancePolicy> getAllPolicies() {
        log.info("Получение всех полисов");
        return policyRepository.findAll();
    }

    public InsurancePolicy getPolicyById(Long id) {
        log.info("Поиск полиса с ID: {}", id);
        return policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Полис с ID " + id + " не найден"
                ));
    }


    public List<InsurancePolicy> getPoliciesByClient(Long clientId) {
        log.info("Получение полисов клиента с ID: {}", clientId);
        return policyRepository.findByPolicyClient_ClientId(clientId);
    }


    public List<InsurancePolicy> getPoliciesByManager(Long managerId) {
        log.info("Получение полисов менеджера с ID: {}", managerId);
        return policyRepository.findByManagerUser_UserId(managerId);
    }


    public List<InsurancePolicy> getActivePolicies() {
        log.info("Получение активных полисов");
        return policyRepository.findByPolicyStatusAndPolicyEndDateAfter(
                InsurancePolicy.PolicyStatus.ACTIVE,
                LocalDate.now()
        );
    }


    public List<InsurancePolicy> getExpiringPolicies(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);

        log.info("Получение полисов, истекающих в период {} - {}", today, futureDate);
        return policyRepository.findExpiringPolicies(today, futureDate);
    }


    @Transactional
    public InsurancePolicy createPolicy(InsurancePolicy policy) {
        log.info("Создание нового полиса");

        if (policy.getPolicyNumber() == null || policy.getPolicyNumber().isEmpty()) {
            policy.setPolicyNumber(generatePolicyNumber());
        }

        if (policy.getPolicyStatus() == null) {
            policy.setPolicyStatus(InsurancePolicy.PolicyStatus.ACTIVE);
        }

        InsurancePolicy saved = policyRepository.save(policy);
        log.info("Полис создан с ID: {} и номером: {}",
                saved.getPolicyId(), saved.getPolicyNumber());
        return saved;
    }

    @Transactional
    public InsurancePolicy updatePolicy(Long id, InsurancePolicy updatedPolicy) {
        log.info("Обновление полиса с ID: {}", id);

        InsurancePolicy policy = getPolicyById(id);

        if (updatedPolicy.getPolicyStartDate() != null) {
            policy.setPolicyStartDate(updatedPolicy.getPolicyStartDate());
        }
        if (updatedPolicy.getPolicyEndDate() != null) {
            policy.setPolicyEndDate(updatedPolicy.getPolicyEndDate());
        }
        if (updatedPolicy.getInsuredSumAmount() != null) {
            policy.setInsuredSumAmount(updatedPolicy.getInsuredSumAmount());
        }
        if (updatedPolicy.getPremiumPaymentAmount() != null) {
            policy.setPremiumPaymentAmount(updatedPolicy.getPremiumPaymentAmount());
        }
        if (updatedPolicy.getPolicyStatus() != null) {
            policy.setPolicyStatus(updatedPolicy.getPolicyStatus());
        }
        if (updatedPolicy.getPolicyNotes() != null) {
            policy.setPolicyNotes(updatedPolicy.getPolicyNotes());
        }

        return policyRepository.save(policy);
    }

    @Transactional
    public void deletePolicy(Long id) {
        log.info("Удаление полиса с ID: {}", id);

        if (!policyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Полис с ID " + id + " не найден");
        }
        policyRepository.deleteById(id);
    }


    private String generatePolicyNumber() {
        String prefix = "POL";
        String year = String.valueOf(LocalDate.now().getYear());
        String uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + year + "-" + uniqueId;
    }


    public BigDecimal getTotalInsuredAmount() {
        BigDecimal total = policyRepository.getTotalInsuredAmount();
        return total != null ? total : BigDecimal.ZERO;
    }
}
