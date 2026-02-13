package com.calorie.management.repository;

import com.calorie.management.entity.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DietPlanRepository extends JpaRepository<DietPlan, UUID> {
}