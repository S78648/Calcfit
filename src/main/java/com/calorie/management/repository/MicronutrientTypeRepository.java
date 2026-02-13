package com.calorie.management.repository;

import com.calorie.management.entity.MicronutrientType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MicronutrientTypeRepository extends JpaRepository<MicronutrientType, UUID> {
}