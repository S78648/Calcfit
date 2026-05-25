CREATE TABLE user_targets (
    user_id UUID PRIMARY KEY,

    target_calories DECIMAL(10,2) NOT NULL,
    target_protein_grams DECIMAL(10,2) NOT NULL,
    target_carbs_grams DECIMAL(10,2) NOT NULL,
    target_fat_grams DECIMAL(10,2) NOT NULL,

    target_fiber_grams DECIMAL(10,2) NOT NULL,
    target_water_ml DECIMAL(10,2) NOT NULL,

    bmr DECIMAL(10,2),
    tdee DECIMAL(10,2),

    calculated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_targets_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_user_targets_calculated_at
ON user_targets(calculated_at);