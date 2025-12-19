package com.insurance.server.controller;

import com.insurance.server.entity.InsuranceClaim;
import com.insurance.server.service.InsuranceClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class InsuranceClaimController {

    private final InsuranceClaimService claimService;


    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<InsuranceClaim>> getAllClaims() {
        log.info("Запрос всех страховых случаев");
        return ResponseEntity.ok(claimService.getAllClaims());
    }


    @GetMapping("/{id}")
    public ResponseEntity<InsuranceClaim> getClaimById(@PathVariable Long id) {
        log.info("Запрос заявления с ID: {}", id);
        return ResponseEntity.ok(claimService.getClaimById(id));
    }


    @GetMapping("/policy/{policyId}")
    public ResponseEntity<List<InsuranceClaim>> getClaimsByPolicy(@PathVariable Long policyId) {
        log.info("Запрос заявлений по полису с ID: {}", policyId);
        return ResponseEntity.ok(claimService.getClaimsByPolicy(policyId));
    }


    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<InsuranceClaim>> getClaimsByStatus(@RequestParam String status) {
        log.info("Запрос заявлений со статусом: {}", status);
        InsuranceClaim.ClaimStatus claimStatus = InsuranceClaim.ClaimStatus.valueOf(status);
        return ResponseEntity.ok(claimService.getClaimsByStatus(claimStatus));
    }


    @PostMapping
    public ResponseEntity<Map<String, Object>> createClaim(@RequestBody InsuranceClaim claim) {
        log.info("Запрос на создание нового заявления о страховом случае");

        InsuranceClaim created = claimService.createClaim(claim);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Заявление успешно создано");
        response.put("claim", created);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<InsuranceClaim> updateClaim(
            @PathVariable Long id,
            @RequestBody InsuranceClaim claim) {
        log.info("Запрос на обновление заявления с ID: {}", id);
        return ResponseEntity.ok(claimService.updateClaim(id, claim));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteClaim(@PathVariable Long id) {
        log.info("Запрос на удаление заявления с ID: {}", id);

        claimService.deleteClaim(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Заявление удалено");

        return ResponseEntity.ok(response);
    }


    @GetMapping("/stats/total-paid")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getTotalPaidAmount() {
        BigDecimal total = claimService.getTotalPaidAmount();

        Map<String, Object> response = new HashMap<>();
        response.put("totalPaidAmount", total);

        return ResponseEntity.ok(response);
    }
}
