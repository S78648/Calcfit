package com.calorie.management.repository;

import com.calorie.management.entity.DietPlanMeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DietPlanMealRepository extends JpaRepository<DietPlanMeal, UUID> {
}