package com.calorie.management.entity;

import com.calorie.management.enums.ActivityLevel;
import com.calorie.management.enums.Gender;
import com.calorie.management.enums.GoalType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "gender",nullable = false)
    private Gender gender;

    private BigDecimal heightCm;

    private BigDecimal weightKg;

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    /** Lose/gain only; null for maintenance (target = current weight). */
    private BigDecimal targetWeightKg;

    /** kg per week (0.25-1.0) for lose/gain; null -> goal's default +/-500 kcal/day. */
    private BigDecimal weeklyPaceKg;
}

