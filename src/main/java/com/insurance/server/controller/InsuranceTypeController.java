package com.insurance.server.controller;

import com.insurance.server.entity.InsuranceType;
import com.insurance.server.service.InsuranceTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/insurance-types")
@RequiredArgsConstructor
@Slf4j
public class InsuranceTypeController {

    private final InsuranceTypeService insuranceTypeService;

    @GetMapping
    public ResponseEntity<List<InsuranceType>> getAllInsuranceTypes() {
        log.info("Получение списка типов страхования");
        return ResponseEntity.ok(insuranceTypeService.getAllInsuranceTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsuranceType> getInsuranceTypeById(@PathVariable Long id) {
        log.info("Получение типа страхования ID={}", id);
        return ResponseEntity.ok(insuranceTypeService.getInsuranceTypeById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InsuranceType> createInsuranceType(@RequestBody InsuranceType type) {
        log.info("Создание типа страхования: {}", type);
        return ResponseEntity.ok(insuranceTypeService.createInsuranceType(type));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InsuranceType> updateInsuranceType(
            @PathVariable Long id,
            @RequestBody InsuranceType updatedType
    ) {
        log.info("Обновление типа страхования ID={}", id);
        return ResponseEntity.ok(insuranceTypeService.updateInsuranceType(id, updatedType));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deactivateInsuranceType(@PathVariable Long id) {
        log.info("Деактивация типа страхования ID={}", id);
        insuranceTypeService.deactivateInsuranceType(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Тип страхования деактивирован");

        return ResponseEntity.ok(response);
    }
}