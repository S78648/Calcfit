package com.calorie.management.repository;

import com.calorie.management.entity.NutritionMicronutrient;
import com.calorie.management.entity.composite.NutritionMicronutrientId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NutritionMicronutrientRepository extends JpaRepository<NutritionMicronutrient, NutritionMicronutrientId> {
}