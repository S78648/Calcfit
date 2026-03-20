package com.calorie.management.entity;
import com.calorie.management.enums.FoodTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "food_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItem {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;


    @Column(name = "serving_size", precision = 8, scale = 2)
    private BigDecimal servingSize;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "nutrition_profile_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_food_items_nutrition")
    )
    private NutritionProfile nutritionProfile;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}

