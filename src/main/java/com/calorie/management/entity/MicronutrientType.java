package com.calorie.management.entity;

import com.calorie.management.enums.MicronutrientCategoryEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Table(
        name = "micronutrient_types",
        uniqueConstraints = {
                @UniqueConstraint(name = "micronutrient_types_name_key", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MicronutrientType {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MicronutrientCategoryEnum category;
}

