package com.insurance.server.service;

import com.insurance.server.entity.InsuranceType;
import com.insurance.server.exception.ResourceNotFoundException;
import com.insurance.server.repository.InsuranceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InsuranceTypeService {

    private final InsuranceTypeRepository insuranceTypeRepository;

    public List<InsuranceType> getAllInsuranceTypes() {
        return insuranceTypeRepository.findAll();
    }

    public List<InsuranceType> getActiveInsuranceTypes() {
        return insuranceTypeRepository.findByIsActiveTypeTrue();
    }

    public InsuranceType getInsuranceTypeById(Long id) {
        return insuranceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Тип не найден"));
    }

    @Transactional
    public InsuranceType createInsuranceType(InsuranceType type) {
        type.setIsActiveType(true);
        return insuranceTypeRepository.save(type);
    }

    @Transactional
    public InsuranceType updateInsuranceType(Long id, InsuranceType updatedType) {
        InsuranceType type = getInsuranceTypeById(id);

        type.setTypeName(updatedType.getTypeName());
        type.setTypeDescription(updatedType.getTypeDescription());
        type.setBaseRatePercent(updatedType.getBaseRatePercent());

        return insuranceTypeRepository.save(type);
    }

    @Transactional
    public void deactivateInsuranceType(Long id) {
        InsuranceType type = getInsuranceTypeById(id);
        type.setIsActiveType(false);
        insuranceTypeRepository.save(type);
    }

    @Transactional
    public void deleteInsuranceType(Long id) {
        insuranceTypeRepository.deleteById(id);
    }
}