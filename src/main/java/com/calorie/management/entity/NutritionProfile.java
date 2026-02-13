package com.calorie.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;




@Entity
@Table(name = "nutrition_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionProfile {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "reference_quantity", nullable = false, precision = 8, scale = 2)
    private BigDecimal referenceQuantity;

    @Column(name = "reference_unit", nullable = false)
    private String referenceUnit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ==============================
    // RELATIONSHIPS
    // ==============================

    // 1:1 with NutritionMacros
    @OneToOne(
            mappedBy = "nutritionProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private NutritionMacros macros;

    // 1:N with NutritionMicronutrients
    @OneToMany(
            mappedBy = "nutritionProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<NutritionMicronutrient> micronutrients = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

