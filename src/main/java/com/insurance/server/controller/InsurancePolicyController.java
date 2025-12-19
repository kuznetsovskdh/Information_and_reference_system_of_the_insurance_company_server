package com.insurance.server.controller;

import com.insurance.server.entity.InsurancePolicy;
import com.insurance.server.service.InsurancePolicyService;
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
@RequestMapping("/api/policies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class InsurancePolicyController {

    private final InsurancePolicyService policyService;


    @GetMapping
    public ResponseEntity<List<InsurancePolicy>> getAllPolicies() {
        log.info("Запрос всех полисов");
        return ResponseEntity.ok(policyService.getAllPolicies());
    }


    @GetMapping("/{id}")
    public ResponseEntity<InsurancePolicy> getPolicyById(@PathVariable Long id) {
        log.info("Запрос полиса с ID: {}", id);
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }


    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<InsurancePolicy>> getPoliciesByClient(@PathVariable Long clientId) {
        log.info("Запрос полисов клиента с ID: {}", clientId);
        return ResponseEntity.ok(policyService.getPoliciesByClient(clientId));
    }


    @GetMapping("/manager/{managerId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<InsurancePolicy>> getPoliciesByManager(@PathVariable Long managerId) {
        log.info("Запрос полисов менеджера с ID: {}", managerId);
        return ResponseEntity.ok(policyService.getPoliciesByManager(managerId));
    }


    @GetMapping("/active")
    public ResponseEntity<List<InsurancePolicy>> getActivePolicies() {
        log.info("Запрос активных полисов");
        return ResponseEntity.ok(policyService.getActivePolicies());
    }


    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<InsurancePolicy>> getExpiringPolicies(
            @RequestParam(defaultValue = "30") int days) {
        log.info("Запрос полисов, истекающих в ближайшие {} дней", days);
        return ResponseEntity.ok(policyService.getExpiringPolicies(days));
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> createPolicy(@RequestBody InsurancePolicy policy) {
        log.info("Запрос на создание нового полиса");

        InsurancePolicy created = policyService.createPolicy(policy);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Полис успешно создан");
        response.put("policy", created);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<InsurancePolicy> updatePolicy(
            @PathVariable Long id,
            @RequestBody InsurancePolicy policy) {
        log.info("Запрос на обновление полиса с ID: {}", id);
        return ResponseEntity.ok(policyService.updatePolicy(id, policy));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deletePolicy(@PathVariable Long id) {
        log.info("Запрос на удаление полиса с ID: {}", id);

        policyService.deletePolicy(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Полис удалён");

        return ResponseEntity.ok(response);
    }


    @GetMapping("/stats/total-insured")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getTotalInsuredAmount() {
        BigDecimal total = policyService.getTotalInsuredAmount();

        Map<String, Object> response = new HashMap<>();
        response.put("totalInsuredAmount", total);

        return ResponseEntity.ok(response);
    }
}
