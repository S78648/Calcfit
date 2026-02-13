package com.calorie.management.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 100)
    private String category;

    @Column(name = "default_unit", length = 20)
    private String defaultUnit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "nutrition_profile_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ingredients_nutrition")
    )
    private NutritionProfile nutritionProfile;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}

