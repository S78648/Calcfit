package com.calorie.management.repository;

import com.calorie.management.entity.UserTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserTargetRepository extends JpaRepository<UserTarget, UUID> {

}