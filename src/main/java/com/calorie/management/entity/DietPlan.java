package com.calorie.management.entity;
import com.calorie.management.enums.CreatedByEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "diet_plans",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_user_date",
                        columnNames = {"user_id", "date"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietPlan {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_diet_plans_user")
    )
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "calorie_target", precision = 8, scale = 2)
    private BigDecimal calorieTarget;

    @Enumerated(EnumType.STRING)
    @Column(name = "created_by", nullable = false)
    private CreatedByEnum createdBy;
}

