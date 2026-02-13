package com.calorie.management.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "daily_food_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyFoodLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_daily_logs_user")
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "food_item_id",
            foreignKey = @ForeignKey(name = "fk_daily_logs_food")
    )
    private FoodItem foodItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "recipe_id",
            foreignKey = @ForeignKey(name = "fk_daily_logs_recipe")
    )
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "nutrition_profile_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_daily_logs_nutrition")
    )
    private NutritionProfile nutritionProfile;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    // Snapshot columns
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal calories;

    @Column(name = "protein_g", nullable = false, precision = 10, scale = 2)
    private BigDecimal proteinG;

    @Column(name = "carbs_g", nullable = false, precision = 10, scale = 2)
    private BigDecimal carbsG;

    @Column(name = "fat_g", nullable = false, precision = 10, scale = 2)
    private BigDecimal fatG;

    @Column(name = "fiber_g", nullable = false, precision = 10, scale = 2)
    private BigDecimal fiberG;

    @Column(name = "nutrition_snapshot", columnDefinition = "jsonb", nullable = false)
    private String nutritionSnapshot;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

