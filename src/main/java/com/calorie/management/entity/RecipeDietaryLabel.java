package com.calorie.management.entity;

import com.calorie.management.entity.composite.RecipeDietaryLabelId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipe_dietary_labels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDietaryLabel {

    @EmbeddedId
    private RecipeDietaryLabelId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recipeId")
    @JoinColumn(
            name = "recipe_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_recipe_diet_label_recipe")
    )
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dietaryLabelId")
    @JoinColumn(
            name = "dietary_label_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_recipe_diet_label_label")
    )
    private DietaryLabel dietaryLabel;
}

