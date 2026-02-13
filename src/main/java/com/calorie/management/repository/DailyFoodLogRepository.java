package com.calorie.management.repository;

import com.calorie.management.entity.DailyFoodLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyFoodLogRepository extends JpaRepository<DailyFoodLog, Long> {
}