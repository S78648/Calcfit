package com.calorie.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;



@Entity
@Table(name = "nutrition_macros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionMacros {

    @Id
    @Column(name = "nutrition_profile_id")
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "nutrition_profile_id",
            foreignKey = @ForeignKey(name = "fk_macros_profile")
    )
    private NutritionProfile nutritionProfile;

    @Column(name = "calories_kcal", nullable = false, precision = 8, scale = 2)
    private BigDecimal caloriesKcal;

    @Column(name = "protein_g", nullable = false, precision = 8, scale = 2)
    private BigDecimal proteinG;

    @Column(name = "carbs_g", nullable = false, precision = 8, scale = 2)
    private BigDecimal carbsG;

    @Column(name = "fat_g", nullable = false, precision = 8, scale = 2)
    private BigDecimal fatG;

    @Column(name = "fiber_g", nullable = false, precision = 8, scale = 2)
    private BigDecimal fiberG;
}

