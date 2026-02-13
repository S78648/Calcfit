package com.calorie.management.repository;

import com.calorie.management.entity.NutritionMacros;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NutritionMacrosRepository extends JpaRepository<NutritionMacros, UUID> {
}