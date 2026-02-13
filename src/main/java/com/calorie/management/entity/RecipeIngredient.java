package com.calorie.management.entity;
import com.calorie.management.entity.composite.RecipeIngredientId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(name = "recipe_ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredient {

    @EmbeddedId
    private RecipeIngredientId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("recipeId")
    @JoinColumn(
            name = "recipe_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_recipe_ingredients_recipe")
    )
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("ingredientId")
    @JoinColumn(
            name = "ingredient_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_recipe_ingredients_ingredient")
    )
    private Ingredient ingredient;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal quantity;
}

