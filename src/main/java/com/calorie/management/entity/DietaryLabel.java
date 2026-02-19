package com.calorie.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "dietary_labels",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_dietary_label_name", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietaryLabel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "is_system_generated", nullable = false)
    @Builder.Default
    private Boolean isSystemGenerated = true;

    @OneToMany(
            mappedBy = "dietaryLabel",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<RecipeDietaryLabel> recipes;
}

