package com.calorie.management.repository;

import com.calorie.management.entity.RecipeDietaryLabel;
import com.calorie.management.entity.composite.RecipeDietaryLabelId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeDietaryLabelRepository extends JpaRepository<RecipeDietaryLabel, RecipeDietaryLabelId> {
}