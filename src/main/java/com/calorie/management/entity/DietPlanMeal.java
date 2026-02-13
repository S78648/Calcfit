package com.calorie.management.entity;
import com.calorie.management.enums.MealTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "diet_plan_meals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietPlanMeal {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "diet_plan_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_diet_plan_meals_plan")
    )
    private DietPlan dietPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealTypeEnum mealType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "food_item_id",
            foreignKey = @ForeignKey(name = "fk_diet_plan_meals_food")
    )
    private FoodItem foodItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "recipe_id",
            foreignKey = @ForeignKey(name = "fk_diet_plan_meals_recipe")
    )
    private Recipe recipe;

    @Column(name = "suggested_quantity",
            nullable = false,
            precision = 8,
            scale = 2)
    private BigDecimal suggestedQuantity;
}

