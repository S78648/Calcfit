package com.calorie.management.repository;

import com.calorie.management.entity.RecipeIngredient;
import com.calorie.management.entity.composite.RecipeIngredientId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, RecipeIngredientId> {
}