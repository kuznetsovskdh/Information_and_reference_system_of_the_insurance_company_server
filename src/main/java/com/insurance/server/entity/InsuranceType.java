package com.insurance.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "insurance_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long insuranceTypeId;

    @Column(unique = true, nullable = false, length = 100)
    private String typeName;

    @Column(name = "type_description", columnDefinition = "TEXT")
    private String typeDescription;

    @Column(name = "base_rate_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal baseRatePercent;

    @Column(name = "is_active_type")
    private Boolean isActiveType = true;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}
