package com.calorie.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "food_labels",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_label", columnNames = {"name", "category"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodLabel {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "is_system",nullable = false)
    @Builder.Default
    private Boolean isSystem = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Optional: Only add this if you NEED reverse navigation.
     * Otherwise, skip for simplicity.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "food_item_labels",
            joinColumns = @JoinColumn(name = "food_item_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<FoodLabel> labels = new HashSet<>();

}

