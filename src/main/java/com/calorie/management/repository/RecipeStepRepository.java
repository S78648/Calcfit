package com.calorie.management.repository;

import com.calorie.management.entity.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, UUID> {
}