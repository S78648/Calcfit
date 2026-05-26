package com.calorie.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_targets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTarget {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "target_calories", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetCalories;

    @Column(name = "target_protein_grams", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetProteinGrams;

    @Column(name = "target_carbs_grams", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetCarbsGrams;

    @Column(name = "target_fat_grams", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetFatGrams;

    @Column(name = "target_fiber_grams", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetFiberGrams;

    @Column(name = "target_water_ml", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetWaterMl;

    @Column(name = "bmr", precision = 10, scale = 2)
    private BigDecimal bmr;

    @Column(name = "tdee", precision = 10, scale = 2)
    private BigDecimal tdee;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        calculatedAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculatedAt = LocalDateTime.now();
    }
}
