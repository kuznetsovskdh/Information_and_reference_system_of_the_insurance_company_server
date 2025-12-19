package com.insurance.server.repository;

import com.insurance.server.entity.InsuranceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface InsuranceTypeRepository extends JpaRepository<InsuranceType, Long> {


    List<InsuranceType> findByIsActiveTypeTrue();


    Optional<InsuranceType> findByTypeName(String typeName);


    boolean existsByTypeName(String typeName);
}
