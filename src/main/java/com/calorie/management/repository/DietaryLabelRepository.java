package com.calorie.management.repository;

import com.calorie.management.entity.DietaryLabel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DietaryLabelRepository extends JpaRepository<DietaryLabel, UUID> {
}