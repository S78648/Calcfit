package com.calorie.management.repository;

import com.calorie.management.entity.NutritionProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NutritionProfileRepository extends JpaRepository<NutritionProfile, UUID> {
}