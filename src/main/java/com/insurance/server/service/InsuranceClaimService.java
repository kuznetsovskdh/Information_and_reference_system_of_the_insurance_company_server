package com.insurance.server.service;

import com.insurance.server.entity.InsuranceClaim;
import com.insurance.server.exception.ResourceNotFoundException;
import com.insurance.server.repository.InsuranceClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class InsuranceClaimService {

    private final InsuranceClaimRepository claimRepository;


    public List<InsuranceClaim> getAllClaims() {
        log.info("Получение всех страховых случаев");
        return claimRepository.findAll();
    }


    public InsuranceClaim getClaimById(Long id) {
        log.info("Поиск заявления с ID: {}", id);
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Заявление с ID " + id + " не найдено"
                ));
    }


    public List<InsuranceClaim> getClaimsByPolicy(Long policyId) {
        log.info("Получение заявлений по полису с ID: {}", policyId);
        return claimRepository.findByRelatedPolicy_PolicyId(policyId);
    }


    public List<InsuranceClaim> getClaimsByStatus(InsuranceClaim.ClaimStatus status) {
        log.info("Получение заявлений со статусом: {}", status);
        return claimRepository.findByClaimStatus(status);
    }


    @Transactional
    public InsuranceClaim createClaim(InsuranceClaim claim) {
        log.info("Создание нового заявления о страховом случае");

        // Генерируем номер заявления
        if (claim.getClaimNumber() == null || claim.getClaimNumber().isEmpty()) {
            claim.setClaimNumber(generateClaimNumber());
        }

        // Устанавливаем статус по умолчанию
        if (claim.getClaimStatus() == null) {
            claim.setClaimStatus(InsuranceClaim.ClaimStatus.UNDER_REVIEW);
        }

        InsuranceClaim saved = claimRepository.save(claim);
        log.info("Заявление создано с ID: {} и номером: {}",
                saved.getClaimId(), saved.getClaimNumber());
        return saved;
    }


    @Transactional
    public InsuranceClaim updateClaim(Long id, InsuranceClaim updatedClaim) {
        log.info("Обновление заявления с ID: {}", id);

        InsuranceClaim claim = getClaimById(id);

        if (updatedClaim.getIncidentDescription() != null) {
            claim.setIncidentDescription(updatedClaim.getIncidentDescription());
        }
        if (updatedClaim.getClaimedCompensationAmount() != null) {
            claim.setClaimedCompensationAmount(updatedClaim.getClaimedCompensationAmount());
        }
        if (updatedClaim.getApprovedCompensationAmount() != null) {
            claim.setApprovedCompensationAmount(updatedClaim.getApprovedCompensationAmount());
        }
        if (updatedClaim.getClaimStatus() != null) {
            claim.setClaimStatus(updatedClaim.getClaimStatus());
        }
        if (updatedClaim.getResolutionDecisionDate() != null) {
            claim.setResolutionDecisionDate(updatedClaim.getResolutionDecisionDate());
        }
        if (updatedClaim.getPaymentExecutionDate() != null) {
            claim.setPaymentExecutionDate(updatedClaim.getPaymentExecutionDate());
        }
        if (updatedClaim.getAdditionalNotes() != null) {
            claim.setAdditionalNotes(updatedClaim.getAdditionalNotes());
        }

        return claimRepository.save(claim);
    }


    @Transactional
    public void deleteClaim(Long id) {
        log.info("Удаление заявления с ID: {}", id);

        if (!claimRepository.existsById(id)) {
            throw new ResourceNotFoundException("Заявление с ID " + id + " не найдено");
        }
        claimRepository.deleteById(id);
    }


    private String generateClaimNumber() {
        String prefix = "CLM";
        String uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + uniqueId;
    }


    public BigDecimal getTotalPaidAmount() {
        BigDecimal total = claimRepository.getTotalPaidAmount();
        return total != null ? total : BigDecimal.ZERO;
    }


    public long countByStatus(InsuranceClaim.ClaimStatus status) {
        return claimRepository.countByStatus(status);
    }
}
