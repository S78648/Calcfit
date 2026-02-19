package com.calorie.management.entity.composite;

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
public class RecipeDietaryLabelId implements Serializable {

    private UUID recipeId;
    private UUID dietaryLabelId;
}

