package com.calorie.management.entity.composite;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NutritionMicronutrientId implements Serializable {

    @Column(name = "nutrition_profile_id")
    private UUID nutritionProfileId;

    @Column(name = "micronutrient_type_id")
    private UUID micronutrientTypeId;
}

