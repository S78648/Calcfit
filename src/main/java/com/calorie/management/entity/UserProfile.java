package com.calorie.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    private UUID userId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String fullName;

    private Integer age;

    @Column(nullable = false)
    private String gender;

    private BigDecimal heightCm;

    private BigDecimal weightKg;

    private String activityLevel;

    private String goalType;
}

