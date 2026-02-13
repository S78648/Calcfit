package com.calorie.management.entity;
import com.calorie.management.entity.composite.NutritionMicronutrientId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(name = "nutrition_micronutrients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionMicronutrient {

    @EmbeddedId
    private NutritionMicronutrientId id;

    @MapsId("nutritionProfileId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "nutrition_profile_id",
            foreignKey = @ForeignKey(name = "fk_nm_profile")
    )
    private NutritionProfile nutritionProfile;

    @MapsId("micronutrientTypeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "micronutrient_type_id",
            foreignKey = @ForeignKey(name = "fk_nm_type")
    )
    private MicronutrientType micronutrientType;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal amount;
}

